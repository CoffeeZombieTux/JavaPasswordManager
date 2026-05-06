package com.passwordmanager.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.passwordmanager.crypto.CryptoService;
import com.passwordmanager.model.Credential;
import com.passwordmanager.model.CredentialFilter;
import com.passwordmanager.storage.StoragePathResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileCredentialRepository implements CredentialRepository {

    private final ObjectMapper objectMapper;
    private final Path filePath;
    private final CryptoService cryptoService;
    private final List<Credential> data;

    public FileCredentialRepository(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
        filePath = StoragePathResolver.credentialsFilePath();
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        data = new ArrayList<>(loadOrCreate());
    }

    @Override
    public synchronized List<Credential> filter(CredentialFilter filters) {
        return data.stream()
                .filter(c -> matchesSearch(c, filters.getSearchInput()))
                .filter(c -> filters.getType() == null || c.type() == filters.getType())
                .filter(c -> matchesCategory(c, filters.getCategory()))
                .toList();
    }

    @Override
    public synchronized Credential add(Credential credential) {
        Objects.requireNonNull(credential, "credential must not be null");
        UUID id = Objects.requireNonNull(credential.id(), "credential.id must not be null");

        boolean exists = data.stream().anyMatch(c -> c.id().equals(id));
        if (exists) {
            throw new IllegalArgumentException("Credential with id already exists: " + id);
        }

        data.add(credential);
        writeToDisk(data);
        return credential;
    }

    @Override
    public synchronized Credential update(Credential updated) {
        Objects.requireNonNull(updated, "updated must not be null");
        UUID id = Objects.requireNonNull(updated.id(), "id must not be null");

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).id().equals(id)) {
                data.set(i, updated);
                writeToDisk(data);
                return updated;
            }
        }
        throw new IllegalArgumentException("Credential with id does not exist: " + id);
    }

    @Override
    public synchronized void deleteById(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        boolean removed = data.removeIf(c -> c.id().equals(id));
        if (removed) {
            writeToDisk(data);
            return;
        }
        throw new IllegalArgumentException("Credential with id does not exist: " + id);
    }

    private boolean matchesSearch(Credential c, String search) {
        if (search == null || search.isBlank()) return true;
        String q = search.toLowerCase();
        if (c.name().toLowerCase().contains(q)) return true;
        if (c.username() != null && c.username().toLowerCase().contains(q)) return true;
        if (c.website() != null && c.website().toLowerCase().contains(q)) return true;
        return c.notes() != null && c.notes().toLowerCase().contains(q);
    }

    private boolean matchesCategory(Credential c, String category) {
        if (category == null || category.isBlank() || category.equals(CredentialFilter.ALL_CATEGORIES)) return true;
        if (c.category() == null || c.category().isBlank()) return false;
        return c.category().equals(category);
    }

    private List<Credential> loadOrCreate() {
        if (Files.exists(filePath)) {
            try {
                byte[] encrypted = Files.readAllBytes(filePath);
                byte[] json = cryptoService.decrypt(encrypted);
                return objectMapper.readValue(json, new TypeReference<>() {});
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to read credentials: " + filePath, e);
            } catch (GeneralSecurityException e) {
                throw new VaultException("Failed to decrypt credentials: " + filePath, e);
            }
        }
        writeToDisk(List.of());
        return List.of();
    }

    private void writeToDisk(List<Credential> credentials) {
        Path tempFile = filePath.resolveSibling(filePath.getFileName() + ".tmp");
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(credentials);
            byte[] encrypted = cryptoService.encrypt(json);

            Files.write(tempFile, encrypted);
            moveTmpFile(tempFile, filePath);
        } catch (IOException e) {
            deleteSilently(tempFile);
            throw new UncheckedIOException("Failed to write credentials: " + filePath, e);
        } catch (GeneralSecurityException e) {
            deleteSilently(tempFile);
            throw new VaultException("Failed to encrypt credentials: " + filePath, e);
        }
    }

    private static void moveTmpFile(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicMoveFailure) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteSilently(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // best-effort cleanup — if the temp file can't be deleted, the OS will reclaim it eventually
        }
    }
}
