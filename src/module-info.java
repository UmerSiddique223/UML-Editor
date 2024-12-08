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
//    requires org.junit.jupiter.api;
    requires org.junit.platform.commons;
//    opens UMLMain;

    exports UMLMain;
    opens UMLMain to org.junit.platform.commons;
//    opens tests to org.junit.platform.commons;
}