package com.passwordmanager.ui.component.list;

import com.passwordmanager.model.Credential;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class CredentialListController {
    @FXML
    private ListView<Credential> credentialListView;

    private Consumer<Credential> onCredentialSelected = credential -> {};

    @FXML
    private void initialize() {
        credentialListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, selected) -> {
                    if (selected != null) {
                        onCredentialSelected.accept(selected);
                    }
                 }
        );
    }

    public void bind(List<Credential> data) {
        credentialListView.setItems(FXCollections.observableArrayList(data));
        credentialListView.setCellFactory(list -> new CredentialListCell());
    }

    public void setOnCredentialSelected(Consumer<Credential> onCredentialSelected) {
        this.onCredentialSelected = Objects.requireNonNull(onCredentialSelected);
    }

    public void select(Credential credential) {
        credentialListView.getSelectionModel().select(credential);
    }

    private static class CredentialListCell extends ListCell<Credential> {

        private final Node view;
        private final CredentialItemController controller;

        private CredentialListCell() {
            try {
                FXMLLoader loader = new FXMLLoader(
                        CredentialListController.class.getResource("/com/passwordmanager/ui/component/list/credential-item.fxml")
                );
                view = loader.load();
                controller = loader.getController();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load credential-item.fxml", e);
            }
        }

        @Override
        protected void updateItem(Credential item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                return;
            }
            controller.bind(item);
            controller.setSelected(isSelected());
            setGraphic(view);
        }

        @Override
        public void updateSelected(boolean selected) {
            super.updateSelected(selected);
            if (getItem() != null) {
                controller.setSelected(selected);
            }
        }
    }
}
