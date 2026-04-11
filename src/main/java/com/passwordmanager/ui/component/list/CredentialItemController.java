package com.passwordmanager.ui.component.list;

import com.passwordmanager.model.Credential;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CredentialItemController {

    @FXML
    private HBox rowRoot;

    @FXML
    private Label titleLabel;

    @FXML
    private Label usernameLabel;

    public void bind(Credential credential) {
        titleLabel.setText(credential.getName());
        usernameLabel.setText(credential.getUsername());
    }

    public void setSelected(boolean selected) {
        rowRoot.getStyleClass().remove("selected");
        if (selected) {
            rowRoot.getStyleClass().add("selected");
        }
    }
}
