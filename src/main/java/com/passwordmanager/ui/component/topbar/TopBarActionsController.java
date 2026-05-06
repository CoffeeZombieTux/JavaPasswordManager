package com.passwordmanager.ui.component.topbar;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Consumer;

public class TopBarActionsController {

    @FXML
    private HBox topBarRoot;
    @FXML
    private TextField searchInput;

    private Runnable addButtonCallback = () -> {};
    private Consumer<String> searchButtonCallback = searchString -> {};

    private double dragOffsetX;
    private double dragOffsetY;

    @FXML
    private void initialize() {
        topBarRoot.setOnMousePressed(e -> {
            dragOffsetX = e.getSceneX();
            dragOffsetY = e.getSceneY();
        });
        topBarRoot.setOnMouseDragged(e -> {
            Stage stage = (Stage) topBarRoot.getScene().getWindow();
            stage.setX(e.getScreenX() - dragOffsetX);
            stage.setY(e.getScreenY() - dragOffsetY);
        });
    }

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

    @FXML
    public void onMinimizeButtonClick() {
        ((Stage) topBarRoot.getScene().getWindow()).setIconified(true);
    }

    @FXML
    public void onCloseButtonClick() {
        ((Stage) topBarRoot.getScene().getWindow()).close();
    }
}
