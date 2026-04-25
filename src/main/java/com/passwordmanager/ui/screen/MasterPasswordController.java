package com.passwordmanager.ui.screen;

import com.passwordmanager.crypto.CryptoService;
import com.passwordmanager.storage.StoragePathResolver;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.crypto.AEADBadTagException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class MasterPasswordController {

    @FXML
    private VBox rootPane;
    @FXML
    private Label subtitleLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private VBox confirmRow;
    @FXML
    private PasswordField confirmField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button submitButton;

    private final boolean firstRun = !Files.exists(StoragePathResolver.credentialsFilePath());
    private CryptoService cryptoService;
    private double dragOffsetX, dragOffsetY;

    @FXML
    private void initialize() {
        if (firstRun) {
            subtitleLabel.setText("Set a master password to protect your credentials.");
            submitButton.setText("Create vault");
        } else {
            subtitleLabel.setText("Enter your master password to unlock.");
            confirmRow.setVisible(false);
            confirmRow.setManaged(false);
            submitButton.setText("Unlock");
        }
        passwordField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) handleSubmit(); });
        confirmField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) handleSubmit(); });

        rootPane.setOnMousePressed(e -> {
            dragOffsetX = e.getSceneX();
            dragOffsetY = e.getSceneY();
        });
        rootPane.setOnMouseDragged(e -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setX(e.getScreenX() - dragOffsetX);
            stage.setY(e.getScreenY() - dragOffsetY);
        });
    }

    @FXML
    public void handleSubmit() {
        errorLabel.setText("");
        // PasswordField.getText() returns String — toCharArray() limits exposure window
        char[] password = passwordField.getText().toCharArray();

        if (password.length == 0) {
            errorLabel.setText("Password cannot be empty.");
            Arrays.fill(password, '\0');
            return;
        }

        if (firstRun) {
            handleCreate(password);
        } else {
            handleUnlock(password);
        }
    }

    private void handleCreate(char[] password) {
        char[] confirm = confirmField.getText().toCharArray();
        try {
            if (password.length < 8) {
                errorLabel.setText("Password must be at least 8 characters.");
                return;
            }
            if (!Arrays.equals(password, confirm)) {
                errorLabel.setText("Passwords do not match.");
                confirmField.clear();
                return;
            }
            cryptoService = CryptoService.withNewSalt(password);
            close();
        } catch (GeneralSecurityException e) {
            errorLabel.setText("Failed to create vault: " + e.getMessage());
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirm, '\0');
        }
    }

    private void handleUnlock(char[] password) {
        try {
            byte[] fileBytes = Files.readAllBytes(StoragePathResolver.credentialsFilePath());
            if (fileBytes.length < CryptoService.SALT_LENGTH) {
                errorLabel.setText("Vault file is corrupted.");
                return;
            }
            byte[] salt = Arrays.copyOf(fileBytes, CryptoService.SALT_LENGTH);
            CryptoService candidate = CryptoService.withSalt(password, salt);
            candidate.decrypt(fileBytes); // verify — throws AEADBadTagException if wrong
            cryptoService = candidate;
            close();
        } catch (AEADBadTagException e) {
            errorLabel.setText("Wrong password.");
            passwordField.clear();
        } catch (GeneralSecurityException | IOException e) {
            errorLabel.setText("Failed to unlock: " + e.getMessage());
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void close() {
        passwordField.getScene().getWindow().hide();
    }

    /** Returns the CryptoService after successful unlock, or {@code null} if cancelled. */
    public CryptoService getCryptoService() {
        return cryptoService;
    }
}
