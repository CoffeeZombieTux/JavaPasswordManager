package com.passwordmanager.ui.screen;

import com.passwordmanager.model.Credential;
import com.passwordmanager.repository.CredentialRepository;
import com.passwordmanager.repository.FileCredentialRepository;
import com.passwordmanager.service.CredentialService;
import com.passwordmanager.ui.component.categories.CategoryListController;
import com.passwordmanager.ui.component.form.AddCredentialFormController;
import com.passwordmanager.ui.component.detail.CredentialDetailController;
import com.passwordmanager.ui.component.list.CredentialListController;
import com.passwordmanager.ui.component.types.TypesSwitcherController;
import com.passwordmanager.ui.component.topbar.TopBarActionsController;
import javafx.fxml.FXML;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainController {

    private final CredentialService credentialService;
    private List<Credential> filteredData;
    private Map<Credential.CredentialType, List<Credential>> allData;
    private String selectedCategory = CredentialService.ALL_CATEGORIES;
    private Credential.CredentialType selectedType = Credential.CredentialType.ACCOUNT;
    private UUID selectedCredentialId;

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

    public MainController() {
        CredentialRepository repository = new FileCredentialRepository();
        this.credentialService = new CredentialService(repository);
    }

    @FXML
    private void initialize() {
        Map<Credential.CredentialType, List<Credential>> allData = credentialService.findAll();

        this.filteredData = allData.get(selectedType);

        typesSwitcherController.bind(countTotals(allData));
        typesSwitcherController.setTypeSelectedCallback(this::filterCredentialsByType);

        topBarActionsController.setAddButtonCallback(this::showAddNewCredentialForm);

        addFormViewController.setCredentialService(credentialService);
        addFormViewController.setCancelButtonCallback(this::closeForm);
        addFormViewController.setOnSavedCallback(this::onCredentialSaved);

        detailViewController.setCredentialService(credentialService);
        detailViewController.setOnEditCallback(this::showEditSelectedForm);
        detailViewController.setOnDeleteCallback(this::onCredentialDeleted);

        categoryListController.bind(this.credentialService.findAllCategories(selectedType));
        categoryListController.setCategorySelectedCallback(this::filterCredentialsByCategory);
        categoryListController.select(selectedCategory);

        credentialListController.bind(this.filteredData);
        credentialListController.setCredentialSelectedCallback(this::showDetails);
        selectDefaultCredential();
    }

    private void reloadData() {
        this.allData = credentialService.findAll();
        this.filteredData = credentialService.filterByCategory(selectedCategory, selectedType);
        credentialListController.bind(filteredData);
        typesSwitcherController.bind(countTotals(allData));
        categoryListController.bind(credentialService.findAllCategories(selectedType));
        categoryListController.select(selectedCategory);
        selectDefaultCredential();
    }

    private void onCredentialSaved(Credential saved) {
        this.selectedCredentialId = saved.getId();
        reloadData();
    }

    private void onCredentialDeleted() {
        Credential deleted = credentialListController.getSelected();
        int index = filteredData.indexOf(deleted);
        Credential previous = (index > 0) ? filteredData.get(index - 1) : null;
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
            Credential selectedCredential = this.filteredData
                    .stream()
                    .filter(c -> c.getId().equals(this.selectedCredentialId))
                    .findFirst()
                    .orElse(null);
            if (selectedCredential != null) {
                credentialListController.select(selectedCredential);
                return;
            }
        }
        if (!this.filteredData.isEmpty()) {
            selectedCredentialId = this.filteredData.getFirst().getId();
            credentialListController.select(this.filteredData.getFirst());
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

    private void filterCredentialsByType(Credential.CredentialType type) {
        if (type.equals(this.selectedType)) return;
        this.selectedType = type;
        this.selectedCategory = CredentialService.ALL_CATEGORIES;
        reloadData();
    }

    private Map<Credential.CredentialType, Integer> countTotals(
            Map<Credential.CredentialType, List<Credential>> allData
    ) {
        Map<Credential.CredentialType, Integer> totals = new java.util.HashMap<>(Map.of());
        for (Map.Entry<Credential.CredentialType, List<Credential>> entry : allData.entrySet()) {
            totals.put(entry.getKey(), entry.getValue().size());
        }
        return totals;
    }
}
