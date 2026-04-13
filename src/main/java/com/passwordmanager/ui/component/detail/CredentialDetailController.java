package com.passwordmanager.ui.component.detail;

import com.passwordmanager.model.Credential;
import com.passwordmanager.service.CredentialService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class CredentialDetailController {
    @FXML
    private Label name;
    @FXML
    private Label username;
    @FXML
    private Label password;
    @FXML
    private Label website;
    @FXML
    private Label category;
    @FXML
    private Label notes;
    @FXML
    private Label createdAt;
    @FXML
    private Label updatedAt;
    @FXML
    private VBox detailPane;

    private UUID id;

    private Runnable onEditCallback = () -> {};
    private Runnable onDeleteCallback = () -> {};

    private CredentialService credentialService;

    public void setCredentialService(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    public void bind(@NotNull Credential credential) {
        this.id = credential.getId();
        name.setText(credential.getName());
        username.setText(credential.getUsername());
        password.setText(credential.getPassword());
        website.setText(credential.getWebsite());
        category.setText(credential.getCategory());
        notes.setText(credential.getNotes());
        createdAt.setText(credential.getCreatedAt().toString());
        updatedAt.setText(credential.getUpdatedAt().toString());
    }

    public void show() {
        detailPane.setVisible(true);
        detailPane.setManaged(true);
    }

    public void hide() {
        detailPane.setVisible(false);
        detailPane.setManaged(false);
    }

    public void handleShowPassword(ActionEvent actionEvent) {
    }

    public void handleCopyPassword(ActionEvent actionEvent) {
    }

    public void handleEditCredential(ActionEvent actionEvent) {
        this.onEditCallback.run();
    }

    public void handleDeleteCredential(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete credential");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this credential?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.initOwner(detailPane.getScene().getWindow());
        alert.initModality(javafx.stage.Modality.WINDOW_MODAL);
        alert.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/com/passwordmanager/app.css")).toExternalForm()
        );
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            this.credentialService.delete(id);
            this.onDeleteCallback.run();
        }
    }

    public void setOnEditCallback(Runnable onEditCallback) {
        this.onEditCallback = Objects.requireNonNull(onEditCallback);
    }

    public void setOnDeleteCallback(Runnable onDeleteCallback) {
        this.onDeleteCallback = Objects.requireNonNull(onDeleteCallback);
    }
}
