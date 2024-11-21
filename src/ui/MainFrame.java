package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.geometry.*;
import javafx.stage.Stage;

public class MainFrame extends Application {

    private BorderPane rootPane; // Main container
    private StackPane cardPane; // For switching between views
    private VBox homePanel;
    private CanvasPanel classDiagramPanel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("UML Editor");

        initializeComponents(primaryStage);

        Scene scene = new Scene(rootPane, 1100, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeComponents(Stage stage) {
        rootPane = new BorderPane();

        // Initialize MenuBar
        MenuBarUI menuBar = new MenuBarUI(stage);
        Menu fileMenu = new Menu("File");
        menuBar.getMenus().add(fileMenu);
        rootPane.setTop(menuBar);

        // Initialize Panels
        cardPane = new StackPane();
        initializeHomePanel();
        initializeClassDiagramPanel();

        cardPane.getChildren().addAll(homePanel);
        rootPane.setCenter(cardPane);
    }

    private void initializeHomePanel() {
        homePanel = new VBox(20);
        homePanel.setAlignment(Pos.CENTER);
        homePanel.setStyle("-fx-background-color: white;");

        Label heading = new Label("UML Editor");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4682b4;");

        Button classBtn = new Button("Class Diagram");
        Button useCaseBtn = new Button("Use Case Diagram");

        styleButton(classBtn);
        styleButton(useCaseBtn);

        classBtn.setOnAction(e -> showClassDiagram());
        useCaseBtn.setOnAction(e -> showAlert("Use Case Diagram is not implemented yet."));

        homePanel.getChildren().addAll(heading, classBtn, useCaseBtn);
    }

    private void initializeClassDiagramPanel() {
        classDiagramPanel = new CanvasPanel();
        BorderPane classPanelContainer = new BorderPane();
        classPanelContainer.setCenter(classDiagramPanel);
        classPanelContainer.setStyle("-fx-background-color: lightgray;");
    }

    private void styleButton(Button button) {
        button.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #4682b4; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 20 10 20;"
        );
    }

    private void showClassDiagram() {
        cardPane.getChildren().setAll(classDiagramPanel);

        // Add new diagram
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Class Diagram");
        dialog.setHeaderText("Enter Class Diagram Name:");
        dialog.showAndWait().ifPresent(name -> classDiagramPanel.addClassDiagram(name));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
