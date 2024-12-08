package core.class_diagram;

import data.DiagramSaver;
import bean.DragResizeBean;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// Canvas for Class Diagrams
public class ClassDiagramCanvasPanel extends Pane {
    private boolean isDragSource = false;

    ClassDiagram diagram;
    private String drawingMode = "";

    private ArrayList<Relationship> relationships = new ArrayList<>(); // Store relationships

    public ClassDiagramCanvasPanel() {
        setStyle("-fx-background-color: white;");
        setPrefSize(800, 600);

        setOnMouseClicked(this::handleMouseClick);

//        setOnDragDetected(event -> {
//            isDragSource = true;
//            startFullDrag(); // Enable full drag-and-drop gesture
//            event.consume();
//        });
//
//        // Handle drag drop
//        setOnMouseDragReleased(event -> {
//            if (!isDragSource) {
//                ClassPanel sourceClass = getSourceClassPanel();
//                ClassPanel targetClass = this.diagram.getClassAt(event.getX(), event.getY());
//
//                if (sourceClass != null && sourceClass != targetClass) {
//                    setRelationship("association", sourceClass.getClassName(), targetClass.getClassName());
//                }
//            }
//            isDragSource = false;
//            event.consume();
//        });
//        setOnMouseMoved(this::handleMouseMove); // Add dynamic line updates.

    }
//    private ClassPanel getSourceClassPanel() {
//        // Logic to determine the source class panel from drag context.
//        return (ClassPanel) getScene().lookup(".drag-source"); // Example mechanism
//    }
    public ClassDiagram getDiagram() {
        return diagram;
    }

    public void setCurrentDiagram(ClassDiagram diagram) {
        this.diagram = diagram;
    }

    public void createAndAddClassToCanvasAt(double x, double y, boolean isInterface) {
        String newName = "Class" + (diagram.getClasses().size() + 1);
        ClassPanel newClass = new ClassPanel(newName, isInterface, x, y, this);
        addClassToCanvas(newClass, x, y);
    }

    private void showContextMenu(double screenX, double screenY, double x, double y) {
        ContextMenu contextMenu = new ContextMenu();

        // Option to add a Class
        MenuItem addClassDiagram = new MenuItem("Add Class");
        addClassDiagram.setOnAction(ev -> {
            addClassToCanvas(new ClassPanel("Class" + (diagram.getClasses().size() + 1), false, x, y, this), x, y);
        });

        // Option to add an Interface
        MenuItem addInterfaceDiagram = new MenuItem("Add Interface");
        addInterfaceDiagram.setOnAction(ev -> {
            addClassToCanvas(new ClassPanel("Interface" + (diagram.getClasses().size() + 1), true, x, y, this), x, y);
        });

        contextMenu.getItems().addAll(addClassDiagram, addInterfaceDiagram);
        contextMenu.show(this, screenX, screenY);

        // Add a click listener to hide the context menu when clicking outside
        getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!contextMenu.isShowing()) {
                return; // No need to hide if the menu is already hidden
            }

            if (!contextMenu.getSkin().getNode().contains(event.getX(), event.getY())) {
                contextMenu.hide(); // Hide the context menu
            }
        });
    }



    public void saveDiagram() throws Exception {
        System.out.println(diagram.Name + "  Name");
        System.out.println(diagram + "    hnjnjkdssd");
        if (diagram == null) {
            System.out.println("No diagram to save.");
            return;
        }



        DiagramSaver.saveDiagram(diagram);
    }

    public Consumer<ClassPanel> onClassAdded = classPanel -> {
    };
    public Consumer<ClassPanel> onClassRemoved = classPanel -> {
    };
    public BiConsumer<ClassPanel, String> onClassRename = (classPanel, oldName) -> {};


    public void setOnClassAdded(Consumer<ClassPanel> listener) {
        onClassAdded = listener;
    }

    public void setOnClassRemoved(Consumer<ClassPanel> listener) {
        onClassRemoved = listener;
    }

    public void setOnClassRename(BiConsumer<ClassPanel, String> listener) {
        onClassRename = listener;
    }
    public void addClassToCanvas(ClassPanel classPanel, double x, double y) {
        javafx.scene.shape.Rectangle border = new Rectangle(200, 150);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.BLACK);

        StackPane container = new StackPane(border, classPanel);

        container.setLayoutX(x);
        container.setLayoutY(y);

        // Appling the Dragging and Resizing functionality
        DragResizeBean.apply(container, this, classPanel.getClassName());

