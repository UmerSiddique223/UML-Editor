package ui;

import core.class_diagram.ClassDiagramCanvasPanel;
import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UsecaseToolbar extends VBox {

    private final Map<String, Consumer<Void>> toolActions = new HashMap<>();
    private ClassDiagramCanvasPanel classDiagramCanvasPanel;
    private UseCaseDiagramPanel useCasePanel;

    public UsecaseToolbar(Object panel) {
        setSpacing(10);
        setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px;");

        if (panel instanceof ClassDiagramCanvasPanel) {
            this.classDiagramCanvasPanel = (ClassDiagramCanvasPanel) panel;
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
        if ("ClassDiagram".equals(diagramType) && classDiagramCanvasPanel != null) {

        } else if ("UseCaseDiagram".equals(diagramType) && useCasePanel != null) {
            addTool("Actor", v -> useCasePanel.setCurrentTool("Actor")); // Placeholder for position
            addTool("Use Case", v -> useCasePanel.setCurrentTool("UseCase")); // Placeholder for position
        }
    }


}
