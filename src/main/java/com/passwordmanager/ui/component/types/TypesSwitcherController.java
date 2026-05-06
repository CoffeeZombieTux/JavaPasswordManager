package com.passwordmanager.ui.component.types;

import com.passwordmanager.model.CredentialType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TypesSwitcherController {

    @FXML
    private VBox accountCard;
    @FXML
    private VBox tokenCard;
    @FXML
    private VBox noteCard;
    @FXML
    private Label accountsTotalCount;
    @FXML
    private Label tokensTotalCount;
    @FXML
    private Label notesCountLabel;

    private static final String SELECTED_CLASS = "selected";

    private Consumer<CredentialType> typeSelectedCallback = type -> {};

    @FXML
    private void handleTypeSelected(MouseEvent event) {
        VBox card = (VBox) event.getSource();
        CredentialType type = CredentialType.valueOf((String) card.getUserData());
        typeSelectedCallback.accept(type);
    }

    public void bind(Map<CredentialType, Integer> totals) {
        accountsTotalCount.setText(Integer.toString(totals.get(CredentialType.ACCOUNT)));
        tokensTotalCount.setText(Integer.toString(totals.get(CredentialType.TOKEN)));
        notesCountLabel.setText(Integer.toString(totals.get(CredentialType.NOTE)));
    }

    public void select(CredentialType type) {
        List<VBox> cards = List.of(accountCard, tokenCard, noteCard);
        cards.forEach(c -> c.getStyleClass().remove(SELECTED_CLASS));
        if (type == CredentialType.ACCOUNT) accountCard.getStyleClass().add(SELECTED_CLASS);
        else if (type == CredentialType.TOKEN) tokenCard.getStyleClass().add(SELECTED_CLASS);
        else if (type == CredentialType.NOTE) noteCard.getStyleClass().add(SELECTED_CLASS);
    }

    public void setTypeSelectedCallback(Consumer<CredentialType> typeSelectedCallback) {
        this.typeSelectedCallback = Objects.requireNonNull(typeSelectedCallback);
    }
}
