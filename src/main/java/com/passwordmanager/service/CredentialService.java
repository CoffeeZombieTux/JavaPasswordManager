package com.passwordmanager.service;

import com.passwordmanager.repository.CredentialRepository;
import com.passwordmanager.model.Credential;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CredentialService {
    public static final String ALL_CATEGORIES = "All";

    private final CredentialRepository repository;

    public CredentialService(CredentialRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns all stored credentials.
     *
     * @return unmodifiable list of credentials, never {@code null}
     */
    public List<Credential> findAll(Credential.CredentialType type) {
        return repository.findAll(type);
    }

    /**
     * Returns all stored credentials.
     *
     * @return unmodifiable list of credentials, never {@code null}
     */
    public Map<Credential.CredentialType, List<Credential>> findAll() {
        Map<Credential.CredentialType, List<Credential>> data = new java.util.HashMap<>(Map.of());
        for (Credential.CredentialType type :  Credential.CredentialType.values()) {
            data.put(type, repository.findAll(type));
        }
        return data;
    }


    public List<Credential> filterByCategory(String category, Credential.CredentialType type) {
        if (Objects.equals(category, ALL_CATEGORIES)) {
            return findAll(type);
        }
        return repository.filterByCategoryAndType(type, category);
    }

    /**
     * Returns a list of unique categories in selected type.
     *
     * @return unmodifiable list of categories, never {@code null}
     */
    public List<String> findAllCategories(Credential.CredentialType type) {
        return repository.findAllCategories(type, ALL_CATEGORIES);
    }

    /**
     * Creates or updates a credential.
     * <p>
     * If {@code id} is {@code null}, a new credential is created with a generated id
     * and {@code createdAt} set to the current time. Otherwise, the existing credential
     * is updated with a new {@code updatedAt} timestamp.
     *
     * @param id        the id of the credential to update, or {@code null} to create a new one
     * @param type      the credential type
     * @param name      the credential name
     * @param username  the username
     * @param password  the password
     * @param website   the associated website
     * @param category  the category
     * @param notes     optional notes, may be {@code null}
     * @param createdAt the original creation time; ignored when creating a new credential
     * @return the saved credential
     */
    public Credential save(
            UUID id,
            Credential.CredentialType type,
            String name,
            String username,
            String password,
            String website,
            String category,
            String notes,
            Instant createdAt
    ) {
        boolean createNew = id == null;
        if (createNew) {
            id = UUID.randomUUID();
        }
        Credential c = new Credential(
                id,
                type,
                name,
                username,
                password,
                website,
                category,
                notes,
                createNew ? Instant.now() : createdAt,
                Instant.now()
        );
        this.validate(c);
        return createNew ? repository.add(c) : repository.update(c);
    }

    /**
     * Deletes the credential with the given id.
     *
     * @param id the id of the credential to delete, must not be {@code null}
     */
    public void delete(@NotNull UUID id) {
        repository.deleteById(id);
    }

    /**
     * Note requires name and notes
     * Token requires name and token (password field)
     * Account requires name, username and password
     *
     * @param credential object to validate
     */
    private void validate(Credential credential) {
        if (credential.getName() == null || credential.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (credential.getType() == Credential.CredentialType.NOTE) {
            if (credential.getNotes() == null || credential.getNotes().isBlank()) {
                throw new IllegalArgumentException("Notes is required");
            }
            return;
        }

        if (credential.getPassword() == null || credential.getPassword().isBlank()) {
            String field = credential.getType() == Credential.CredentialType.ACCOUNT ? "Password" : "Token";
            throw new IllegalArgumentException(field + " is required");
        }

        if (credential.getType() == Credential.CredentialType.TOKEN) {
            return;
        }

        if (credential.getUsername() == null || credential.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
    }
}
