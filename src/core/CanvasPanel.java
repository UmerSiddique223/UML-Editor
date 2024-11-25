package core;

import bean.DragResizeBean;
import javafx.scene.Node;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import ui.AbstractDiagramPanel;

public class CanvasPanel extends Pane {
    ClassDiagram diagram;
    private String drawingMode = "";

    private Line tempLine;

    public CanvasPanel() {
        setStyle("-fx-background-color: white;");
        setPrefSize(800, 600);

        setOnMouseClicked(this::handleMouseClick);
//        setOnMouseMoved(this::handleMouseMove); // Add dynamic line updates.

    }

    public void setCurrentDiagram(ClassDiagram diagram) {
        this.diagram = diagram;
    }

    private void showContextMenu(double screenX, double screenY, double x, double y) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem addClassDiagram = new MenuItem("Add Class");
        addClassDiagram.setOnAction(ev -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Class");
            dialog.setHeaderText("Enter Class Name:");
            dialog.showAndWait().ifPresent(name -> addClassToCanvas(new ClassPanel(name), x, y));
        });

        contextMenu.getItems().add(addClassDiagram);
        contextMenu.show(this, screenX, screenY);
    }

    public void addClassToCanvas(ClassPanel classPanel, double x, double y) {
        javafx.scene.shape.Rectangle border = new Rectangle(200, 150);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.BLACK);

        StackPane container = new StackPane(border, classPanel);
        container.setLayoutX(x);
        container.setLayoutY(y);

        DragResizeBean.apply(container, this);
        getChildren().add(container);

        // Adding class to the Diagram class too:
        diagram.addClass(classPanel);
    }

    private void handleMouseClick(MouseEvent event) {
        if (drawingMode.isEmpty()) {
            if (event.getButton() == MouseButton.SECONDARY) {
                showContextMenu(event.getScreenX(), event.getScreenY(), event.getX(), event.getY());
            }
            return;
        }
    }
//        AbstractDiagramPanel clickedDiagram = findDiagramAt(event);
//
//        if (clickedDiagram == null) {
//            return;
//        }
//
//        if (startDiagram == null) {
//            startDiagram = clickedDiagram;
//            System.out.println("Start diagram selected.");
//            createTempLine(startDiagram.getLayoutX() + startDiagram.getWidth() / 2,
//                    startDiagram.getLayoutY() + startDiagram.getHeight() / 2);
//        } else if (clickedDiagram != startDiagram) {
//            endDiagram = clickedDiagram;
//            System.out.println("End diagram selected.");
//            finalizeRelationship();
//        }
//
//    }


//    private void handleMouseMove(MouseEvent event) {
//        if (tempLine != null) {
//            updateTempLine(event.getX(), event.getY());
//        }
//    }

