package com.passwordmanager.ui.component.types;

import com.passwordmanager.model.Credential;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TypesSwitcherController {

    private static final Credential.CredentialType ACCOUNT = Credential.CredentialType.ACCOUNT;
    private static final Credential.CredentialType TOKEN = Credential.CredentialType.TOKEN;
    private static final Credential.CredentialType NOTE = Credential.CredentialType.NOTE;

    @FXML
    private Label accountsTotalCount;
    @FXML
    private Label tokensTotalCount;
    @FXML
    private Label notesCountLabel;

    private Consumer<Credential.CredentialType> typeSelectedCallback = type -> {};

    @FXML
    private void handleTypeSelected(MouseEvent event) {
        VBox card = (VBox) event.getSource();
        Credential.CredentialType type = Credential.CredentialType.valueOf((String) card.getUserData());
        typeSelectedCallback.accept(type);
    }

    public void bind(Map<Credential.CredentialType, Integer> totals) {
        accountsTotalCount.setText(Integer.toString(totals.get(ACCOUNT)));
        tokensTotalCount.setText(Integer.toString(totals.get(TOKEN)));
        notesCountLabel.setText(Integer.toString(totals.get(NOTE)));
    }

    public void setTypeSelectedCallback(Consumer<Credential.CredentialType> typeSelectedCallback) {
        this.typeSelectedCallback = Objects.requireNonNull(typeSelectedCallback);
    }
}
