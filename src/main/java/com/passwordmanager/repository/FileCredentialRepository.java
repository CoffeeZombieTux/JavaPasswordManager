package com.passwordmanager.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.passwordmanager.model.Credential;
import com.passwordmanager.storage.StoragePathResolver;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FileCredentialRepository implements CredentialRepository {

    private final ObjectMapper objectMapper;
    private final Path filePath;
    private final List<Credential> data;

    public FileCredentialRepository() {
        this.filePath = StoragePathResolver.credentialsFilePath();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.data = new ArrayList<>(loadOrCreate());
    }

    @Override
    public synchronized List<Credential> findAll() {
        return List.copyOf(data);
    }

    @Override
    public synchronized Optional<Credential> findById(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        return data.stream()
                .filter(credential -> credential.getId().equals(id))
                .findFirst();
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
    public synchronized boolean update(Credential updated) {
        Objects.requireNonNull(updated, "updated must not be null");
        UUID id = Objects.requireNonNull(updated.getId(), "id must not be null");

        for (int i = 0; i < data.size(); i++) {
            Credential current = data.get(i);
            if (current.getId().equals(id)) {
                data.set(i, updated);
                writeToDisk(data);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean deleteById(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        boolean removed = data.removeIf(credential -> credential.getId().equals(id));
        if (removed) {
            writeToDisk(data);
            return true;
        }
        return false;
    }

    private List<Credential> loadOrCreate() {
        if (Files.exists(filePath)) {
            try {
                return objectMapper.readValue(filePath.toFile(), new TypeReference<>() {
                });
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to read credentials: " + filePath, e);
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

            Path tempFile = filePath.resolveSibling(filePath.getFileName() + ".tmp");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(tempFile.toFile(), credentials);
            try {
                Files.move(
                        tempFile,
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (IOException atomicMoveFailure) {
                Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write credentials: " + filePath, e);
        }
    }
}
