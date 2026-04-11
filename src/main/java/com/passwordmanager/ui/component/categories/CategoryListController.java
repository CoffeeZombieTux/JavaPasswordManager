package com.passwordmanager.ui.component.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class CategoryListController {

    @FXML
    private ListView<String> categoryListView;
    private final ObservableList<String> items = FXCollections.observableArrayList();

    private Consumer<String> categorySelectedCallback = category -> {};

    @FXML
    private void initialize() {
        categoryListView.setItems(items);
        categoryListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });
        categoryListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, selected) -> {
                    if (selected != null) {
                        categorySelectedCallback.accept(selected);
                    }
                }
        );
    }

    public void bind(List<String> data) {
        categoryListView.getSelectionModel().clearSelection();
        items.setAll(data);
    }

    public void select(String category) {
        categoryListView.getSelectionModel().select(category);
    }

    public String getSelected() {
        return categoryListView.getSelectionModel().getSelectedItem();
    }

    public void setCategorySelectedCallback(Consumer<String> categorySelectedCallback) {
        this.categorySelectedCallback = Objects.requireNonNull(categorySelectedCallback);
    }
}
