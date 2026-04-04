package com.passwordmanager.ui.component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RecordsCountController {

    @FXML
    private VBox recordsCount;

    @FXML
    private Label totalCountLabel;

    public void bind(int total) {
        totalCountLabel.setText(Integer.toString(total));
    }
}
