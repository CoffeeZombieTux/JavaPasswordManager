package com.passwordmanager.ui.screen;

import com.passwordmanager.model.Credential;
import com.passwordmanager.repository.CredentialRepository;
import com.passwordmanager.repository.FileCredentialRepository;
import com.passwordmanager.service.CredentialService;
import com.passwordmanager.ui.component.form.AddCredentialFormController;
import com.passwordmanager.ui.component.detail.CredentialDetailController;
import com.passwordmanager.ui.component.list.CredentialListController;
import com.passwordmanager.ui.component.RecordsCountController;
import com.passwordmanager.ui.component.topbar.TopBarActionsController;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;

public class MainController {

    private final CredentialService credentialService;
    private List<Credential> data;

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

        credentialListController.bind(this.data);
        credentialListController.setCredentialSelectedCallback(this::showDetails);

        if (!this.data.isEmpty()) {
            credentialListController.select(this.data.getFirst());
        } else {
            showAddNewCredentialForm();
        }
    }

    private void onCredentialSaved(Credential saved) {
        boolean isNew = data.stream().noneMatch(c -> c.getId().equals(saved.getId()));
        if (isNew) {
            data.add(saved);
        } else {
            data.replaceAll(c -> c.getId().equals(saved.getId()) ? saved : c);
        }
        credentialListController.bind(data);
        credentialListController.select(saved);
        recordsCountController.bind(data.size());
        showDetails(saved);
    }

    private void onCredentialDeleted() {
        Credential deleted = credentialListController.getSelected();
        int index = data.indexOf(deleted);
        Credential next = (index < data.size() - 1) ? data.get(index + 1) : null;
        data.remove(deleted);
        if (next == null) {
            next = data.isEmpty() ? null : data.getFirst();
        }
        credentialListController.bind(data);
        recordsCountController.bind(data.size());
        if (next == null) {
            showAddNewCredentialForm();
            return;
        }
        credentialListController.select(next);
        showDetails(next);
    }

    private void showDetails(Credential selected) {
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
}
