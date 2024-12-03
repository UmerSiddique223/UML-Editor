package ui;

import core.class_diagram.ClassDiagramCanvasPanel;
import core.class_diagram.ClassPanel;
import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class ClassDiagramToolbar extends VBox {

    private final Map<String, Consumer<Void>> toolActions = new HashMap<>();
    private ClassDiagramCanvasPanel classDiagramCanvasPanel;
    private UseCaseDiagramPanel useCasePanel;

    public ClassDiagramToolbar(Object panel) {
        setSpacing(10);
        setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px;");

        this.classDiagramCanvasPanel = (ClassDiagramCanvasPanel) panel;

        Button addClassButton = new Button("Add Class");

//        addClassButton.setOnAction(e -> classDiagramCanvasPanel.addClassToCanvas(new ClassPanel()));
//        ContextMenu contextMenu = new ContextMenu();

        addClassButton.setOnAction(ev -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Class");
            dialog.setHeaderText("Enter Class Name:");
            dialog.showAndWait().ifPresent(name -> classDiagramCanvasPanel.addClassToCanvas(new ClassPanel(name, false), 100, 100));
        });

        getChildren().add(addClassButton);

        Button button1 = new Button("Association");
        button1.setOnAction(e -> classDiagramCanvasPanel.setRelationship("association"));
        getChildren().add(button1);

        Button button2 = new Button("Composition");
        button2.setOnAction(e -> classDiagramCanvasPanel.setRelationship("composition"));
        getChildren().add(button2);

        Button button3 = new Button("Aggregation");
        button3.setOnAction(e -> classDiagramCanvasPanel.setRelationship("aggregation"));
        getChildren().add(button3);

        Button button4 = new Button("Inheritance");
        button4.setOnAction(e -> classDiagramCanvasPanel.setRelationship("inheritance"));
        getChildren().add(button4);
    }

}
