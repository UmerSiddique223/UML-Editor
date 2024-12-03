package core.class_diagram;

import data.DiagramSaver;
import bean.DragResizeBean;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
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

// Canvas for Class Diagrams
public class ClassDiagramCanvasPanel extends Pane {
    ClassDiagram diagram;
    private String drawingMode = "";

    private ArrayList<Relationship> relationships = new ArrayList<>(); // Store relationships

    public ClassDiagramCanvasPanel() {
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

        // Option to add a Class
        MenuItem addClassDiagram = new MenuItem("Add Class");
        addClassDiagram.setOnAction(ev -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Class");
            dialog.setHeaderText("Enter Class Name:");
            dialog.showAndWait().ifPresent(name -> addClassToCanvas(new ClassPanel(name, false,x,y), x, y));
        });

        // Option to add an Interface
        MenuItem addInterfaceDiagram = new MenuItem("Add Interface");
        addInterfaceDiagram.setOnAction(ev -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Interface");
            dialog.setHeaderText("Enter Interface Name:");
            dialog.showAndWait().ifPresent(name -> addClassToCanvas(new ClassPanel(name, true,x,y), x, y));
        });

        contextMenu.getItems().addAll(addClassDiagram, addInterfaceDiagram);
        contextMenu.show(this, screenX, screenY);
    }


    public void saveDiagram(Stage parentStage) throws Exception {
        System.out.println(diagram.Name+"  Name");
        System.out.println(diagram+"    hnjnjkdssd");
        if (diagram == null) {
            System.out.println("No diagram to save.");
            return;
        }

        DiagramSaver.saveDiagram(diagram);
    }

    public void addClassToCanvas(ClassPanel classPanel, double x, double y) {
        javafx.scene.shape.Rectangle border = new Rectangle(200, 150);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.BLACK);

        StackPane container = new StackPane(border, classPanel);
        container.setLayoutX(x);
        container.setLayoutY(y);

        // Apply resizing functionality
        DragResizeBean.apply(container, this);

        getChildren().add(container);

        // Attach drag event handlers
        container.setOnMousePressed(event -> handleClassDragStart(container, event));
        container.setOnMouseDragged(event -> handleClassDrag(container, event));
        container.setOnMouseReleased(event -> handleClassDragEnd(container));
        ClassPanel classData = new ClassPanel(classPanel.getClassName(), classPanel.isInterface(), x, y);

        // Adding class to the Diagram class too:
        diagram.addClass(classData);
        System.out.println(diagram.Name+"  Name");
    }

    private double dragStartX;
    private double dragStartY;

    private void handleClassDragStart(StackPane container, MouseEvent event) {
        dragStartX = event.getSceneX() - container.getLayoutX();
        dragStartY = event.getSceneY() - container.getLayoutY();
    }

    private void handleClassDrag(StackPane container, MouseEvent event) {
        // Get the current position of the mouse relative to the canvas
        double newX = event.getSceneX() - dragStartX;
        double newY = event.getSceneY() - dragStartY;

        // Update the position of the container during dragging
        container.setLayoutX(newX);
        container.setLayoutY(newY);
    }

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

        if (relationshipType.equals("association")) {
            // Get parent StackPanes
            StackPane startParent = (StackPane) startClass.getParent();
            StackPane endParent = (StackPane) endClass.getParent();

            // Create a line and bind its start and end points using border calculations
            Line relationshipLine = new Line();
            relationshipLine.setStroke(Color.BLACK);

            // Bind start points to the border of the source class
            relationshipLine.startXProperty().bind(calculateBorderX(startParent, endParent));
            relationshipLine.startYProperty().bind(calculateBorderY(startParent, endParent));

            // Bind end points to the border of the target class
            relationshipLine.endXProperty().bind(calculateBorderX(endParent, startParent));
            relationshipLine.endYProperty().bind(calculateBorderY(endParent, startParent));

            // Add the line to the canvas
            getChildren().add(relationshipLine);
            relationships.add(new Relationship(startClass.ClassName, endClass.ClassName, "association"));
            diagram.addRelationship(new Relationship(startClass.ClassName, endClass.ClassName, relationshipType));

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
            relationships.add(new Relationship(startClass.ClassName, endClass.ClassName, "composition"));
            diagram.addRelationship(new Relationship(startClass.ClassName, endClass.ClassName, relationshipType));

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
            relationships.add(new Relationship(startClass.ClassName, endClass.ClassName, "aggregation"));
            diagram.addRelationship(new Relationship(startClass.ClassName, endClass.ClassName, relationshipType));

            System.out.println("Aggregation relationship added between " + startClass.ClassName + " (whole) and " + endClass.ClassName + " (part).");
        }


        else if (relationshipType == "inheritance") {
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
            relationships.add(new Relationship(startClass.ClassName, endClass.ClassName, "inheritance"));
            diagram.addRelationship(new Relationship(startClass.ClassName, endClass.ClassName, relationshipType));

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







}