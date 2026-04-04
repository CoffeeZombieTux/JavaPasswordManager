package com.passwordmanager.ui.component.form;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AddCredentialFormController {

    @FXML
    private VBox addFormPane;
    @FXML
    private TextField newTitleField;
    @FXML
    private TextField newUsernameField;
    @FXML
    private TextField newWebsiteField;

    public void clear() {
        newTitleField.clear();
        newUsernameField.clear();
        newWebsiteField.clear();
    }

    public void bind() {

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
    }
}
