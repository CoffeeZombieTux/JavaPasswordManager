package com.passwordmanager.ui.component.detail;

import com.passwordmanager.model.Credential;
import com.passwordmanager.service.CredentialService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.passwordmanager.model.Credential.CredentialType.NOTE;
import static com.passwordmanager.model.Credential.CredentialType.TOKEN;

public class CredentialDetailController {

    private static final String SHOW_PASSWORD_LABEL = "Show password";
    private static final String HIDE_PASSWORD_LABEL = "Hide password";
    private static final String COPY_PASSWORD_LABEL = "Copy password";
    private static final String SHOW_TOKEN_LABEL = "Show token";
    private static final String HIDE_TOKEN_LABEL = "Hide token";
    private static final String COPY_TOKEN_LABEL = "Copy token";
    private static final String PASSWORD_LABEL = "Password";
    private static final String TOKEN_LABEL = "Token";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d, yyyy  HH:mm").withZone(ZoneId.systemDefault());

    @FXML
    private Label name;
    @FXML
    private HBox usernameRow;
    @FXML
    private Label username;
    @FXML
    private HBox passwordRow;
    @FXML
    private Label password;
    @FXML
    private HBox websiteRow;
    @FXML
    private Label website;
    @FXML
    private Label category;
    @FXML
    private HBox notesRow;
    @FXML
    private Text notes;
    @FXML
    private Label createdAt;
    @FXML
    private Label updatedAt;
    @FXML
    private VBox detailPane;
    @FXML
    private Button showPasswordButton;
    @FXML
    private Button copyPasswordButton;

    private UUID id;
    private CredentialService credentialService;
    private PasswordState passwordState;

    private Runnable onEditCallback = () -> {};
    private Runnable onDeleteCallback = () -> {};

    @FXML
    public void initialize() {
        // label is 110px, spacing is 16px
        notes.wrappingWidthProperty().bind(notesRow.widthProperty().subtract(110 + 16));
    }

    public void setCredentialService(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    public void setOnEditCallback(Runnable onEditCallback) {
        this.onEditCallback = Objects.requireNonNull(onEditCallback);
    }

    public void setOnDeleteCallback(Runnable onDeleteCallback) {
        this.onDeleteCallback = Objects.requireNonNull(onDeleteCallback);
    }

    public void bind(@NotNull Credential credential) {
        id = credential.getId();
        passwordState = new PasswordState(credential.getPassword());
        bindSelectedTypeForm(credential.getType());
        name.setText(credential.getName());
        username.setText(credential.getUsername());
        password.setText(passwordState.getDisplayValue());
        website.setText(credential.getWebsite());
        category.setText(credential.getCategory());
        notes.setText(credential.getNotes());
        createdAt.setText(DATE_FORMATTER.format(credential.getCreatedAt()));
        updatedAt.setText(DATE_FORMATTER.format(credential.getUpdatedAt()));
    }

    public void show() {
        detailPane.setVisible(true);
        detailPane.setManaged(true);
    }

    public void hide() {
        detailPane.setVisible(false);
        detailPane.setManaged(false);
    }

    @FXML
    public void handleShowPassword(ActionEvent actionEvent) {
        passwordState.toggle();
        password.setText(passwordState.getDisplayValue());
        showPasswordButton.setText(passwordState.isVisible() ? HIDE_PASSWORD_LABEL : SHOW_PASSWORD_LABEL);
    }

    @FXML
    public void handleCopyPassword(ActionEvent actionEvent) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(passwordState.getValue()), null);
    }

    @FXML
    public void handleEditCredential(ActionEvent actionEvent) {
        onEditCallback.run();
    }

    @FXML
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
            credentialService.delete(id);
            onDeleteCallback.run();
        }
    }

    private void bindSelectedTypeForm(Credential.CredentialType type) {
        resetDetail();
        switch (type) {
            case TOKEN:
                bindTokenDetail();
                break;
            case NOTE:
                bindNoteDetail();
                break;
        }
    }

    private void resetDetail() {
        Label passwordLabel = (Label) passwordRow.getChildren().getFirst();
        passwordLabel.setText(PASSWORD_LABEL + ":");
        usernameRow.setVisible(true);
        usernameRow.setManaged(true);
        passwordRow.setVisible(true);
        passwordRow.setManaged(true);
        websiteRow.setVisible(true);
        websiteRow.setManaged(true);
        showPasswordButton.setText(passwordState.isVisible() ? HIDE_PASSWORD_LABEL : SHOW_PASSWORD_LABEL);
        showPasswordButton.setVisible(true);
        showPasswordButton.setManaged(true);
        copyPasswordButton.setVisible(true);
        copyPasswordButton.setManaged(true);
        copyPasswordButton.setText(COPY_PASSWORD_LABEL);
    }

    private void bindTokenDetail() {
        usernameRow.setVisible(false);
        usernameRow.setManaged(false);
        Label passwordLabel = (Label) passwordRow.getChildren().getFirst();
        passwordLabel.setText(TOKEN_LABEL + ":");
        showPasswordButton.setText(passwordState.isVisible() ? HIDE_TOKEN_LABEL : SHOW_TOKEN_LABEL);
        copyPasswordButton.setText(COPY_TOKEN_LABEL);
    }

    private void bindNoteDetail() {
        usernameRow.setVisible(false);
        usernameRow.setManaged(false);
        passwordRow.setVisible(false);
        passwordRow.setManaged(false);
        websiteRow.setVisible(false);
        websiteRow.setManaged(false);
        showPasswordButton.setVisible(false);
        showPasswordButton.setManaged(false);
        copyPasswordButton.setVisible(false);
        copyPasswordButton.setManaged(false);
    }

    private static class PasswordState {
        private final String value;
        private boolean visible = false;

        PasswordState(String value) {
            this.value = value;
        }

        void toggle() {
            visible = !visible;
        }

        String getDisplayValue() {
            return visible ? value : "*".repeat(value.length());
        }

        public String getValue() {
            return value;
        }

        public boolean isVisible() {
            return visible;
        }
    }
}
