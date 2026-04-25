package com.passwordmanager.ui.component.form;

import com.passwordmanager.model.Credential;
import com.passwordmanager.service.CredentialService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class AddCredentialFormController {
    @FXML
    private Label title;
    @FXML
    private TextField name;
    @FXML
    private HBox usernameRow;
    @FXML
    private TextField username;
    @FXML
    private HBox passwordRow;
    @FXML
    private TextField password;
    @FXML
    private HBox websiteRow;
    @FXML
    private TextField website;
    @FXML
    private TextField category;
    @FXML
    private TextArea notes;
    @FXML
    private VBox addFormPane;
    @FXML
    private HBox typeGroupContainer;
    @FXML
    private ToggleGroup typeGroup;
    @FXML
    private void initialize() {
        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                this.switchType();
                this.bind();
            }
        });
    }

    Credential.CredentialType selectedType;

    private Runnable cancelButtonCallback = () -> {};
    private Consumer<Credential> onSavedCallback = credential -> {};

    private Credential editingCredential;

    private CredentialService credentialService;

    public void bind() {
        this.editingCredential = null;
        this.reset();
        this.switchType();
        title.setText("Add new " + this.selectedType);
        this.bindSelectedTypeForm();
    }

    public void bind(Credential credential) {
        this.editingCredential = credential;
        this.reset();
        this.typeGroupContainer.setVisible(false);
        this.typeGroupContainer.setManaged(false);
        this.selectedType = credential.getType();
        this.bindSelectedTypeForm();
        title.setText(credential.getName() + " (Edit)");
        name.setText(credential.getName());
        username.setText(credential.getUsername());
        password.setText(credential.getPassword());
        website.setText(credential.getWebsite());
        category.setText(credential.getCategory());
        notes.setText(credential.getNotes());
    }

    public void reset() {
        name.clear();
        username.clear();
        password.clear();
        website.clear();
        category.clear();
        notes.clear();
        this.typeGroupContainer.setVisible(true);
        this.typeGroupContainer.setManaged(true);
        this.usernameRow.setVisible(true);
        this.usernameRow.setManaged(true);
        this.passwordRow.setVisible(true);
        this.passwordRow.setManaged(true);
        this.websiteRow.setVisible(true);
        this.websiteRow.setManaged(true);
        Label passwordLabel = (Label) passwordRow.getChildren().getFirst();
        passwordLabel.setText("Password:");
    }

    public void show() {
        addFormPane.setVisible(true);
        addFormPane.setManaged(true);
    }

    public void hide() {
        addFormPane.setVisible(false);
        addFormPane.setManaged(false);
    }

    public void handleSaveCredential(ActionEvent actionEvent) {
        UUID id = editingCredential != null ? editingCredential.getId() : null;

        try {
            Credential saved = credentialService.save(
                    id,
                    selectedType,
                    name.getText(),
                    username.getText(),
                    password.getText(),
                    website.getText(),
                    category.getText(),
                    notes.getText(),
                    editingCredential != null ? editingCredential.getCreatedAt() : null
            );
            onSavedCallback.accept(saved);
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.getDialogPane().getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/passwordmanager/app.css")).toExternalForm()
            );
            alert.showAndWait();
        }
    }

    public void handleCancelEditing(ActionEvent actionEvent) {
        this.editingCredential = null;
        this.reset();
        cancelButtonCallback.run();
    }

    public void setCancelButtonCallback(Runnable cancelButtonCallback) {
        this.cancelButtonCallback = Objects.requireNonNull(cancelButtonCallback);
    }

    public void setOnSavedCallback(Consumer<Credential> onSavedCallback) {
        this.onSavedCallback = Objects.requireNonNull(onSavedCallback);
    }

    public void setCredentialService(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    private void switchType() {
        this.selectedType = Credential.CredentialType.valueOf(
                (String) typeGroup.getSelectedToggle().getUserData()
        );
    }

    private void bindSelectedTypeForm() {
        switch (this.selectedType) {
            case TOKEN:
                this.bindTokenForm();
                break;
            case NOTE:
                this.bindNoteForm();
                break;
        }
    }

    private void bindTokenForm() {
        this.usernameRow.setVisible(false);
        this.usernameRow.setManaged(false);
        Label passwordLabel = (Label) passwordRow.getChildren().getFirst();
        passwordLabel.setText("Token:");
    }

    private void bindNoteForm() {
        this.usernameRow.setVisible(false);
        this.usernameRow.setManaged(false);
        this.passwordRow.setVisible(false);
        this.passwordRow.setManaged(false);
        this.websiteRow.setVisible(false);
        this.websiteRow.setManaged(false);
    }
}
