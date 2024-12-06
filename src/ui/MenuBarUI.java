package ui;

import core.class_diagram.ClassDiagramCanvasPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class MenuBarUI extends MenuBar {

    public MenuBarUI(Stage parentStage) {
        // File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem exitApp = new MenuItem("Exit");

        saveFile.setOnAction(event -> {
            try {
                MainFrame.getClassDiagramCanvasPanel().saveDiagram(parentStage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        openFile.setOnAction(event -> showLoadDiagramWindow(parentStage));


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


    private void showLoadDiagramWindow(Stage parentStage) {
        Stage loadStage = new Stage();
        loadStage.setTitle("Load Diagram");
        loadStage.initModality(Modality.APPLICATION_MODAL);
        loadStage.initOwner(parentStage);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Label heading = new Label("Available Diagrams:");
        heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<String> fileList = new ListView<>();
        populateFileList(fileList);

        Button openButton = new Button("Open");
        openButton.setDisable(true);
        openButton.setOnAction(e -> {
            String selectedFile = fileList.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                try {
                    MainFrame.loadDiagram(new File("User diagrams" + File.separator + selectedFile));
                    // Call the load diagram logic
                } catch (Exception ex) {
                    showErrorDialog("Error", "Failed to load diagram: " + ex.getMessage());
                    System.out.println("Failed to load diagram: " + ex.getMessage());
                }
                loadStage.close();
            }
        });

        fileList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            openButton.setDisable(newValue == null);
        });

        layout.getChildren().addAll(heading, fileList, openButton);

        Scene scene = new Scene(layout, 400, 300);
        loadStage.setScene(scene);
        loadStage.show();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void populateFileList(ListView<String> fileList) {
        File folder = new File("User diagrams");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".xml"));
            if (files != null) {
                for (File file : files) {
                    fileList.getItems().add(file.getName());
                }
            }
        } else {
            System.out.println("The 'User diagrams' folder does not exist.");
        }
    }
}
