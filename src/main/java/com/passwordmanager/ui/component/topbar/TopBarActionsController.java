package com.passwordmanager.ui.component.topbar;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.Objects;
import java.util.function.Consumer;

public class TopBarActionsController {
    @FXML
    private TextField searchInput;

    private Runnable addButtonCallback = () -> {};
    private Consumer<String> searchButtonCallback = searchString -> {};

    public void setAddButtonCallback(Runnable addButtonCallback) {
        this.addButtonCallback = Objects.requireNonNull(addButtonCallback);
    }

    public void setSearchButtonCallback(Consumer<String> searchButtonCallback) {
        this.searchButtonCallback = Objects.requireNonNull(searchButtonCallback);
    }

    @FXML
    public void onAddSearchButtonClick() {
        searchButtonCallback.accept(searchInput.getText());
    }

    @FXML
    public void onAddCredentialButtonClick() {
        addButtonCallback.run();
    }
}
