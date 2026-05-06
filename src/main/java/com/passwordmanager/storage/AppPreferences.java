package com.passwordmanager.storage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppPreferences {

    private String lastExportDirectory;

    public String getLastExportDirectory() {
        return lastExportDirectory;
    }

    public void setLastExportDirectory(String lastExportDirectory) {
        this.lastExportDirectory = lastExportDirectory;
    }
}
