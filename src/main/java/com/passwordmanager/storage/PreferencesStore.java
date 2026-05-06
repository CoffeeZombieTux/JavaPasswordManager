package com.passwordmanager.storage;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PreferencesStore {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private PreferencesStore() {}

    public static AppPreferences load() {
        Path path = StoragePathResolver.preferencesFilePath();
        if (Files.exists(path)) {
            try {
                return MAPPER.readValue(path.toFile(), AppPreferences.class);
            } catch (IOException ignored) {
                // Corrupted or unreadable prefs — fall back to defaults silently
            }
        }
        return new AppPreferences();
    }

    public static void save(AppPreferences prefs) {
        Path path = StoragePathResolver.preferencesFilePath();
        try {
            Path parent = path.getParent();
            if (parent != null) Files.createDirectories(parent);
            MAPPER.writeValue(path.toFile(), prefs);
        } catch (IOException ignored) {
            // Best-effort save — if prefs can't be written, the app continues without persisting them
        }
    }
}
