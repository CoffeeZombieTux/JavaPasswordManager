package com.passwordmanager.ui.component.form;

import com.passwordmanager.model.Credential;
import com.passwordmanager.service.CredentialService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.passwordmanager.ui.Dialogs;

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

    private Credential.CredentialType selectedType;
    private Credential editingCredential;
    private CredentialService credentialService;

    private Runnable cancelButtonCallback = () -> {};
    private Consumer<Credential> onSavedCallback = credential -> {};

    @FXML
    private void initialize() {
        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                switchType();
                bind();
            }
        });
    }

    public void bind() {
        editingCredential = null;
        reset();
        switchType();
        title.setText("Add new " + selectedType);
        bindSelectedTypeForm();
    }

    public void bind(Credential credential) {
        editingCredential = credential;
        reset();
        typeGroupContainer.setVisible(false);
        typeGroupContainer.setManaged(false);
        selectedType = credential.getType();
        bindSelectedTypeForm();
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
        typeGroupContainer.setVisible(true);
        typeGroupContainer.setManaged(true);
        usernameRow.setVisible(true);
        usernameRow.setManaged(true);
        passwordRow.setVisible(true);
        passwordRow.setManaged(true);
        websiteRow.setVisible(true);
        websiteRow.setManaged(true);
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

    @FXML
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
            Dialogs.styleAsRounded(alert);
            alert.showAndWait();
        }
    }

    @FXML
    public void handleCancelEditing(ActionEvent actionEvent) {
        editingCredential = null;
        reset();
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
        selectedType = Credential.CredentialType.valueOf(
                (String) typeGroup.getSelectedToggle().getUserData()
        );
    }

    private void bindSelectedTypeForm() {
        switch (selectedType) {
            case TOKEN:
                bindTokenForm();
                break;
            case NOTE:
                bindNoteForm();
                break;
        }
    }

    private void bindTokenForm() {
        usernameRow.setVisible(false);
        usernameRow.setManaged(false);
        Label passwordLabel = (Label) passwordRow.getChildren().getFirst();
        passwordLabel.setText("Token:");
    }

    private void bindNoteForm() {
        usernameRow.setVisible(false);
        usernameRow.setManaged(false);
        passwordRow.setVisible(false);
        passwordRow.setManaged(false);
        websiteRow.setVisible(false);
        websiteRow.setManaged(false);
    }
}
