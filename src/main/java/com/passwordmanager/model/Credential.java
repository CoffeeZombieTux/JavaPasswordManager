package com.passwordmanager.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public class Credential {

    private final UUID id;
    private final String title;
    private final String username;
    private final String password;
    private final String website;
    private final String category;
    private final @Nullable String notes;
    private final Instant createdAt;
    private final Instant updatedAt;

    @JsonCreator
    public Credential(
            @JsonProperty("id") UUID id,
            @JsonProperty("title") String title,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("website") String website,
            @JsonProperty("category") String category,
            @JsonProperty("notes") @Nullable String notes,
            @JsonProperty("createdAt") Instant createdAt,
            @JsonProperty("updatedAt") Instant updatedAt
    ) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.password = password;
        this.website = website;
        this.category = category;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getWebsite() {
        return website;
    }

    public String getCategory() {
        return category;
    }

    public @Nullable String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }
}
