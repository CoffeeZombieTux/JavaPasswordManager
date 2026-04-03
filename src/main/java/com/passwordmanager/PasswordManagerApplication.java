package com.passwordmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PasswordManagerApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                PasswordManagerApplication.class.getResource("main-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
        stage.setTitle("PasswordManager");
        stage.setScene(scene);
        stage.setMinWidth(1366);
        stage.setMinHeight(768);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
