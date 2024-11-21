package ui;

import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class MenuBarUI extends MenuBar {

    public MenuBarUI(Stage parentStage) {
        // File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem exitApp = new MenuItem("Exit");

        exitApp.setOnAction(e -> parentStage.close()); // Close the application
        fileMenu.getItems().addAll(newFile, openFile, saveFile, new SeparatorMenuItem(), exitApp);

        // Edit Menu
        Menu editMenu = new Menu("Edit");

        MenuItem undo = new MenuItem("Undo");
        MenuItem redo = new MenuItem("Redo");
        MenuItem delete = new MenuItem("Delete");

        editMenu.getItems().addAll(undo, redo, delete);

        // View Menu
        Menu viewMenu = new Menu("View");

        MenuItem zoomIn = new MenuItem("Zoom In");
        MenuItem zoomOut = new MenuItem("Zoom Out");
        MenuItem switchDiagram = new MenuItem("Switch Diagram");

        viewMenu.getItems().addAll(zoomIn, zoomOut, new SeparatorMenuItem(), switchDiagram);

        // Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initOwner(parentStage);
            alert.setTitle("About");
            alert.setHeaderText("UML Editor v1.0");
            alert.setContentText("A simple UML editor application built with JavaFX.");
            alert.showAndWait();
        });

        helpMenu.getItems().add(about);

        // Add Menus to the MenuBar
        this.getMenus().addAll(fileMenu, editMenu, viewMenu, helpMenu);
    }
}
