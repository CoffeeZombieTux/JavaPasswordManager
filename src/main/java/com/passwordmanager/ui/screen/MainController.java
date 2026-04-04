package com.passwordmanager.ui.screen;

import com.passwordmanager.model.Credential;
import com.passwordmanager.repository.CredentialRepository;
import com.passwordmanager.repository.FileCredentialRepository;
import com.passwordmanager.service.CredentialService;
import com.passwordmanager.ui.component.form.AddCredentialFormController;
import com.passwordmanager.ui.component.detail.CredentialDetailController;
import com.passwordmanager.ui.component.list.CredentialListController;
import com.passwordmanager.ui.component.RecordsCountController;
import javafx.fxml.FXML;

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

    public MainController() {
        CredentialRepository repository = new FileCredentialRepository();
        this.credentialService = new CredentialService(repository);
    }

    @FXML
    private void initialize() {
        this.data = credentialService.findAll();
        recordsCountController.bind(this.data.size());
        credentialListController.bind(this.data);
        credentialListController.setOnCredentialSelected(this::showDetails);

        if (!this.data.isEmpty()) {
            credentialListController.select(this.data.getFirst());
        } else {
            detailViewController.hide();
            addFormViewController.show();
        }
    }

    private void showDetails(Credential selected) {
        detailViewController.bind(selected);
        detailViewController.show();
        addFormViewController.hide();
    }
}
