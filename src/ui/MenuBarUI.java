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
    private static final String MENU_BAR_STYLE =
            "-fx-background-color: #FFFFFF; " +
                    "-fx-padding: 5; "
                  ;

    private static final String MENU_STYLE =
            "-fx-font-size: 14px; " +
                    "-fx-font-weight: normal; " +
                    "-fx-text-fill: black;";

    private static final String MENU_ITEM_STYLE =
            "-fx-font-size: 13px; " +
                    "-fx-padding: 5 10 5 10;";

    private static final String MENU_ITEM_HOVER_STYLE =
            "-fx-background-color: rgba(30, 144, 255, 0.3); " + // Subtle hover effect
                    "-fx-text-fill: #ffffff;";

    public MenuBarUI(Stage parentStage) {
        // Apply styles to the MenuBar
        this.setStyle(MENU_BAR_STYLE);

        // File Menu
        Menu fileMenu = createStyledMenu("File");
        Menu newFileMenu = createStyledMenu("New");

        MenuItem newClassDiagram = createStyledMenuItem("Class Diagram");
        MenuItem newUseCaseDiagram = createStyledMenuItem("Use Case Diagram");

        newClassDiagram.setOnAction(e -> MainFrame.showClassDiagram("New Class Diagram"));
        newUseCaseDiagram.setOnAction(e -> MainFrame.showUseCaseDiagram("New Use Case Diagram"));

        newFileMenu.getItems().addAll(newClassDiagram, newUseCaseDiagram);

        MenuItem home = createStyledMenuItem("Home");
        home.setOnAction(e -> MainFrame.showHomePanel());

        MenuItem openFile = createStyledMenuItem("Open");
        openFile.setOnAction(event -> showLoadDiagramWindow(parentStage));

        MenuItem saveFile = createStyledMenuItem("Save");
        saveFile.setOnAction(event -> saveCurrentDiagram());

        MenuItem exitApp = createStyledMenuItem("Exit");
        exitApp.setOnAction(e -> parentStage.close());

        fileMenu.getItems().addAll(newFileMenu,  openFile, saveFile, new SeparatorMenuItem(), exitApp);

        // Edit Menu
        Menu editMenu = createStyledMenu("Edit");
        MenuItem undo = createStyledMenuItem("Undo");
        MenuItem redo = createStyledMenuItem("Redo");

        undo.setOnAction(e -> undoAction());
        redo.setOnAction(e -> redoAction());

        editMenu.getItems().addAll(undo, redo);

        // View Menu
        Menu viewMenu = createStyledMenu("View");
        MenuItem switchDiagram = createStyledMenuItem("Switch Diagram");
        switchDiagram.setOnAction(e -> switchDiagramPanel());
        viewMenu.getItems().add(switchDiagram);
        viewMenu.getItems().add(home);


        // Help Menu
        Menu helpMenu = createStyledMenu("Help");
        MenuItem about = createStyledMenuItem("About");
        about.setOnAction(e -> showAboutDialog(parentStage));
        helpMenu.getItems().add(about);

        // Add menus to the MenuBar
        this.getMenus().addAll(fileMenu, editMenu, viewMenu, helpMenu);
    }

    // Helper method to create styled menus
    private Menu createStyledMenu(String text) {
        Menu menu = new Menu(text);
        menu.setStyle(MENU_STYLE);
        return menu;
    }

    // Helper method to create styled menu items
    private MenuItem createStyledMenuItem(String text) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.setStyle(MENU_ITEM_STYLE);

        return menuItem;
    }

    private void saveCurrentDiagram() {
        try {
            Pane currentPanel = MainFrame.getCurrentDiagramPanel();
            if (currentPanel instanceof ClassDiagramCanvasPanel) {
                MainFrame.getClassDiagramCanvasPanel().saveDiagram();
            } else if (currentPanel instanceof UseCaseDiagramPanel) {
                data.DiagramSaver.saveUseCaseDiagram((UseCaseDiagramPanel) currentPanel);
            } else {
                throw new UnsupportedOperationException("Unsupported diagram type.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void undoAction() {
        Pane currentPanel = MainFrame.getCurrentDiagramPanel();
        if (currentPanel instanceof UndoableDiagramPanel) {
            ((UndoableDiagramPanel) currentPanel).undo();
        }
    }

    private void redoAction() {
        Pane currentPanel = MainFrame.getCurrentDiagramPanel();
        if (currentPanel instanceof UndoableDiagramPanel) {
            ((UndoableDiagramPanel) currentPanel).redo();
        }
    }

    private void switchDiagramPanel() {
        Pane currentPanel = MainFrame.getCurrentDiagramPanel();
        if (currentPanel instanceof ClassDiagramCanvasPanel) {
            MainFrame.showUseCaseDiagram("New Use Case Diagram");
        } else if (currentPanel instanceof UseCaseDiagramPanel) {
            MainFrame.showClassDiagram("New Class Diagram");
        }
    }

    private void showAboutDialog(Stage parentStage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(parentStage);
        alert.setTitle("About");
        alert.setHeaderText("UML Editor v1.0");
        alert.setContentText("A simple UML editor application built with JavaFX.");
        alert.showAndWait();
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
                        MainFrame.loadClassDiagram(file);
                    } else {
                        MainFrame.loadUseCaseDiagram(file);
                    }

                    loadStage.close();
                } catch (Exception ex) {
                    System.out.println("Failed to load diagram: " + ex);
                    showErrorDialog("Error", "Failed to load diagram: " + ex.getMessage());
                    System.out.println("Failed to load diagram: " + ex.getMessage());
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
