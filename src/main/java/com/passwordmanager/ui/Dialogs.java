package com.passwordmanager.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;

import java.util.Objects;

public final class Dialogs {

    private Dialogs() {}

    /**
     * Applies transparent style and rounded clip to an Alert before it is shown.
     * Must be called before {@code alert.showAndWait()}.
     */
    public static void styleAsRounded(Alert alert) {
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(Dialogs.class.getResource("/com/passwordmanager/app.css")).toExternalForm()
        );
        alert.setOnShowing(e -> {
            DialogPane dp = alert.getDialogPane();
            dp.getScene().setFill(Color.TRANSPARENT);
            Rectangle clip = new Rectangle();
            clip.setArcWidth(32);
            clip.setArcHeight(32);
            clip.widthProperty().bind(dp.widthProperty());
            clip.heightProperty().bind(dp.heightProperty());
            dp.setClip(clip);
        });
    }
}
