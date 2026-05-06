package com.passwordmanager.ui.screen;

import com.passwordmanager.crypto.CryptoService;
import com.passwordmanager.storage.StoragePathResolver;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class WelcomeController {

    public enum Action { CREATE, IMPORT, CANCEL }

    @FXML
    private VBox rootPane;
    @FXML
    private Label errorLabel;

    private Action result = Action.CANCEL;
    private double dragOffsetX;
    private double dragOffsetY;

    @FXML
    private void initialize() {
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
    private void onCreateVault() {
        result = Action.CREATE;
        close();
    }

    @FXML
    private void onImport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Vault File");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Vault Files (*.vault)", "*.vault")
        );
        File selected = chooser.showOpenDialog(rootPane.getScene().getWindow());
        if (selected == null) return;

        if (selected.length() < CryptoService.SALT_LENGTH) {
            errorLabel.setText("Selected file is not a valid vault.");
            return;
        }

        try {
            Path dest = StoragePathResolver.credentialsFilePath();
            Files.createDirectories(dest.getParent());
            Files.copy(selected.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            result = Action.IMPORT;
            close();
        } catch (IOException e) {
            errorLabel.setText("Failed to import vault: " + e.getMessage());
        }
    }

    public Action getResult() {
        return result;
    }

    private void close() {
        rootPane.getScene().getWindow().hide();
    }
}
