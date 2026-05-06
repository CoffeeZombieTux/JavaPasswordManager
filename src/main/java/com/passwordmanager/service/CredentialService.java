package com.passwordmanager.service;

import com.passwordmanager.model.Credential;
import com.passwordmanager.model.CredentialFilter;
import com.passwordmanager.model.CredentialType;
import com.passwordmanager.repository.CredentialRepository;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;

public class CredentialService {

    public static final String ALL_CATEGORIES = CredentialFilter.ALL_CATEGORIES;

    private final CredentialRepository repository;

    public CredentialService(CredentialRepository repository) {
        this.repository = repository;
    }

    public List<Credential> getFilteredData(CredentialFilter filter) {
        return repository.filter(filter);
    }

    public Map<CredentialType, Integer> getTotals(CredentialFilter filter) {
        List<Credential> data = repository.filter(new CredentialFilter(filter.getSearchInput()));
        Map<CredentialType, Integer> totals = new EnumMap<>(CredentialType.class);
        for (CredentialType type : CredentialType.values()) totals.put(type, 0);
        data.forEach(c -> totals.merge(c.type(), 1, Integer::sum));
        return totals;
    }


    /**
     * Returns a list of unique categories in selected data.
     *
     * @return unmodifiable list of categories, never {@code null}
     */
    public List<String> getCategories(CredentialFilter filter) {
        List<Credential> data = repository.filter(new CredentialFilter(filter.getSearchInput()));
        List<String> categories = new ArrayList<>(data.stream()
                .filter(credential -> credential.type().equals(filter.getType()))
                .map(Credential::category)
                .filter(category -> category != null && !category.isBlank())
                .distinct()
                .sorted()
                .toList());
        categories.addFirst(ALL_CATEGORIES);
        return List.copyOf(categories);
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
    @SuppressWarnings("java:S107")
    public Credential save(
            UUID id,
            CredentialType type,
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
        validate(c);
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
        if (credential.name().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (credential.type() == CredentialType.NOTE) {
            if (credential.notes() == null || credential.notes().isBlank()) {
                throw new IllegalArgumentException("Notes is required");
            }
            return;
        }

        if (credential.password().isBlank()) {
            String field = credential.type() == CredentialType.ACCOUNT ? "Password" : "Token";
            throw new IllegalArgumentException(field + " is required");
        }

        if (credential.type() == CredentialType.TOKEN) {
            return;
        }

        if (credential.username() == null || credential.username().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
    }
}