//        container.setOnMouseReleased(event -> handleClassDragEnd(container));


        getChildren().add(container);

        diagram.addClass(classPanel);

        onClassAdded.accept(classPanel);

    }

    public void enableClassPlacementMode(boolean isInterface) {
        // Change the cursor to a plus icon
        setCursor(Cursor.CROSSHAIR); // or use a custom cursor if preferred

        // Set a one-time click listener
        setOnMouseClicked(event -> {
            // Get the mouse click location
            double x = event.getX();
            double y = event.getY();

            // Create and add the class
            createAndAddClassToCanvasAt(x, y, isInterface);

            // Reset the cursor and remove the listener
            setCursor(Cursor.DEFAULT);
            setOnMouseClicked(null); // Remove the listener to avoid creating multiple classes
        });
    }

    private double dragStartX;
    private double dragStartY;


    private void handleClassDragEnd(StackPane container) {
        // After the mouse is released, check for overlaps and adjust position
        for (javafx.scene.Node node : getChildren()) {
            if (node instanceof StackPane && node != container) {
                StackPane otherContainer = (StackPane) node;
                if (isOverlapping(container, otherContainer)) {
                    // Reposition the container outside the other class to avoid overlap
                    double newX = container.getLayoutX();
                    double newY = container.getLayoutY();

                    // Find an empty space where the container can be placed outside the other container
                    if (newX + container.getWidth() > otherContainer.getLayoutX() &&
                            newX < otherContainer.getLayoutX() + otherContainer.getWidth()) {
                        newX = otherContainer.getLayoutX() + otherContainer.getWidth() + 10; // Move to the right of the other class
                    }

                    if (newY + container.getHeight() > otherContainer.getLayoutY() &&
                            newY < otherContainer.getLayoutY() + otherContainer.getHeight()) {
                        newY = otherContainer.getLayoutY() + otherContainer.getHeight() + 10; // Move below the other class
                    }

                    // Apply the adjusted position to avoid overlap
                    container.setLayoutX(newX);
                    container.setLayoutY(newY);
                    return;
                }
            }
        }
    }

    public void updatePosition(String className, double x, double y) {
        System.out.println("Updating position of " + className + " to (" + x + ", " + y + ")");
        if (diagram.getClass(className) != null) {
            diagram.getClass(className).setPosition(x, y);
        }

    }

    // Method to check if two StackPanes are overlapping
    private boolean isOverlapping(StackPane container, StackPane otherContainer) {
        double x1 = container.getLayoutX();
        double y1 = container.getLayoutY();
        double w1 = container.getWidth();
        double h1 = container.getHeight();

        double x2 = otherContainer.getLayoutX();
        double y2 = otherContainer.getLayoutY();
        double w2 = otherContainer.getWidth();
        double h2 = otherContainer.getHeight();

        // Check for overlap on X and Y axis
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    private void handleMouseClick(MouseEvent event) {
        if (drawingMode.isEmpty()) {
            if (event.getButton() == MouseButton.SECONDARY) {
                showContextMenu(event.getScreenX(), event.getScreenY(), event.getX(), event.getY());
            }
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

    private void createDeleteContextMenu(final Line relationshipLine, final String startClassName, final String endClassName, final String relationshipType) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        contextMenu.getItems().add(deleteItem);

        deleteItem.setOnAction(event -> {
            // Remove the visual representation of the relationship
            getChildren().remove(relationshipLine);
            getChildren().removeIf(node -> {
                if (node instanceof Polygon) {
                    Polygon polygon = (Polygon) node;
                    return polygon.layoutXProperty().isBound() && polygon.layoutXProperty().get() == relationshipLine.startXProperty().get();
                }
                return false;
            });
            // Remove the relationship from the list based on its type
            relationships.removeIf(rel ->
                    rel.getStartClass().equals(startClassName) &&
                            rel.getEndClass().equals(endClassName) &&
                            rel.getType().equals(relationshipType)
            );

            // Remove the relationship from the diagram
            diagram.removeRelationship(startClassName, endClassName, relationshipType);
        });

        // Create an invisible hitbox around the line
        Rectangle hitBox = new Rectangle();
        hitBox.setFill(Color.TRANSPARENT); // Make it invisible
        hitBox.setStrokeWidth(0); // Ensure no visual border

        // Match the dimensions of the hitbox to the line's bounds, with a buffer
        updateHitBox(hitBox, relationshipLine);

        // Add the hitbox to the scene (if needed)
        getChildren().add(hitBox);

        // Update the hitbox when the line changes
        relationshipLine.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
            updateHitBox(hitBox, relationshipLine);
        });

        // Show the context menu on a click within the hitbox
        hitBox.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // Right-click
                contextMenu.show(hitBox, event.getScreenX(), event.getScreenY());
                event.consume();
            }
        });
    }

    // Helper function to update the hitbox's bounds
    private void updateHitBox(Rectangle hitBox, Line relationshipLine) {
        Bounds bounds = relationshipLine.getBoundsInParent();

        // Add a buffer around the line for easier interaction
        double buffer = 10;
        hitBox.setX(bounds.getMinX() - buffer);
        hitBox.setY(bounds.getMinY() - buffer);
        hitBox.setWidth(bounds.getWidth() + 2 * buffer);
        hitBox.setHeight(bounds.getHeight() + 2 * buffer);
    }




    public void setRelationship(String relationshipType, String startingClass, String endingClass) {
        if (diagram == null || diagram.classes.isEmpty()) {
            System.out.println("No classes available to create a relationship.");
            return;
        }


        ClassPanel startClass = null;
        ClassPanel endClass = null;

        // Prompt user to select the start class
        if (startingClass == null || endingClass == null) {
            // Handle the case where either startingClass or endingClass is null

            ChoiceDialog<String> startClassDialog = new ChoiceDialog<>();
            startClassDialog.getItems().addAll(diagram.getClassList());
            startClassDialog.setTitle("Select Start Class");
            startClassDialog.setHeaderText("Select the start class for the relationship:");
            startClassDialog.setContentText("Start Class:");

            String startClassName = startClassDialog.showAndWait().orElse(null);
            if (startClassName == null) {
                System.out.println("Start class selection was canceled.");
                return;
            } else {
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
            } else {
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
        } else {
            endClass = diagram.getClass(endingClass);
            startClass = diagram.getClass(startingClass);
            if (startClass == null || endClass == null) {
                System.out.println("One or both classes not found.");
                return;
            }
        }
        // Drawing Lines:
        System.out.println(startClass.getX() + "    aa     " + startClass.getY());
        System.out.println(endClass.getX() + "    a      " + endClass.getY());

        if (relationshipType.equals("association")) {
            // Get parent StackPanes
            StackPane startParent = (StackPane) startClass.getParent();
            StackPane endParent = (StackPane) endClass.getParent();

            System.out.println(startParent.getLayoutX() + "    " + startParent.getLayoutY());
            System.out.println(endParent.getLayoutX() + "    " + endParent.getLayoutY());
            // Create a line and bind its start and end points using border calculations
            Line relationshipLine = new Line();
            relationshipLine.setStroke(Color.BLACK);

            // Bind start points to the border of the source class
            relationshipLine.startXProperty().bind(calculateBorderX(startParent, endParent));
            relationshipLine.startYProperty().bind(calculateBorderY(startParent, endParent));

            // Bind end points to the border of the target class
            relationshipLine.endXProperty().bind(calculateBorderX(endParent, startParent));
            relationshipLine.endYProperty().bind(calculateBorderY(endParent, startParent));

            getChildren().add(relationshipLine);
            relationships.add(new Relationship(startClass.ClassName, endClass.ClassName, "association", relationshipLine, null));
            diagram.addRelationship(new Relationship(startClass.ClassName, endClass.ClassName, relationshipType, relationshipLine, null));
            createDeleteContextMenu(relationshipLine, startClass.ClassName, endClass.ClassName, "association");
            System.out.println("Association relationship added between " + startClass.ClassName + " and " + endClass.ClassName + ".");
        }
        else if (relationshipType.equals("composition")) {
            // Get parent StackPanes
            StackPane wholeParent = (StackPane) startClass.getParent();
            StackPane partParent = (StackPane) endClass.getParent();

            // Create a line and bind its start and end points using border calculations
            Line compositionLine = new Line();
            compositionLine.setStroke(Color.BLACK);

            // Bind start points to the border of the whole class (start of the relationship)
            compositionLine.startXProperty().bind(calculateBorderX(wholeParent, partParent));
            compositionLine.startYProperty().bind(calculateBorderY(wholeParent, partParent));

            // Bind end points to the border of the part class (end of the relationship)
            compositionLine.endXProperty().bind(calculateBorderX(partParent, wholeParent));
            compositionLine.endYProperty().bind(calculateBorderY(partParent, wholeParent));

            // Create a diamond shape for the composition
            Polygon diamond = new Polygon();
            diamond.getPoints().addAll(
                    0.0, 0.0,   // Top point
                    10.0, 10.0, // Right point
                    0.0, 20.0,  // Bottom point
                    -10.0, 10.0 // Left point
            );
            diamond.setFill(Color.BLACK);

            // Bind the diamond's position to the start of the line
            diamond.layoutXProperty().bind(compositionLine.startXProperty());
            diamond.layoutYProperty().bind(compositionLine.startYProperty().subtract(10)); // Offset for center alignment

            // Add the line and diamond to the canvas
            getChildren().addAll(compositionLine, diamond);
            relationships.add(new Relationship(startClass.ClassName, endClass.ClassName, "composition", compositionLine, diamond));
            diagram.addRelationship(new Relationship(startClass.ClassName, endClass.ClassName, relationshipType, compositionLine, diamond));
            createDeleteContextMenu(compositionLine, startClass.ClassName, endClass.ClassName, "composition");

            System.out.println("Composition relationship added between " + startClass.ClassName + " (whole) and " + endClass.ClassName + " (part).");
        }
        else if (relationshipType.equals("aggregation")) {
            // Get parent StackPanes
            StackPane wholeParent = (StackPane) startClass.getParent();
            StackPane partParent = (StackPane) endClass.getParent();

            // Create a line and bind its start and end points using border calculations
            Line aggregationLine = new Line();
            aggregationLine.setStroke(Color.BLACK);

            // Bind start points to the border of the whole class (start of the relationship)
            aggregationLine.startXProperty().bind(calculateBorderX(wholeParent, partParent));
            aggregationLine.startYProperty().bind(calculateBorderY(wholeParent, partParent));

            // Bind end points to the border of the part class (end of the relationship)
            aggregationLine.endXProperty().bind(calculateBorderX(partParent, wholeParent));
            aggregationLine.endYProperty().bind(calculateBorderY(partParent, wholeParent));

            // Create a hollow diamond shape for the aggregation
            Polygon hollowDiamond = new Polygon();
            hollowDiamond.getPoints().addAll(
                    0.0, 0.0,   // Top point
                    10.0, 10.0, // Right point
                    0.0, 20.0,  // Bottom point
                    -10.0, 10.0 // Left point
            );
            hollowDiamond.setFill(Color.WHITE);  // Hollow (empty inside)
            hollowDiamond.setStroke(Color.BLACK); // Black border

            // Bind the hollow diamond's position to the start of the line
            hollowDiamond.layoutXProperty().bind(aggregationLine.startXProperty());
            hollowDiamond.layoutYProperty().bind(aggregationLine.startYProperty().subtract(10)); // Offset for center alignment

            // Add the line and hollow diamond to the canvas
            getChildren().addAll(aggregationLine, hollowDiamond);
            relationships.add(new Relationship(startClass.ClassName, endClass.ClassName, "aggregation", aggregationLine, hollowDiamond));
            diagram.addRelationship(new Relationship(startClass.ClassName, endClass.ClassName, relationshipType, aggregationLine, hollowDiamond));
            createDeleteContextMenu(aggregationLine, startClass.ClassName, endClass.ClassName, "aggregation");
            System.out.println("Aggregation relationship added between " + startClass.ClassName + " (whole) and " + endClass.ClassName + " (part).");
        } else if (relationshipType == "inheritance") {
            // Get parent StackPanes
            StackPane parentClassParent = (StackPane) startClass.getParent(); // Superclass
            StackPane childClassParent = (StackPane) endClass.getParent(); // Subclass

            // Create a line
            Line inheritanceLine = new Line();
            inheritanceLine.setStroke(Color.BLACK);

            // Bind the line dynamically with border intersection
            inheritanceLine.startXProperty().bind(calculateBorderX(parentClassParent, childClassParent));
            inheritanceLine.startYProperty().bind(calculateBorderY(parentClassParent, childClassParent));
            inheritanceLine.endXProperty().bind(calculateBorderX(childClassParent, parentClassParent));
            inheritanceLine.endYProperty().bind(calculateBorderY(childClassParent, parentClassParent));

            // Create a hollow triangle for inheritance
            Polygon triangle = new Polygon();
            triangle.getPoints().addAll(
                    0.0, 0.0,  // Tip of the triangle
                    -10.0, 20.0, // Bottom left corner
                    10.0, 20.0   // Bottom right corner
            );
            triangle.setFill(Color.WHITE); // Hollow triangle
            triangle.setStroke(Color.BLACK); // Black border

            // Bind the triangle's position dynamically to the parent class border
            triangle.layoutXProperty().bind(inheritanceLine.startXProperty());
            triangle.layoutYProperty().bind(inheritanceLine.startYProperty());

            // Add a listener to dynamically update the triangle's rotation
            inheritanceLine.startXProperty().addListener((observable, oldValue, newValue) -> updateTriangleRotation(inheritanceLine, triangle));
            inheritanceLine.startYProperty().addListener((observable, oldValue, newValue) -> updateTriangleRotation(inheritanceLine, triangle));
            inheritanceLine.endXProperty().addListener((observable, oldValue, newValue) -> updateTriangleRotation(inheritanceLine, triangle));
            inheritanceLine.endYProperty().addListener((observable, oldValue, newValue) -> updateTriangleRotation(inheritanceLine, triangle));


            // Add the line and triangle to the canvas
            getChildren().addAll(inheritanceLine, triangle);
            relationships.add(new Relationship(startClass.ClassName, endClass.ClassName, "inheritance", inheritanceLine, triangle));
            diagram.addRelationship(new Relationship(startClass.ClassName, endClass.ClassName, relationshipType, inheritanceLine, triangle));
            createDeleteContextMenu(inheritanceLine, startClass.ClassName, endClass.ClassName, "inheritance");
            System.out.println("Inheritance relationship added between " + startClass.ClassName + " (superclass) and " + endClass.ClassName + " (subclass).");
        }

    }

    private DoubleBinding calculateBorderX(StackPane source, StackPane target) {
        return Bindings.createDoubleBinding(() -> {
                    double sourceCenterX = source.getLayoutX() + source.getWidth() / 2;
                    double sourceCenterY = source.getLayoutY() + source.getHeight() / 2;

                    double targetCenterX = target.getLayoutX() + target.getWidth() / 2;
                    double targetCenterY = target.getLayoutY() + target.getHeight() / 2;

                    // Calculate direction vector
                    double dx = targetCenterX - sourceCenterX;
                    double dy = targetCenterY - sourceCenterY;

                    // Calculate scaling factor to reach the border
                    double scale = Math.min(
                            Math.abs(source.getWidth() / 2 / dx),
                            Math.abs(source.getHeight() / 2 / dy)
                    );

                    // Calculate border intersection point
                    return sourceCenterX + dx * scale;
                }, source.layoutXProperty(), source.layoutYProperty(),
                target.layoutXProperty(), target.layoutYProperty());
    }

    private DoubleBinding calculateBorderY(StackPane source, StackPane target) {
        return Bindings.createDoubleBinding(() -> {
                    double sourceCenterX = source.getLayoutX() + source.getWidth() / 2;
                    double sourceCenterY = source.getLayoutY() + source.getHeight() / 2;

                    double targetCenterX = target.getLayoutX() + target.getWidth() / 2;
                    double targetCenterY = target.getLayoutY() + target.getHeight() / 2;

                    // Calculate direction vector
                    double dx = targetCenterX - sourceCenterX;
                    double dy = targetCenterY - sourceCenterY;

                    // Calculate scaling factor to reach the border
                    double scale = Math.min(
                            Math.abs(source.getWidth() / 2 / dx),
                            Math.abs(source.getHeight() / 2 / dy)
                    );

                    // Calculate border intersection point
                    return sourceCenterY + dy * scale;
                }, source.layoutXProperty(), source.layoutYProperty(),
                target.layoutXProperty(), target.layoutYProperty());
    }

    private void updateTriangleRotation(Line line, Polygon triangle) {
        double angle = calculateAngle(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        triangle.setRotate(angle - 90); // Subtract 90 to make it point opposite
    }


    private double calculateAngle(double startX, double startY, double endX, double endY) {
        double dx = endX - startX;
        double dy = endY - startY;
        return Math.toDegrees(Math.atan2(dy, dx));
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