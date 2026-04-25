package com.passwordmanager.storage;

import java.nio.file.Path;

public final class StoragePathResolver {

    private static final String APP_DIR_NAME = "PasswordManager";
    private static final String FILE_NAME = "credentials.vault";

    private StoragePathResolver() {
    }

    public static Path credentialsFilePath() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        String userHome = System.getProperty("user.home", ".");

        Path basePath;
        if (osName.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isBlank()) {
                basePath = Path.of(appData);
            } else {
                basePath = Path.of(userHome, "AppData", "Roaming");
            }
        } else if (osName.contains("mac")) {
            basePath = Path.of(userHome, "Library", "Application Support");
        } else {
            basePath = Path.of(userHome, ".local", "share");
        }

        return basePath.resolve(APP_DIR_NAME).resolve(FILE_NAME);
    }
}
