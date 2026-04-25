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
                .filter(c -> filters.getType() == null || c.getType() == filters.getType())
                .filter(c -> matchesCategory(c, filters.getCategory()))
                .toList();
    }

    @Override
    public synchronized Credential add(Credential credential) {
        Objects.requireNonNull(credential, "credential must not be null");
        UUID id = Objects.requireNonNull(credential.getId(), "credential.id must not be null");

        boolean exists = data.stream().anyMatch(c -> c.getId().equals(id));
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
        UUID id = Objects.requireNonNull(updated.getId(), "id must not be null");

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(id)) {
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
        boolean removed = data.removeIf(c -> c.getId().equals(id));
        if (removed) {
            writeToDisk(data);
            return;
        }
        throw new IllegalArgumentException("Credential with id does not exist: " + id);
    }

    private boolean matchesSearch(Credential c, String search) {
        if (search == null || search.isBlank()) return true;
        String q = search.toLowerCase();
        if (c.getName().toLowerCase().contains(q)) return true;
        if (c.getUsername() != null && c.getUsername().toLowerCase().contains(q)) return true;
        if (c.getWebsite() != null && c.getWebsite().toLowerCase().contains(q)) return true;
        return c.getNotes() != null && c.getNotes().toLowerCase().contains(q);
    }

    private boolean matchesCategory(Credential c, String category) {
        if (category == null || category.isBlank() || category.equals(CredentialFilter.ALL_CATEGORIES)) return true;
        if (c.getCategory() == null || c.getCategory().isBlank()) return false;
        return c.getCategory().equals(category);
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
                throw new RuntimeException("Failed to decrypt credentials: " + filePath, e);
            }
        }
        List<Credential> empty = List.of();
        writeToDisk(empty);
        return empty;
    }

    private void writeToDisk(List<Credential> credentials) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(credentials);
            byte[] encrypted = cryptoService.encrypt(json);

            Path tempFile = filePath.resolveSibling(filePath.getFileName() + ".tmp");
            Files.write(tempFile, encrypted);
            try {
                Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicMoveFailure) {
                Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write credentials: " + filePath, e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to encrypt credentials: " + filePath, e);
        }
    }
}
