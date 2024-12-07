package ui;

import core.class_diagram.ClassDiagramCanvasPanel;
import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class MenuBarUI extends MenuBar {
    private static final String USE_CASE_FOLDER = "User Diagrams/Use Case Diagrams";
    private static final String CLASS_DIAGRAM_FOLDER = "User Diagrams/Class Diagrams";


    public MenuBarUI(Stage parentStage) {
        // File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem exitApp = new MenuItem("Exit");

        saveFile.setOnAction(event -> {
            try {
                Pane currentPanel = MainFrame.getCurrentDiagramPanel();

                if (currentPanel instanceof ClassDiagramCanvasPanel) {
                    MainFrame.getClassDiagramCanvasPanel().saveDiagram(parentStage);
                } else if (currentPanel instanceof UseCaseDiagramPanel) {
                    data.DiagramSaver.saveUseCaseDiagram((UseCaseDiagramPanel) currentPanel);
                } else {
                    throw new UnsupportedOperationException("Unsupported diagram type.");
                }            } catch (Exception e) {
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

        undo.setOnAction(e -> {
            Pane currentPanel = MainFrame.getCurrentDiagramPanel();
            if (currentPanel instanceof UndoableDiagramPanel) {
                ((UndoableDiagramPanel) currentPanel).undo();
            }
        });

        redo.setOnAction(e -> {
            Pane currentPanel = MainFrame.getCurrentDiagramPanel();
            if (currentPanel instanceof UndoableDiagramPanel) {
                ((UndoableDiagramPanel) currentPanel).redo();
            }
        });
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

        Label classHeading = new Label("Class Diagrams:");
        classHeading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<String> classFileList = new ListView<>();
        populateFileList(classFileList, CLASS_DIAGRAM_FOLDER);

        Label useCaseHeading = new Label("Use Case Diagrams:");
        useCaseHeading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<String> useCaseFileList = new ListView<>();
        populateFileList(useCaseFileList, USE_CASE_FOLDER);

        Button openButton = new Button("Open");
        openButton.setDisable(true);

        // Handle selection between lists
        classFileList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            useCaseFileList.getSelectionModel().clearSelection();
            openButton.setDisable(newValue == null);
        });

        useCaseFileList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            classFileList.getSelectionModel().clearSelection();
            openButton.setDisable(newValue == null);
        });

        openButton.setOnAction(e -> {
            String selectedFile = classFileList.getSelectionModel().getSelectedItem();
            boolean isClassDiagram = selectedFile != null;
            if (!isClassDiagram) {
                selectedFile = useCaseFileList.getSelectionModel().getSelectedItem();
            }

            if (selectedFile != null) {
                try {
                    File file = new File((isClassDiagram ? CLASS_DIAGRAM_FOLDER : USE_CASE_FOLDER) + File.separator + selectedFile);

                    if (isClassDiagram) {
                        MainFrame.loadDiagram(file);
                    } else {
                        MainFrame.loadUseCaseDiagram(file);
                    }

                    loadStage.close();
                } catch (Exception ex) {
                    showErrorDialog("Error", "Failed to load diagram: " + ex.getMessage());
                }
            }
        });

        layout.getChildren().addAll(classHeading, classFileList, useCaseHeading, useCaseFileList, openButton);

        Scene scene = new Scene(layout, 400, 400);
        loadStage.setScene(scene);
        loadStage.show();
    }

    private void showErrorDialog(String error, String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(error);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    private void populateFileList(ListView<String> fileList, String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".xml"));
            if (files != null) {
                for (File file : files) {
                    fileList.getItems().add(file.getName());
                }
            }
        } else {
            System.out.println("The folder '" + folderPath + "' does not exist.");
        }
    }
}
