package com.passwordmanager.ui.screen;

import com.passwordmanager.crypto.CryptoService;
import com.passwordmanager.model.Credential;
import com.passwordmanager.model.CredentialFilter;
import com.passwordmanager.model.CredentialType;
import com.passwordmanager.repository.FileCredentialRepository;
import com.passwordmanager.service.CredentialService;
import com.passwordmanager.storage.AppPreferences;
import com.passwordmanager.storage.PreferencesStore;
import com.passwordmanager.storage.StoragePathResolver;
import com.passwordmanager.ui.Dialogs;
import com.passwordmanager.ui.component.categories.CategoryListController;
import com.passwordmanager.ui.component.form.AddCredentialFormController;
import com.passwordmanager.ui.component.detail.CredentialDetailController;
import com.passwordmanager.ui.component.list.CredentialListController;
import com.passwordmanager.ui.component.types.TypesSwitcherController;
import com.passwordmanager.ui.component.topbar.TopBarActionsController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MainController {

    @FXML
    private BorderPane windowRoot;

    @FXML
    private CredentialDetailController detailViewController;

    @FXML
    private AddCredentialFormController addFormViewController;

    @FXML
    private TypesSwitcherController typesSwitcherController;

    @FXML
    private CredentialListController credentialListController;

    @FXML
    private TopBarActionsController topBarActionsController;

    @FXML
    private CategoryListController categoryListController;

    private final CredentialService credentialService;
    private List<Credential> data;
    private UUID selectedCredentialId;
    private CredentialFilter credentialFilter;

    public MainController(CryptoService cryptoService) {
        credentialService = new CredentialService(new FileCredentialRepository(cryptoService));
        credentialFilter = new CredentialFilter();
    }

    @FXML
    private void initialize() {
        typesSwitcherController.setTypeSelectedCallback(this::filterCredentialsByType);

        topBarActionsController.setAddButtonCallback(this::showAddNewCredentialForm);
        topBarActionsController.setSearchButtonCallback(this::searchCredentials);
        topBarActionsController.setExportCallback(this::handleExport);
        topBarActionsController.setDeleteVaultCallback(this::handleDeleteVault);

        addFormViewController.setCredentialService(credentialService);
        addFormViewController.setCancelButtonCallback(this::closeForm);
        addFormViewController.setOnSavedCallback(this::onCredentialSaved);

        detailViewController.setCredentialService(credentialService);
        detailViewController.setOnEditCallback(this::showEditSelectedForm);
        detailViewController.setOnDeleteCallback(this::onCredentialDeleted);

        categoryListController.setCategorySelectedCallback(this::filterCredentialsByCategory);

        credentialListController.setCredentialSelectedCallback(this::showDetails);

        Rectangle clip = new Rectangle();
        clip.setArcWidth(44);
        clip.setArcHeight(44);
        clip.widthProperty().bind(windowRoot.widthProperty());
        clip.heightProperty().bind(windowRoot.heightProperty());
        windowRoot.setClip(clip);

        reloadData();
    }

    private void reloadData() {
        data = credentialService.getFilteredData(credentialFilter);
        credentialListController.bind(data);
        typesSwitcherController.bind(credentialService.getTotals(credentialFilter));
        typesSwitcherController.select(credentialFilter.getType());
        categoryListController.bind(credentialService.getCategories(credentialFilter));
        categoryListController.select(credentialFilter.getCategory());
        selectDefaultCredential();
    }

    private void onCredentialSaved(Credential saved) {
        selectedCredentialId = saved.id();
        reloadData();
    }

    private void onCredentialDeleted() {
        Credential deleted = credentialListController.getSelected();
        int index = data.indexOf(deleted);
        Credential previous = (index > 0) ? data.get(index - 1) : null;
        if (previous != null) {
            selectedCredentialId = previous.id();
        }
        reloadData();
    }

    private void showDetails(Credential selected) {
        selectedCredentialId = selected.id();
        detailViewController.bind(selected);
        addFormViewController.hide();
        detailViewController.show();
    }

    private void showEditSelectedForm() {
        addFormViewController.bind(credentialListController.getSelected());
        detailViewController.hide();
        addFormViewController.show();
    }

    private void showAddNewCredentialForm() {
        addFormViewController.bind();
        detailViewController.hide();
        addFormViewController.show();
    }

    private void closeForm() {
        Credential selected = credentialListController.getSelected();
        if (selected != null) {
            showDetails(selected);
        } else {
            showAddNewCredentialForm();
        }
    }

    private void selectDefaultCredential() {
        if (selectedCredentialId != null) {
            Credential selectedCredential = data.stream()
                    .filter(c -> c.id().equals(selectedCredentialId))
                    .findFirst()
                    .orElse(null);
            if (selectedCredential != null) {
                credentialListController.select(selectedCredential);
                return;
            }
        }
        if (!data.isEmpty()) {
            selectedCredentialId = data.getFirst().id();
            credentialListController.select(data.getFirst());
            return;
        }
        selectedCredentialId = null;
        showAddNewCredentialForm();
    }

    private void filterCredentialsByCategory(String category) {
        if (Objects.equals(category, credentialFilter.getCategory())) return;
        credentialFilter = credentialFilter.withCategory(category);
        reloadData();
    }

    private void filterCredentialsByType(CredentialType type) {
        if (Objects.equals(type, credentialFilter.getType())) return;
        credentialFilter = credentialFilter.withType(type).withCategory(CredentialService.ALL_CATEGORIES);
        reloadData();
    }

    private void searchCredentials(String search) {
        credentialFilter = new CredentialFilter(
                search,
                CredentialService.ALL_CATEGORIES,
                CredentialType.ACCOUNT
        );
        reloadData();
    }

    private void handleExport() {
        AppPreferences prefs = PreferencesStore.load();
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Export Directory");
        if (prefs.getLastExportDirectory() != null) {
            File initial = new File(prefs.getLastExportDirectory());
            if (initial.isDirectory()) {
                chooser.setInitialDirectory(initial);
            }
        }
        Stage stage = (Stage) windowRoot.getScene().getWindow();
        File dir = chooser.showDialog(stage);
        if (dir == null) return;

        try {
            Path src = StoragePathResolver.credentialsFilePath();
            Path dest = dir.toPath().resolve(src.getFileName());
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);

            prefs.setLastExportDirectory(dir.getAbsolutePath());
            PreferencesStore.save(prefs);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Successful");
            alert.setHeaderText(null);
            alert.setContentText("Vault exported to:\n" + dest.toAbsolutePath());
            Dialogs.styleAsRounded(alert);
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Failed");
            alert.setHeaderText(null);
            alert.setContentText("Could not export vault: " + e.getMessage());
            Dialogs.styleAsRounded(alert);
            alert.showAndWait();
        }
    }

    private void handleDeleteVault() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainController.class.getResource("/com/passwordmanager/ui/screen/delete-vault-view.fxml")
            );
            Scene scene = new Scene(loader.load());
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            MainController.class.getResource("/com/passwordmanager/app.css")
                    ).toExternalForm()
            );
            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(windowRoot.getScene().getWindow());
            dialog.setScene(scene);
            dialog.sizeToScene();
            dialog.showAndWait();

            DeleteVaultController controller = loader.getController();
            if (controller.isDeleted()) {
                Platform.exit();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not open delete dialog: " + e.getMessage());
            Dialogs.styleAsRounded(alert);
            alert.showAndWait();
        }
    }
}
