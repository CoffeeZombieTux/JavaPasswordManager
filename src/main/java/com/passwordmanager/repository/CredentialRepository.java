package com.passwordmanager.repository;

import com.passwordmanager.model.Credential;
import com.passwordmanager.model.CredentialFilter;

import java.util.List;
import java.util.UUID;

public interface CredentialRepository {

    /**
     * Returns credentials matching all non-null fields in {@code filters}.
     *
     * @param filters the filter criteria, must not be {@code null}
     * @return unmodifiable list of matching credentials, never {@code null}
     */
    List<Credential> filter(CredentialFilter filters);

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
