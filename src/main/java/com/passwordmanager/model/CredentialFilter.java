package com.passwordmanager.model;

import org.jetbrains.annotations.Nullable;

public class CredentialFilter {

    public static final String ALL_CATEGORIES = "All";

    private Credential.CredentialType type;
    private @Nullable String category;
    private final @Nullable String searchInput;

    public CredentialFilter() {
        type = Credential.CredentialType.ACCOUNT;
        category = ALL_CATEGORIES;
        searchInput = null;
    }

    public CredentialFilter(@Nullable String searchInput) {
        type = null;
        category = null;
        this.searchInput = searchInput;
    }

    public @Nullable String getCategory() {
        return category;
    }

    public void setCategory(@Nullable String category) {
        this.category = category;
    }

    public @Nullable String getSearchInput() {
        return searchInput;
    }

    public Credential.CredentialType getType() {
        return type;
    }

    public void setType(Credential.CredentialType type) {
        this.type = type;
    }
}
