package com.passwordmanager.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.io.IOException;
import java.util.Objects;

public class PasswordManagerApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                PasswordManagerApplication.class.getResource("/com/passwordmanager/ui/screen/main-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
        scene.getStylesheets().add(
                Objects.requireNonNull(PasswordManagerApplication.class.getResource("/com/passwordmanager/app.css"))
                        .toExternalForm()
        );
        stage.setTitle("PasswordManager");
        stage.setScene(scene);
        stage.setMinWidth(1366);
        stage.setMinHeight(768);
        stage.show();
        ScenicView.show(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
