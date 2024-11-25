package ui;

import core.CanvasPanel;
import core.UseCaseDiagramPanel;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


import core.CanvasPanel;
import core.UseCaseDiagramPanel;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class ClassDiagramToolbar extends VBox {

    private final Map<String, Consumer<Void>> toolActions = new HashMap<>();
    private CanvasPanel canvasPanel;
    private UseCaseDiagramPanel useCasePanel;

    public ClassDiagramToolbar(Object panel) {
        setSpacing(10);
        setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px;");

        this.canvasPanel = (CanvasPanel) panel;

        Button button1 = new Button("Association");
        button1.setOnAction(e -> canvasPanel.setRelationship("association"));
        getChildren().add(button1);

        Button button2 = new Button("Composition");
        button2.setOnAction(e -> canvasPanel.setRelationship("composition"));
        getChildren().add(button2);

        Button button3 = new Button("Aggregation");
        button3.setOnAction(e -> canvasPanel.setRelationship("aggregation"));
        getChildren().add(button3);

        Button button4 = new Button("Inheritance");
        button4.setOnAction(e -> canvasPanel.setRelationship("inheritance"));
        getChildren().add(button4);
    }

}
