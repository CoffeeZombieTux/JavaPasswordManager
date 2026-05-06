package com.passwordmanager.model;

import org.jetbrains.annotations.Nullable;

public final class CredentialFilter {

    public static final String ALL_CATEGORIES = "All";

    private final @Nullable CredentialType type;
    private final @Nullable String category;
    private final @Nullable String searchInput;

    public CredentialFilter() {
        this.type = CredentialType.ACCOUNT;
        this.category = ALL_CATEGORIES;
        this.searchInput = null;
    }

    public CredentialFilter(@Nullable String searchInput) {
        this.type = null;
        this.category = null;
        this.searchInput = searchInput;
    }

    public CredentialFilter(@Nullable String searchInput, @Nullable String category, @Nullable CredentialType type) {
        this.type = type;
        this.category = category;
        this.searchInput = searchInput;
    }

    public @Nullable String getCategory() {
        return category;
    }

    public @Nullable String getSearchInput() {
        return searchInput;
    }

    public @Nullable CredentialType getType() {
        return type;
    }

    public CredentialFilter withType(@Nullable CredentialType type) {
        return new CredentialFilter(searchInput, category, type);
    }

    public CredentialFilter withCategory(@Nullable String category) {
        return new CredentialFilter(searchInput, category, type);
    }
}
