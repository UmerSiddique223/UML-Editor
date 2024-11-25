package core;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public  class ClassPanel extends VBox {
    public String ClassName;
    private final ArrayList<String> attributes = new ArrayList<>();
    private final ArrayList<String> methods = new ArrayList<>();

    final Label titleLabel;
    private final TextArea attributesArea;
    private final TextArea methodsArea;

    public ClassPanel(String className) {
        ClassName = className;
        setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: white;");
        setPrefSize(200, 150);

        // Title
        titleLabel = new Label(className);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        propagateEvents(titleLabel);
        // Attributes Section
        attributesArea = new TextArea("Attributes...");
        attributesArea.setEditable(false);
        attributesArea.setWrapText(true);
        propagateEvents(attributesArea);
        // Methods Section
        methodsArea = new TextArea("Methods...");
        methodsArea.setEditable(false);
        methodsArea.setWrapText(true);
        propagateEvents(methodsArea);
        getChildren().addAll(titleLabel, attributesArea, methodsArea);
        setOnMouseClicked(this::handleContextMenu);
    }

    private void propagateEvents(Control control) {
        control.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
            this.fireEvent(event);
        });
    }
    private void handleContextMenu(MouseEvent e) {
        if (e.getButton() == MouseButton.SECONDARY) {
            ContextMenu contextMenu = new ContextMenu();

            MenuItem addAttribute = new MenuItem("Add Attribute");
            addAttribute.setOnAction(ev -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add Attribute");
                dialog.setHeaderText("Enter Attribute:");
                dialog.showAndWait().ifPresent(attribute -> {
                    attributes.add(attribute);
                    updateAttributes();
                });
            });

            MenuItem addMethod = new MenuItem("Add Method");
            addMethod.setOnAction(ev -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add Method");
                dialog.setHeaderText("Enter Method:");
                dialog.showAndWait().ifPresent(method -> {
                    methods.add(method);
                    updateMethods();
                });
            });

            MenuItem delete = new MenuItem("Delete Class Diagram");
            delete.setOnAction(ev -> {
                Pane parent = (Pane) getParent();
                if (parent instanceof StackPane stackPane) {
                    Pane grandParent = (Pane) stackPane.getParent();
                    grandParent.getChildren().remove(stackPane);
                }
            });

            contextMenu.getItems().addAll(addAttribute, addMethod, delete);
            contextMenu.show(this, e.getScreenX(), e.getScreenY());
            e.consume();
        }
    }

    private void updateAttributes() {
        attributesArea.setText(String.join("\n", attributes));
    }

    private void updateMethods() {
        methodsArea.setText(String.join("\n", methods));
    }
}