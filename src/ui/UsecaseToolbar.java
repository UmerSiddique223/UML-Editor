package ui;

import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class UsecaseToolbar extends VBox {
    private UseCaseDiagramPanel useCaseDiagramPanel;

    public UsecaseToolbar(UseCaseDiagramPanel useCaseDiagramPanel) {
        this.useCaseDiagramPanel = useCaseDiagramPanel;

        // Style the toolbar
        setSpacing(10);
        setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px;");

        // Add buttons for use case diagram-specific actions
        Button addActorButton = new Button("Add Actor");
        Button addUserCaseButton = new Button("Add Use Case");
        Button addRelationshipButton = new Button("Add Relationship");
        Button dragModeButton = new Button("Drag/Move Elements");

        // Set up event handlers for the buttons
        addActorButton.setOnAction(e -> {
            resetOtherModes();
            useCaseDiagramPanel.setAddActorMode(true);
            System.out.println("Add Actor mode activated.");
        });
        addUserCaseButton.setOnAction(e -> {
            resetOtherModes();
            useCaseDiagramPanel.setAddUseCaseMode(true);
            System.out.println("Add Use Case mode activated.");
        });
        addRelationshipButton.setOnAction(e -> {
            resetOtherModes();
            useCaseDiagramPanel.initiateRelationshipCreation();
            System.out.println("Add Relationship mode activated.");
        });
        dragModeButton.setOnAction(e -> {
            resetOtherModes();
            useCaseDiagramPanel.setDragMode(true);
            System.out.println("Drag Mode activated.");
        });

        // Add buttons to the toolbar
        getChildren().addAll(addActorButton, addUserCaseButton, addRelationshipButton, dragModeButton);
    }

    // Method to reset all modes before activating a new one
    private void resetOtherModes() {
        useCaseDiagramPanel.setAddActorMode(false);
        useCaseDiagramPanel.setAddUseCaseMode(false);
        useCaseDiagramPanel.setDragMode(false);
        useCaseDiagramPanel.setRelationshipCreationMode(false);
    }
}
