package com.passwordmanager.app;

import com.passwordmanager.crypto.CryptoService;
import com.passwordmanager.ui.screen.MainController;
import com.passwordmanager.ui.screen.MasterPasswordController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("java:S1075") // classpath resource paths are fixed by design, not configurable URIs
public class PasswordManagerApplication extends Application {
    private static final String APP_ICON_PATH = "/com/passwordmanager/icon.png";
    private static final String MAIN_VIEW_FXML = "/com/passwordmanager/ui/screen/main-view.fxml";
    private static final String MASTER_PASSWORD_VIEW_FXML = "/com/passwordmanager/ui/screen/master-password-view.fxml";
    private static final String APP_CSS = "/com/passwordmanager/app.css";

    @Override
    public void start(Stage stage) throws IOException {
        CryptoService cryptoService = showMasterPasswordDialog();
        if (cryptoService == null) {
            Platform.exit();
            return;
        }

        FXMLLoader mainLoader = new FXMLLoader(
                PasswordManagerApplication.class.getResource(MAIN_VIEW_FXML)
        );
        mainLoader.setControllerFactory(clazz -> {
            if (clazz == MainController.class) {
                return new MainController(cryptoService);
            }
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to instantiate controller: " + clazz.getName(), e);
            }
        });

        Scene scene = new Scene(mainLoader.load(), 1426, 828);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(
                Objects.requireNonNull(PasswordManagerApplication.class.getResource(APP_CSS))
                        .toExternalForm()
        );
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Password Manager");
        applyAppIcon(stage);
        stage.setScene(scene);
        stage.setMinWidth(1426);
        stage.setMinHeight(828);
        stage.show();
    }

    private CryptoService showMasterPasswordDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                PasswordManagerApplication.class.getResource(MASTER_PASSWORD_VIEW_FXML)
        );
        Scene scene = new Scene(loader.load());
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(
                Objects.requireNonNull(PasswordManagerApplication.class.getResource(APP_CSS))
                        .toExternalForm()
        );

        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.APPLICATION_MODAL);
        applyAppIcon(dialog);
        dialog.setScene(scene);
        dialog.sizeToScene();
        dialog.showAndWait();

        MasterPasswordController controller = loader.getController();
        return controller.getCryptoService();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void applyAppIcon(Stage stage) {
        var iconUrl = PasswordManagerApplication.class.getResource(APP_ICON_PATH);
        if (iconUrl != null) {
            stage.getIcons().setAll(new Image(iconUrl.toExternalForm()));
        }
    }
}
