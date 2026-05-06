package com.passwordmanager.ui.component.form;

import com.passwordmanager.model.Credential;
import com.passwordmanager.model.CredentialType;
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
    private Label passwordRowLabel;
    @FXML
    private PasswordField password;
    @FXML
    private TextField passwordPlain;
    @FXML
    private Button showPasswordButton;
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

    private CredentialType selectedType;
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
        passwordPlain.textProperty().bindBidirectional(password.textProperty());
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
        selectedType = credential.type();
        bindSelectedTypeForm();
        title.setText(credential.name() + " (Edit)");
        name.setText(credential.name());
        username.setText(credential.username());
        password.setText(credential.password());
        website.setText(credential.website());
        category.setText(credential.category());
        notes.setText(credential.notes());
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
        passwordRowLabel.setText("Password:");
        password.setVisible(true);
        password.setManaged(true);
        passwordPlain.setVisible(false);
        passwordPlain.setManaged(false);
        showPasswordButton.setText("Show");
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
        UUID id = editingCredential != null ? editingCredential.id() : null;

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
                    editingCredential != null ? editingCredential.createdAt() : null
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

    @FXML
    public void handleShowPassword(ActionEvent actionEvent) {
        boolean currentlyHidden = password.isVisible();
        password.setVisible(!currentlyHidden);
        password.setManaged(!currentlyHidden);
        passwordPlain.setVisible(currentlyHidden);
        passwordPlain.setManaged(currentlyHidden);
        showPasswordButton.setText(currentlyHidden ? "Hide" : "Show");
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
        selectedType = CredentialType.valueOf(
                (String) typeGroup.getSelectedToggle().getUserData()
        );
    }

    private void bindSelectedTypeForm() {
        if (selectedType == CredentialType.TOKEN) {
            bindTokenForm();
            return;
        }
        if (selectedType == CredentialType.NOTE) {
            bindNoteForm();
        }
    }

    private void bindTokenForm() {
        usernameRow.setVisible(false);
        usernameRow.setManaged(false);
        passwordRowLabel.setText("Token:");
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
