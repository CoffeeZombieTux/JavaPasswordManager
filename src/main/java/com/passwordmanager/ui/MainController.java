package com.passwordmanager.ui;

import com.passwordmanager.model.Credential;
import com.passwordmanager.repository.CredentialRepository;
import com.passwordmanager.repository.FileCredentialRepository;
import com.passwordmanager.service.CredentialService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MainController {

    private final CredentialService credentialService;

    @FXML
    public VBox addFormPane;

    @FXML
    public VBox detailPane;
    public TextField newTitleField;
    public TextField newUsernameField;
    public TextField newWebsiteField;

    @FXML
    private Label totalCountLabel;

    @FXML
    private ListView<Credential> credentialListView;

    @FXML
    private Label selectedTitleLabel;

    @FXML
    private Label selectedUsernameLabel;

    public MainController() {
        CredentialRepository repository = new FileCredentialRepository();
        this.credentialService = new CredentialService(repository);
    }

    @FXML
    private void initialize() {
        var credentials = credentialService.findAll();
        int total = credentials.size();
        totalCountLabel.setText(Integer.toString(total));

        credentialListView.setItems(FXCollections.observableArrayList(credentials));
        credentialListView.setCellFactory(list -> new CredentialListCell());
        if (total > 0) {
            this.showDetailMode(credentials.getFirst());
        } else {
            this.showAddMode();
        }
        credentialListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, selected) -> {
                    if (selected == null) {
                        return;
                    }
                    this.showDetailMode(selected);
                }
        );
    }

    public void handleSaveCredential(ActionEvent actionEvent) {
    }

    private static class CredentialListCell extends ListCell<Credential> {

        private final Node view;
        private final CredentialItemController controller;

        private CredentialListCell() {
            try {
                FXMLLoader loader = new FXMLLoader(
                        MainController.class.getResource("/com/passwordmanager/ui/credential-item.fxml")
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

    private void showDetailMode(@NotNull Credential c) {
        detailPane.setVisible(true);
        detailPane.setManaged(true);
        addFormPane.setVisible(false);
        addFormPane.setManaged(false);

        selectedTitleLabel.setText(c.getTitle());
        selectedUsernameLabel.setText(c.getUsername());
    }

    private void showAddMode() {
        detailPane.setVisible(false);
        detailPane.setManaged(false);
        addFormPane.setVisible(true);
        addFormPane.setManaged(true);
    }
}