//    private void createTempLine(double x, double y) {
//        tempLine = new Line();
//        tempLine.setStartX(x);
//        tempLine.setStartY(y);
//        tempLine.setEndX(x);
//        tempLine.setEndY(y);
//        tempLine.setStroke(Color.GRAY);
//        tempLine.getStrokeDashArray().addAll(5.0, 5.0);
//
//        getChildren().add(tempLine);
//    }
//
//    private void updateTempLine(double x, double y) {
//        if (tempLine != null) {
//            tempLine.setEndX(x);
//            tempLine.setEndY(y);
//        }
//    }
//
//    private void clearTempLine() {
//        if (tempLine != null) {
//            getChildren().remove(tempLine);
//            tempLine = null;
//        }
//    }

    public void setRelationship(String relationshipType) {
        if (diagram == null || diagram.classes.isEmpty()) {
            System.out.println("No classes available to create a relationship.");
            return;
        }

        ClassPanel startClass = null;
        ClassPanel endClass = null;

        // Prompt user to select the start class
        ChoiceDialog<String> startClassDialog = new ChoiceDialog<>();
        startClassDialog.getItems().addAll(diagram.getClassList());
        startClassDialog.setTitle("Select Start Class");
        startClassDialog.setHeaderText("Select the start class for the relationship:");
        startClassDialog.setContentText("Start Class:");

        String startClassName = startClassDialog.showAndWait().orElse(null);
        if (startClassName == null) {
            System.out.println("Start class selection was canceled.");
            return;
        }
        else {
            startClass = diagram.getClass(startClassName);
            if (startClass == null) {
                System.out.println("Class " + startClassName + " not found.");
                return;
            }
        }

        // Prompt user to select the end class
        ChoiceDialog<String> endClassDialog = new ChoiceDialog<>();
        endClassDialog.getItems().addAll(diagram.getClassList());
        endClassDialog.setTitle("Select End Class");
        endClassDialog.setHeaderText("Select the end class for the relationship:");
        endClassDialog.setContentText("End Class:");

        String endClassName = endClassDialog.showAndWait().orElse(null);
        if (endClassName == null) {
            System.out.println("End class selection was canceled.");
            return;
        }
        else {
            endClass = diagram.getClass(endClassName);
            if (endClass == null) {
                System.out.println("Class " + endClassName + " not found.");
                return;
            }
        }

        // Prevent creating a relationship with the same class
        if (startClass == endClass) {
            System.out.println("Cannot create a relationship between the same class.");
            return;
        }

        // Drawing Lines:

        if (relationshipType == "association"){

            // Get parent StackPanes
            StackPane startParent = (StackPane) startClass.getParent();
            StackPane endParent = (StackPane) endClass.getParent();

            // Create a line and bind its start and end points to the center positions of the StackPanes
            Line relationshipLine = new Line();
            relationshipLine.setStroke(Color.BLACK);

            // Bind start points
            relationshipLine.startXProperty().bind(startParent.layoutXProperty().add(startParent.widthProperty().divide(2)));
            relationshipLine.startYProperty().bind(startParent.layoutYProperty().add(startParent.heightProperty().divide(2)));

            // Bind end points
            relationshipLine.endXProperty().bind(endParent.layoutXProperty().add(endParent.widthProperty().divide(2)));
            relationshipLine.endYProperty().bind(endParent.layoutYProperty().add(endParent.heightProperty().divide(2)));

            // Add the line to the canvas
            getChildren().add(relationshipLine);

            System.out.println("Relationship added between " + startClass.ClassName + " and " + endClass.ClassName + ".");
        }

        else if (relationshipType == "composition") {
            // Get parent StackPanes
            StackPane wholeParent = (StackPane) startClass.getParent();
            StackPane partParent = (StackPane) endClass.getParent();

            // Create a line and bind its start and end points
            Line compositionLine = new Line();
            compositionLine.setStroke(Color.BLACK);

            // Bind start points
            compositionLine.startXProperty().bind(wholeParent.layoutXProperty().add(wholeParent.widthProperty().divide(2)));
            compositionLine.startYProperty().bind(wholeParent.layoutYProperty().add(wholeParent.heightProperty().divide(2)));

            // Bind end points
            compositionLine.endXProperty().bind(partParent.layoutXProperty().add(partParent.widthProperty().divide(2)));
            compositionLine.endYProperty().bind(partParent.layoutYProperty().add(partParent.heightProperty().divide(2)));

            // Create a diamond shape for the composition
            Polygon diamond = new Polygon();
            diamond.getPoints().addAll(
                    0.0, 0.0, // Top point
                    10.0, 10.0, // Right point
                    0.0, 20.0, // Bottom point
                    -10.0, 10.0 // Left point
            );
            diamond.setFill(Color.BLACK);

            // Bind the diamond's position to the start of the line
            diamond.layoutXProperty().bind(compositionLine.startXProperty());
            diamond.layoutYProperty().bind(compositionLine.startYProperty().subtract(10)); // Offset for center alignment

            // Add the line and diamond to the canvas
            getChildren().addAll(compositionLine, diamond);

            System.out.println("Composition relationship added between " + startClass.ClassName + " (whole) and " + endClass.ClassName + " (part).");

        }
    }


//    private void resetDrawingState() {
//        startDiagram = null;
//        endDiagram = null;
//        drawingMode = "";
//    }

//    private AbstractDiagramPanel findDiagramAt(MouseEvent event) {
//        Node node = (Node) event.getTarget();
//        while (node != null && !(node instanceof AbstractDiagramPanel)) {
//            node = node.getParent();
//        }
//        if (node == null) {
//            System.out.println("No diagram found at clicked position.");
//        } else {
//
//            System.out.println("Found diagram: " + ((AbstractDiagramPanel) node).getClass().getName());
//        }
//        return (AbstractDiagramPanel) node;
//    }

}