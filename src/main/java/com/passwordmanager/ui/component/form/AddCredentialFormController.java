package com.passwordmanager.ui.component.form;

import com.passwordmanager.model.Credential;
import com.passwordmanager.service.CredentialService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private TextField website;
    @FXML
    private TextField category;
    @FXML
    private TextField notes;
    @FXML
    private VBox addFormPane;

    private Runnable cancelButtonCallback = () -> {};
    private Consumer<Credential> onSavedCallback = credential -> {};

    private Credential editingCredential;

    private CredentialService credentialService;

    public void bind() {
        this.editingCredential = null;
        this.clear();
        title.setText("Add new credential");
    }

    public void bind(Credential credential) {
        this.editingCredential = credential;
        this.clear();
        title.setText(credential.getName() + " (Edit)");
        name.setText(credential.getName());
        username.setText(credential.getUsername());
        password.setText(credential.getPassword());
        website.setText(credential.getWebsite());
        category.setText(credential.getCategory());
        notes.setText(credential.getNotes());
    }

    public void clear() {
        name.clear();
        username.clear();
        password.clear();
        website.clear();
        category.clear();
        notes.clear();
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
        Credential saved = credentialService.save(
                id,
                name.getText(),
                username.getText(),
                password.getText(),
                website.getText(),
                category.getText(),
                notes.getText(),
                editingCredential != null ? editingCredential.getCreatedAt() : null
                );
        onSavedCallback.accept(saved);
    }

    public void handleCancelEditing(ActionEvent actionEvent) {
        this.editingCredential = null;
        this.clear();
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
}
