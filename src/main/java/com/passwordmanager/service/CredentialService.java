package com.passwordmanager.service;

import com.passwordmanager.repository.CredentialRepository;
import com.passwordmanager.model.Credential;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
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
    public List<Credential> findAll() {
        return repository.findAll();
    }

    public List<Credential> filterByCategory(String category) {
        if (Objects.equals(category, ALL_CATEGORIES)) {
            return findAll();
        }
        return repository.filterByCategory(category);
    }

    /**
     * Returns a list of unique categories.
     *
     * @return unmodifiable list of categories, never {@code null}
     */
    public List<String> findAllCategories() {
        return repository.findAllCategories(ALL_CATEGORIES);
    }

    /**
     * Creates or updates a credential.
     * <p>
     * If {@code id} is {@code null}, a new credential is created with a generated id
     * and {@code createdAt} set to the current time. Otherwise, the existing credential
     * is updated with a new {@code updatedAt} timestamp.
     *
     * @param id        the id of the credential to update, or {@code null} to create a new one
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
                name,
                username,
                password,
                website,
                category,
                notes,
                createNew ? Instant.now() : createdAt,
                Instant.now()
        );
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
}
