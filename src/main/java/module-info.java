module com.passwordmanager {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires org.scenicview.scenicview;
    requires static org.jetbrains.annotations;
    requires java.sql;

    exports com.passwordmanager.app;
    exports com.passwordmanager.model;
    exports com.passwordmanager.repository;
    exports com.passwordmanager.service;
    exports com.passwordmanager.storage;

    opens com.passwordmanager.model to com.fasterxml.jackson.databind;
    opens com.passwordmanager.ui.screen to javafx.fxml;
    opens com.passwordmanager.ui.component.detail to javafx.fxml;
    opens com.passwordmanager.ui.component.topbar to javafx.fxml;
    opens com.passwordmanager.ui.component.form to javafx.fxml;
    opens com.passwordmanager.ui.component.list to javafx.fxml;
    opens com.passwordmanager.ui.component.categories to javafx.fxml;
    opens com.passwordmanager.ui.component.types to javafx.fxml;
}
