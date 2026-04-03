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

    exports com.passwordmanager.app;
    exports com.passwordmanager.model;
    exports com.passwordmanager.repository;
    exports com.passwordmanager.service;
    exports com.passwordmanager.storage;

    opens com.passwordmanager.model to com.fasterxml.jackson.databind;
    opens com.passwordmanager.ui to javafx.fxml;
}
