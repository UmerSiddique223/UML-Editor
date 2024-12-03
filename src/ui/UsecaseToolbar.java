package ui;

import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;



import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

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

        // Set up event handlers for the buttons
        addActorButton.setOnAction(e -> useCaseDiagramPanel.setAddActorMode(true));
        addUserCaseButton.setOnAction(e -> useCaseDiagramPanel.setAddUseCaseMode(true));
        addRelationshipButton.setOnAction(e -> useCaseDiagramPanel.initiateRelationshipCreation());


        // Add buttons to the toolbar
        getChildren().addAll(addActorButton, addUserCaseButton, addRelationshipButton);
    }
}
