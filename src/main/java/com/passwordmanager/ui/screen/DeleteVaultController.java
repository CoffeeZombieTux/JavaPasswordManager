package com.passwordmanager.ui.screen;

import com.passwordmanager.crypto.CryptoService;
import com.passwordmanager.storage.StoragePathResolver;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.crypto.AEADBadTagException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class DeleteVaultController {

    @FXML
    private VBox rootPane;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label capsLockLabel;
    @FXML
    private Label errorLabel;

    private boolean deleted = false;
    private double dragOffsetX;
    private double dragOffsetY;

    @FXML
    private void initialize() {
        capsLockLabel.setVisible(false);
        capsLockLabel.setManaged(false);

        passwordField.setOnKeyReleased(e -> updateCapsLock());
        passwordField.focusedProperty().addListener((obs, old, focused) -> {
            if (Boolean.TRUE.equals(focused)) updateCapsLock();
        });

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

    private void updateCapsLock() {
        try {
            boolean caps = java.awt.Toolkit.getDefaultToolkit()
                    .getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK);
            capsLockLabel.setVisible(caps);
            capsLockLabel.setManaged(caps);
        } catch (UnsupportedOperationException ignored) {
            // Not available on all platforms
        }
    }

    @FXML
    private void onDelete() {
        errorLabel.setText("");
        char[] password = passwordField.getText().toCharArray();
        if (password.length == 0) {
            errorLabel.setText("Password cannot be empty.");
            return;
        }
        try {
            Path vaultPath = StoragePathResolver.credentialsFilePath();
            byte[] fileBytes = Files.readAllBytes(vaultPath);
            if (fileBytes.length < CryptoService.SALT_LENGTH) {
                errorLabel.setText("Vault file appears corrupted.");
                Arrays.fill(password, '\0');
                return;
            }
            byte[] salt = Arrays.copyOf(fileBytes, CryptoService.SALT_LENGTH);
            CryptoService candidate = CryptoService.withSalt(password, salt);
            candidate.decrypt(fileBytes);
            Files.delete(vaultPath);
            deleted = true;
            close();
        } catch (AEADBadTagException e) {
            errorLabel.setText("Wrong password.");
            passwordField.clear();
        } catch (GeneralSecurityException | IOException e) {
            errorLabel.setText("Failed to verify or delete vault.");
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    @FXML
    private void onCancel() {
        close();
    }

    public boolean isDeleted() {
        return deleted;
    }

    private void close() {
        rootPane.getScene().getWindow().hide();
    }
}
