package ui;

import core.class_diagram.ClassDiagramCanvasPanel;
import core.class_diagram.ClassDiagram;
import core.usecase_diagram.UseCaseDiagramPanel;
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
    private ClassDiagramCanvasPanel classDiagramCanvasPanel;
    private UsecaseToolbar usecaseToolbar; // Left-side toolbar
    private ClassDiagramToolbar classDiagramToolbar; // Left-side toolbar

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

        styleButton(classBtn);
        styleButton(useCaseBtn);
        useCaseBtn.setOnAction(e -> showUseCaseDiagram());

        classBtn.setOnAction(e -> showClassDiagram());

        homePanel.getChildren().addAll(heading, classBtn, useCaseBtn);
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
        // Create the canvas panel
        classDiagramCanvasPanel = new ClassDiagramCanvasPanel();
        classDiagramCanvasPanel.setStyle("-fx-background-color: lightgray;");
        classDiagramCanvasPanel.setPrefSize(2000, 2000); // Set a large preferred size for scrolling

        // Wrap the canvas panel in a ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(classDiagramCanvasPanel); // Set the canvas as content
        scrollPane.setPannable(true);                   // Allow panning
        scrollPane.setFitToWidth(false);                // Disable auto-fit for width
        scrollPane.setFitToHeight(false);               // Disable auto-fit for height

        // Display a dialog to get the diagram name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Class Diagram");
        dialog.setHeaderText("Enter Class Diagram Name:");
        dialog.showAndWait().ifPresent(name -> {
            if (name.trim().isEmpty()) {
                System.out.println("Class diagram name cannot be empty.");
                return;
            }

            // Create a new class diagram
            ClassDiagram classDiagram = new ClassDiagram(name);

            // Create the toolbar and associate it with the canvas
            classDiagramToolbar = new ClassDiagramToolbar(classDiagramCanvasPanel);
            rootPane.setLeft(classDiagramToolbar);

            // Set the current diagram in the canvas
            classDiagramCanvasPanel.setCurrentDiagram(classDiagram);

            // Replace the contents of cardPane with the scrollable canvas
            cardPane.getChildren().setAll(scrollPane);

            // Make the toolbar visible
            classDiagramToolbar.setVisible(true);
        });
    }


    private void showUseCaseDiagram() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Use Case Diagram");
        dialog.setHeaderText("Enter Use Case Diagram Name:");
        dialog.showAndWait().ifPresent(name -> {
            UseCaseDiagramPanel useCaseDiagramPanel = new UseCaseDiagramPanel(name);
            usecaseToolbar = new UsecaseToolbar(useCaseDiagramPanel);
            usecaseToolbar.loadToolsForDiagramType("UseCaseDiagram");
            rootPane.setLeft(usecaseToolbar);
            usecaseToolbar.setVisible(true);

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
