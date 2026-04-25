package com.passwordmanager.repository;

import com.passwordmanager.model.Credential;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CredentialRepository {

    /**
     * Returns all stored credentials.
     *
     * @return unmodifiable list of credentials, never {@code null}
     */
    List<Credential> findAll();

    /**
     * Returns all stored credentials of the given type.
     *
     * @param type the credential type to filter by, must not be {@code null}
     * @return unmodifiable list of credentials matching {@code type}, never {@code null}
     */
    List<Credential> findAll(Credential.CredentialType type);

    /**
     * Returns stored credentials by category and type.
     *
     * @return unmodifiable list of credentials, never {@code null}
     */
    List<Credential> filterByCategoryAndType(Credential.CredentialType type, String category);

    /**
     * Returns a list of unique categories for selected type.
     *
     * @return unmodifiable list of categories, never {@code null}
     */
    List<String> findAllCategories(Credential.CredentialType type, String defaultCategory);

    /**
     * Persists a new credential.
     *
     * @param credential the credential to add, must not be {@code null}
     * @return the added credential
     * @throws IllegalArgumentException if a credential with the same id already exists
     */
    Credential add(Credential credential);

    /**
     * Updates an existing credential.
     *
     * @param credential the credential to update, must not be {@code null}
     * @return the updated credential
     * @throws IllegalArgumentException if no credential with the given id exists
     */
    Credential update(Credential credential);

    /**
     * Deletes the credential with the given id.
     *
     * @param id the id of the credential to delete, must not be {@code null}
     * @throws IllegalArgumentException if no credential with the given id exists
     */
    void deleteById(UUID id);
}
