package ui;

import bean.DragResizeBean;
import core.Circle;
import core.DrawingState;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.stage.Stage;

import java.util.Optional;

public class MainFrame extends Application {

    private BorderPane rootPane; // Main container
    private StackPane cardPane; // For switching between views
    private VBox homePanel;
    private Circle.CanvasPanel canvasPanel;
    private ToolBar toolBar; // Left-side toolbar

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
        MenuBar menuBar = initializeMenuBar(stage);
        rootPane.setTop(menuBar);

        // Initialize Panels
        cardPane = new StackPane();
        initializeHomePanel();
        initializeCanvasPanel();

        // Initialize ToolBar
        cardPane.getChildren().add(homePanel);
        rootPane.setCenter(cardPane);
    }

    private MenuBar initializeMenuBar(Stage stage) {
        return new MenuBarUI(stage);
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
        useCaseBtn.setOnAction(e -> showUseCaseDiagram());

        classBtn.setOnAction(e -> showClassDiagram());

        homePanel.getChildren().addAll(heading, classBtn, useCaseBtn);
    }

    private void initializeCanvasPanel() {
        canvasPanel = new Circle.CanvasPanel();
        canvasPanel.setStyle("-fx-background-color: lightgray;");
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
//        cardPane.getChildren().setAll(canvasPanel);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create a Class Diagram");
        dialog.setHeaderText("Enter a name for Class Diagram:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {

            cardPane.getChildren().setAll(canvasPanel);
            String name = result.get();
//            DragResizeBean.ClassDiagramPanel classDiagramPanel = new DragResizeBean.ClassDiagramPanel(name);
            toolBar = new ToolBar(canvasPanel);
            toolBar.loadToolsForDiagramType("ClassDiagram");
            rootPane.setLeft(toolBar);
//            canvasPanel.addDiagramToCanvas(classDiagramPanel, 50, 50);
            toolBar.setVisible(true);
        }
        else {
            System.out.println("Dialog was canceled.");
        }

    }

    private void showUseCaseDiagram() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Use Case Diagram");
        dialog.setHeaderText("Enter Use Case Diagram Name:");
        dialog.showAndWait().ifPresent(name -> {
            DrawingState.UseCaseDiagramPanel useCaseDiagramPanel = new DrawingState.UseCaseDiagramPanel(name);
            toolBar = new ToolBar(useCaseDiagramPanel);
            toolBar.loadToolsForDiagramType("UseCaseDiagram");
            rootPane.setLeft(toolBar);
            toolBar.setVisible(true);

            cardPane.getChildren().setAll(useCaseDiagramPanel);
        });

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
