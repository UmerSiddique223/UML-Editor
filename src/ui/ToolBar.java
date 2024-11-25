package ui;

import core.CanvasPanel;
import core.UseCaseDiagramPanel;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ToolBar extends VBox {

    private final Map<String, Consumer<Void>> toolActions = new HashMap<>();
    private CanvasPanel canvasPanel;
    private UseCaseDiagramPanel useCasePanel;

    public ToolBar(Object panel) {
        setSpacing(10);
        setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px;");

        if (panel instanceof CanvasPanel) {
            this.canvasPanel = (CanvasPanel) panel;
        } else if (panel instanceof UseCaseDiagramPanel) {
            this.useCasePanel = (UseCaseDiagramPanel) panel;
        } else {
            throw new IllegalArgumentException("Unsupported panel type: " + panel.getClass().getName());
        }
    }

    public void addTool(String label, Consumer<Void> action) {
        Button button = new Button(label);
        button.setOnAction(e -> action.accept(null));
        getChildren().add(button);
        toolActions.put(label, action);
    }

    public void clearTools() {
        getChildren().clear();
        toolActions.clear();
    }

    public void loadToolsForDiagramType(String diagramType) {
        clearTools();
        if ("ClassDiagram".equals(diagramType) && canvasPanel != null) {
//            // Add tools specific to Class Diagrams
//            addTool("Aggregation", v -> setRelationshipTool("Aggregation"));
//            addTool("Inheritance", v -> setRelationshipTool("Inheritance"));
//            addTool("Composition", v -> setRelationshipTool("Composition"));
//            addTool("Association", v -> setRelationshipTool("Association"));
        } else if ("UseCaseDiagram".equals(diagramType) && useCasePanel != null) {
            addTool("Actor", v -> useCasePanel.setCurrentTool("Actor")); // Placeholder for position
            addTool("Use Case", v -> useCasePanel.setCurrentTool("UseCase")); // Placeholder for position
        }
    }

//    private void setRelationshipTool(String relationshipType) {
//        if (canvasPanel != null) {
//            canvasPanel.setDrawingMode(relationshipType);
//            System.out.println(relationshipType + " Tool Selected");
//        }
//    }
}
