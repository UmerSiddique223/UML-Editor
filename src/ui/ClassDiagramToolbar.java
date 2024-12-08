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


/**
 * A toolbar for managing tools related to class diagrams.
 * It includes buttons for adding classes, interfaces, and relationships
 * such as association, composition, aggregation, and inheritance in the diagram.
 */
public class ClassDiagramToolbar extends VBox {

    /** A map that stores tool actions associated with each tool name. */
    private final Map<String, Consumer<Void>> toolActions = new HashMap<>();

    /** The canvas panel for managing the class diagram. */
    private ClassDiagramCanvasPanel classDiagramCanvasPanel;

    /**
     * Constructs a ClassDiagramToolbar with the specified panel.
     * The toolbar is populated with buttons for adding classes, interfaces,
     * and various relationships to the class diagram.
     *
     * @param panel the panel (either a class diagram or use case diagram)
     *              associated with the toolbar.
     */
    public ClassDiagramToolbar(Object panel) {
        setSpacing(10);
        setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px;");

        this.classDiagramCanvasPanel = (ClassDiagramCanvasPanel) panel;

        Button addClassButton = new Button("Add Class");

//        addClassButton.setOnAction(e -> classDiagramCanvasPanel.addClassToCanvas(new ClassPanel()));
//        ContextMenu contextMenu = new ContextMenu();

        addClassButton.setOnAction(ev -> {
            classDiagramCanvasPanel.enableClassPlacementMode(false); // false = regular class, true = interface
        });
        Button addInterfaceButton = new Button("Add Interface");


        addInterfaceButton.setOnAction(ev -> {
            classDiagramCanvasPanel.enableClassPlacementMode(true); // false = regular class, true = interface
        });


        getChildren().add(addClassButton);
        getChildren().add(addInterfaceButton);

        Button button1 = new Button("Association");
        button1.setOnAction(e -> classDiagramCanvasPanel.setDrawingMode("association"));
        getChildren().add(button1);

        Button button2 = new Button("Composition");
        button2.setOnAction(e -> classDiagramCanvasPanel.setDrawingMode("composition"));
        getChildren().add(button2);

        Button button3 = new Button("Aggregation");
        button3.setOnAction(e -> classDiagramCanvasPanel.setDrawingMode("aggregation"));
        getChildren().add(button3);

        Button button4 = new Button("Inheritance");
        button4.setOnAction(e -> classDiagramCanvasPanel.setDrawingMode("inheritance"));
        getChildren().add(button4);
    }

}
