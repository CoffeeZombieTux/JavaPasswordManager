package com.passwordmanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public record Credential(
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
) {}
