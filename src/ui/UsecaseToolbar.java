package ui;

import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class UsecaseToolbar extends VBox {

    private final UseCaseDiagramPanel useCaseDiagramPanel;

    public UsecaseToolbar(UseCaseDiagramPanel useCaseDiagramPanel) {
        this.useCaseDiagramPanel = useCaseDiagramPanel;

        // Style the VBox for better alignment and appearance
        setSpacing(15);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #3d3c3c; -fx-border-color: #ccc; -fx-border-width: 0 1px 0 0;");

        // Create and style buttons
        Button addActorButton = createStyledButton("Add Actor");
        addActorButton.setOnAction(e -> {
            resetOtherModes();
            useCaseDiagramPanel.setAddActorMode(true);
            System.out.println("Add Actor mode activated.");
        });

        Button addUseCaseButton = createStyledButton("Add Use Case");
        addUseCaseButton.setOnAction(e -> {
            resetOtherModes();
            useCaseDiagramPanel.setAddUseCaseMode(true);
            System.out.println("Add Use Case mode activated.");
        });

        Button addRelationshipButton = createStyledButton("Add Relationship");
        addRelationshipButton.setOnAction(e -> {
            resetOtherModes();
            useCaseDiagramPanel.initiateRelationshipCreation();
            System.out.println("Add Relationship mode activated.");
        });

        Button dragModeButton = createStyledButton("Drag/Move Elements");
        dragModeButton.setOnAction(e -> {
            resetOtherModes();
            useCaseDiagramPanel.setDragMode(true);
            System.out.println("Drag Mode activated.");
        });

        Button editTextButton = createStyledButton("Add/Edit Text");
        editTextButton.setOnAction(e -> {
            useCaseDiagramPanel.setEditTextMode(true);
            System.out.println("Edit Text mode activated. Click on any actor to edit its text.");
        });

        Button deleteMenuItem = createStyledButton("Delete");
        deleteMenuItem.setOnAction(event -> useCaseDiagramPanel.setDeleteMode(true));

        // Add buttons to the toolbar
        getChildren().addAll(
                addActorButton,
                addUseCaseButton,
                addRelationshipButton,
                dragModeButton,
                editTextButton,
                deleteMenuItem
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

    /**
     * Method to reset all modes before activating a new one
     */
    private void resetOtherModes() {
        useCaseDiagramPanel.setAddActorMode(false);
        useCaseDiagramPanel.setAddUseCaseMode(false);
        useCaseDiagramPanel.setDragMode(false);
        useCaseDiagramPanel.setRelationshipCreationMode(false);
    }
}
