package com.passwordmanager.ui.screen;

import com.passwordmanager.model.Credential;
import com.passwordmanager.repository.CredentialRepository;
import com.passwordmanager.repository.FileCredentialRepository;
import com.passwordmanager.service.CredentialService;
import com.passwordmanager.ui.component.categories.CategoryListController;
import com.passwordmanager.ui.component.form.AddCredentialFormController;
import com.passwordmanager.ui.component.detail.CredentialDetailController;
import com.passwordmanager.ui.component.list.CredentialListController;
import com.passwordmanager.ui.component.RecordsCountController;
import com.passwordmanager.ui.component.topbar.TopBarActionsController;
import javafx.application.Platform;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainController {

    private final CredentialService credentialService;
    private List<Credential> data;
    private String selectedCategory = CredentialService.ALL_CATEGORIES;
    private UUID selectedCredentialId;

    @FXML
    private CredentialDetailController detailViewController;

    @FXML
    private AddCredentialFormController addFormViewController;

    @FXML
    private RecordsCountController recordsCountController;

    @FXML
    private CredentialListController credentialListController;

    @FXML
    private TopBarActionsController topBarActionsController;

    @FXML
    private CategoryListController categoryListController;

    public MainController() {
        CredentialRepository repository = new FileCredentialRepository();
        this.credentialService = new CredentialService(repository);
    }

    @FXML
    private void initialize() {
        this.data = new ArrayList<>(credentialService.findAll());

        recordsCountController.bind(this.data.size());

        topBarActionsController.setAddButtonCallback(this::showAddNewCredentialForm);

        addFormViewController.setCredentialService(credentialService);
        addFormViewController.setCancelButtonCallback(this::closeForm);
        addFormViewController.setOnSavedCallback(this::onCredentialSaved);

        detailViewController.setCredentialService(credentialService);
        detailViewController.setOnEditCallback(this::showEditSelectedForm);
        detailViewController.setOnDeleteCallback(this::onCredentialDeleted);

        categoryListController.bind(this.credentialService.findAllCategories());
        categoryListController.setCategorySelectedCallback(this::filterCredentialsByCategory);

        credentialListController.bind(this.data);
        credentialListController.setCredentialSelectedCallback(this::showDetails);
        selectDefaultCredential();
    }

    private void reloadData() {
        this.data = credentialService.filterByCategory(selectedCategory);
        credentialListController.bind(data);
        recordsCountController.bind(data.size());
        categoryListController.bind(credentialService.findAllCategories());
        categoryListController.select(selectedCategory);
        selectDefaultCredential();
    }

    private void onCredentialSaved(Credential saved) {
        this.selectedCredentialId = saved.getId();
        reloadData();
    }

    private void onCredentialDeleted() {
        Credential deleted = credentialListController.getSelected();
        int index = data.indexOf(deleted);
        Credential previous = (index > 0) ? data.get(index - 1) : null;
        if (previous != null) {
            this.selectedCredentialId = previous.getId();
        }
        reloadData();
    }

    private void showDetails(Credential selected) {
        this.selectedCredentialId = selected.getId();
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
        if (this.selectedCredentialId != null ) {
            Credential selectedCredential = this.data
                    .stream()
                    .filter(c -> c.getId().equals(this.selectedCredentialId))
                    .findFirst()
                    .orElse(null);
            if (selectedCredential != null) {
                credentialListController.select(selectedCredential);
                return;
            }
        }
        if (!this.data.isEmpty()) {
            selectedCredentialId = this.data.getFirst().getId();
            credentialListController.select(this.data.getFirst());
            return;
        }
        selectedCredentialId = null;
        showAddNewCredentialForm();
    }

    private void filterCredentialsByCategory(String category) {
        if (category.equals(this.selectedCategory)) return;
        this.selectedCategory = category;
        reloadData();

    }

}
