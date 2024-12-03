module UML.Editor {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.base;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    exports ui to javafx.graphics;
    opens UMLMain;
}