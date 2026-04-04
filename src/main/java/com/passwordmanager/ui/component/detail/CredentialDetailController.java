package com.passwordmanager.ui.component.detail;

import com.passwordmanager.model.Credential;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

public class CredentialDetailController {

    @FXML
    private VBox detailPane;

    @FXML
    private Label selectedTitleLabel;

    @FXML
    private Label selectedUsernameLabel;

    public void bind(@NotNull Credential credential) {
        selectedTitleLabel.setText(credential.getTitle());
        selectedUsernameLabel.setText(credential.getUsername());
    }

    public void show() {
        detailPane.setVisible(true);
        detailPane.setManaged(true);
    }

    public void hide() {
        detailPane.setVisible(false);
        detailPane.setManaged(false);
    }
}
