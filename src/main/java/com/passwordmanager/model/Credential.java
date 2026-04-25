package com.passwordmanager.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public class Credential {

    public enum CredentialType {
        ACCOUNT,
        TOKEN,
        NOTE
    }

    private final UUID id;
    private final CredentialType type;
    private final String name;
    private final @Nullable String username;
    private final String password;
    private final @Nullable String website;
    private final @Nullable String category;
    private final @Nullable String notes;
    private final Instant createdAt;
    private final Instant updatedAt;

    @JsonCreator
    public Credential(
            @JsonProperty("id") @NotNull UUID id,
            @JsonProperty("type") @NotNull CredentialType type,
            @JsonProperty("name") @NotNull String name,
            @JsonProperty("username") @Nullable String username,
            @JsonProperty("password") @NotNull String password,
            @JsonProperty("website") @Nullable String website,
            @JsonProperty("category") @Nullable String category,
            @JsonProperty("notes") @Nullable String notes,
            @JsonProperty("createdAt") @NotNull Instant createdAt,
            @JsonProperty("updatedAt") @NotNull Instant updatedAt
    ) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.username = username;
        this.password = password;
        this.website = website;
        this.category = category;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public @NotNull UUID getId() {
        return id;
    }

    public @NotNull CredentialType getType() {
        return type;
    }

    public @NotNull String getName() {
        return name;
    }

    public @Nullable String getUsername() {
        return username;
    }

    public @NotNull String getPassword() {
        return password;
    }

    public @Nullable String getWebsite() {
        return website;
    }

    public @Nullable String getCategory() {
        return category;
    }

    public @Nullable String getNotes() {
        return notes;
    }

    public @NotNull Instant getCreatedAt() {
        return createdAt;
    }

    public @NotNull Instant getUpdatedAt() {
        return updatedAt;
    }
}
