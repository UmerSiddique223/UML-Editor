package ui;
import core.usecase_diagram.UseCaseDiagramPanel;

import core.class_diagram.ClassDiagramCanvasPanel;
import core.class_diagram.ClassDiagram;
import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.stage.Stage;

public class MainFrame extends Application {

    private BorderPane rootPane; // Main container
    private StackPane cardPane; // For switching between views
    private VBox homePanel;
    private ClassDiagramCanvasPanel classDiagramCanvasPanel;
    private ClassDiagramToolbar classDiagramToolbar; // Left-side toolbar
    private UseCaseDiagramPanel useCaseDiagramPanel;

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
        MenuBar menuBar = new MenuBarUI(stage);
        rootPane.setTop(menuBar);

        // Initialize Panels
        cardPane = new StackPane();
        initializeHomePanel();

        cardPane.getChildren().add(homePanel);
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
        useCaseBtn.setOnAction(e -> showUseCaseDiagram());
        styleButton(classBtn);
        styleButton(useCaseBtn);

        classBtn.setOnAction(e -> showClassDiagram());

        homePanel.getChildren().addAll(heading, classBtn, useCaseBtn);
    }

    private void showUseCaseDiagram() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Use Case Diagram");
        dialog.setHeaderText("Enter Use Case Diagram Name:");
        dialog.showAndWait().ifPresent(name -> {
            useCaseDiagramPanel = new UseCaseDiagramPanel();
            useCaseDiagramPanel.setStyle("-fx-background-color: lightblue;");
            UsecaseToolbar useCaseToolbar = new UsecaseToolbar(useCaseDiagramPanel);
            rootPane.setLeft(useCaseToolbar);
            cardPane.getChildren().setAll(useCaseDiagramPanel);
            useCaseToolbar.setVisible(true);
        });
    }
    private void styleButton(Button button) {
        button.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #4682b4; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 20 10 20; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #1e90ff; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 20 10 20; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #4682b4; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 20 10 20; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        ));
    }

    private void showClassDiagram() {
        classDiagramCanvasPanel = new ClassDiagramCanvasPanel();
        classDiagramCanvasPanel.setStyle("-fx-background-color: lightgray;");

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Class Diagram");
        dialog.setHeaderText("Enter Class Diagram Name:");
        dialog.showAndWait().ifPresent(name -> {

            ClassDiagram classDiagram = new ClassDiagram(name);
            classDiagramToolbar = new ClassDiagramToolbar(classDiagramCanvasPanel);
            rootPane.setLeft(classDiagramToolbar);

            classDiagramCanvasPanel.setCurrentDiagram(classDiagram);

            cardPane.getChildren().setAll(classDiagramCanvasPanel);
            classDiagramToolbar.setVisible(true);

        });
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
