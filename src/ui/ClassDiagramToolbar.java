package ui;

import core.class_diagram.ClassDiagramCanvasPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ClassDiagramToolbar extends VBox {

    private final ClassDiagramCanvasPanel classDiagramCanvasPanel;

    public ClassDiagramToolbar(Object panel) {
        this.classDiagramCanvasPanel = (ClassDiagramCanvasPanel) panel;

        // Set the VBox properties for better alignment and styling
        setSpacing(15);
        setPadding(new Insets(20)); // Add padding around the toolbar
        setAlignment(Pos.TOP_CENTER); // Center the buttons
        setStyle("-fx-background-color: #3d3c3c; -fx-border-color: #ccc; -fx-border-width: 0 1px 0 0;");

        // Create and style buttons
        Button addClassButton = createStyledButton("Add Class");
        addClassButton.setOnAction(ev -> classDiagramCanvasPanel.enableClassPlacementMode(false));

        Button addInterfaceButton = createStyledButton("Add Interface");
        addInterfaceButton.setOnAction(ev -> classDiagramCanvasPanel.enableClassPlacementMode(true));

        Button associationButton = createStyledButton("Association");
        associationButton.setOnAction(e -> classDiagramCanvasPanel.setDrawingMode("association"));

        Button compositionButton = createStyledButton("Composition");
        compositionButton.setOnAction(e -> classDiagramCanvasPanel.setDrawingMode("composition"));

        Button aggregationButton = createStyledButton("Aggregation");
        aggregationButton.setOnAction(e -> classDiagramCanvasPanel.setDrawingMode("aggregation"));

        Button inheritanceButton = createStyledButton("Inheritance");
        inheritanceButton.setOnAction(e -> classDiagramCanvasPanel.setDrawingMode("inheritance"));

        // Add buttons to the VBox
        getChildren().addAll(
                addClassButton,
                addInterfaceButton,
                associationButton,
                compositionButton,
                aggregationButton,
                inheritanceButton
        );
    }

    /**
     * Creates a styled button with consistent look and feel.
     *
     * @param text the text to display on the button
     * @return the styled button
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(150); // Set a consistent width for all buttons
        button.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 5 10; " +
                        "-fx-font-size: 14px;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #f5f5f5; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 5 10; " +
                        "-fx-font-size: 14px;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 5 10; " +
                        "-fx-font-size: 14px;"
        ));
        return button;
    }
}