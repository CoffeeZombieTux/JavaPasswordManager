module com.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.passwordmanager;
    opens com.passwordmanager to javafx.fxml;
}
