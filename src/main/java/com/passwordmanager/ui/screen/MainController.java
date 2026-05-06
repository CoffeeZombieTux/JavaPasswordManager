package com.passwordmanager.ui.screen;

import com.passwordmanager.crypto.CryptoService;
import com.passwordmanager.model.Credential;
import com.passwordmanager.model.CredentialFilter;
import com.passwordmanager.model.CredentialType;
import com.passwordmanager.repository.FileCredentialRepository;
import com.passwordmanager.service.CredentialService;
import com.passwordmanager.ui.component.categories.CategoryListController;
import com.passwordmanager.ui.component.form.AddCredentialFormController;
import com.passwordmanager.ui.component.detail.CredentialDetailController;
import com.passwordmanager.ui.component.list.CredentialListController;
import com.passwordmanager.ui.component.types.TypesSwitcherController;
import com.passwordmanager.ui.component.topbar.TopBarActionsController;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

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
}
