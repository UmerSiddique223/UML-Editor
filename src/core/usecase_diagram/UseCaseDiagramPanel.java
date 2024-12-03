package core.usecase_diagram;


import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class UseCaseDiagramPanel extends Pane {
    public List<DiagramComponent> components = new ArrayList<>();
    public List<Relationship> relationships = new ArrayList<>();
    private Line tempLine;
    private DiagramComponent selectedComponent;
    private DiagramComponent endComponent;
    private boolean relationshipCreationMode = false;
    private boolean addActorMode = false;
    private boolean addUseCaseMode = false;
/***
    public void setAddUseCaseMode(boolean mode) {
        this.addUseCaseMode = mode;
        if (mode) {
            // Enable mouse event for adding a use case
            setOnMousePressed(this::onMousePressedToAddUseCase);
        } else {
            // Reset to normal mouse event handling
            setOnMousePressed(this::onMousePressed);
        }
    }

 public void setAddActorMode(boolean mode) {
 this.addActorMode = mode;
 if (mode) {
 // Enable mouse event for adding an actor
 setOnMousePressed(this::onMousePressedToAddActor);
 } else {
 // Reset to normal mouse event handling
 setOnMousePressed(this::onMousePressed);
 }
 }



 ***/
public void setAddActorMode(boolean mode) {
    if (mode) {
        // Activate Add Actor mode and deactivate others
        addActorMode = true;
        addUseCaseMode = false;
        relationshipCreationMode = false;
        setOnMousePressed(this::onMousePressedToAddActor);
    } else {
        // Deactivate Add Actor mode
        addActorMode = false;
        setOnMousePressed(this::onMousePressed);
    }
}

public void setAddUseCaseMode(boolean mode) {
    if (mode) {
        // Activate Add Use Case mode and deactivate others
        addUseCaseMode = true;
        addActorMode = false;
        relationshipCreationMode = false;
        setOnMousePressed(this::onMousePressedToAddUseCase);
    } else {
        // Deactivate Add Use Case mode
        addUseCaseMode = false;
        setOnMousePressed(this::onMousePressed);
    }
}
    private String promptForText(String title) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter text:");

        java.util.Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }


    private void onMousePressedToAddUseCase(MouseEvent mouseEvent) {
        String useCaseText = promptForText("Enter Use Case Name");
        if (useCaseText != null && !useCaseText.trim().isEmpty()) {
            addUserCase(mouseEvent.getSceneX(), mouseEvent.getSceneY(), useCaseText.trim());
        } else {
            System.out.println("Use Case creation canceled or empty text.");
        }
        //setAddUseCaseMode(false); allows multiple execution
    }


    private void onMousePressedToAddActor(MouseEvent mouseEvent) {
        addActor( mouseEvent.getSceneX(),  mouseEvent.getSceneY());
       // setAddActorMode(false);
    }


    public void initiateRelationshipCreation() {
        relationshipCreationMode = true;
        addUseCaseMode = false;
        addActorMode = false;
        setOnMousePressed(this::onMousePressedForRelationship);
    }

    private void onMousePressedForRelationship(MouseEvent mouseEvent) {
        if (relationshipCreationMode) {
            // Check if the click was on an existing component
            selectedComponent = components.stream()
                    .filter(component -> component.contains( mouseEvent.getSceneX() ,  mouseEvent.getSceneY()))
                    .findFirst()
                    .orElse(null);

            if (selectedComponent != null) {
                // Start the temporary line at the point of the mouse click
                Point2D localPoint = sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                tempLine = new Line(
                        localPoint.getX(),
                        localPoint.getY(),
                        localPoint.getX(),
                        localPoint.getY()
                ); tempLine.setStroke(Color.BLACK);
                tempLine.setStrokeWidth(2);
                getChildren().add(tempLine);

                relationshipCreationMode = false;
                setOnMousePressed(null); // Reset mouse pressed handler
                setOnMouseDragged(this::onMouseDragged);
                setOnMouseReleased(this::onMouseReleased);
                relationshipCreationMode = false;

            }
        }
    }

    public UseCaseDiagramPanel() {
        setStyle("-fx-background-color: lightblue;");
        //setOnContextMenuRequested(this::onContextMenuRequested);

        // Add drag handlers to each component
        components.forEach(component -> {
            StackPane container = (StackPane) component.shape.getParent();
            container.setOnMousePressed(this::onMousePressed);
            container.setOnMouseDragged(this::onMouseDragged);
            container.setOnMouseReleased(this::onMouseReleased);
        });
    }

    private void onMousePressed(MouseEvent mouseEvent) {
        // Convert scene coordinates to parent coordinates
        double sceneX = mouseEvent.getSceneX();
        double sceneY = mouseEvent.getSceneY();

        selectedComponent = components.stream()
                .filter(component -> component.contains(sceneX, sceneY))
                .findFirst()
                .orElse(null);
        System.out.println("selectedComponent onMousePressed" + " " + selectedComponent);
        if (selectedComponent != null) {
            // Get the center coordinates of the selected component
            System.out.println("Component bounds: " + selectedComponent.container.getBoundsInParent());

            double startX = selectedComponent.getCenterX();
            double startY = selectedComponent.getCenterY();

            Point2D localPoint = sceneToLocal(sceneX, sceneY);
            // Create the temporary line from the component's center
            tempLine = new Line(
                    localPoint.getX(),
                    localPoint.getY(),
                    localPoint.getX(),
                    localPoint.getY()
            );
            tempLine.setStroke(Color.BLACK);
            tempLine.setStrokeWidth(2);
            getChildren().add(tempLine);
        }
    }

    private void onMouseDragged(MouseEvent mouseEvent) {
        if (tempLine != null) {
            // Convert scene coordinates to parent coordinates
            double sceneX = mouseEvent.getSceneX();
            double sceneY = mouseEvent.getSceneY();
            Point2D localPoint = sceneToLocal(sceneX, sceneY);

            tempLine.setEndX(localPoint.getX());
            tempLine.setEndY(localPoint.getY());
        }
    }

    private void onMouseReleased(MouseEvent mouseEvent) {
        if (tempLine != null) {
            // Convert scene coordinates to parent coordinates
            double sceneX = mouseEvent.getSceneX();
            double sceneY = mouseEvent.getSceneY();



            System.out.println("sceneX onMouseReleased" + sceneX);
            System.out.println("sceneY onMouseReleasedY" + sceneY);

            endComponent = components.stream()
                    .filter(component -> component.contains(sceneX, sceneY))
                    .findFirst()
                    .orElse(null);
            System.out.println("endComponent onMouseReleased " + endComponent);

            if (endComponent != null && !selectedComponent.equals(endComponent)) {
                System.out.println("Component bounds: onMouseReleased" + endComponent.container.getBoundsInParent());
                System.out.println("Mouse event coordinates:onMouseReleased (" + sceneX + ", " + sceneY + ")");

                // Create permanent line between component centers
                Line relationshipLine = new Line(
                        selectedComponent.getCenterX(),
                        selectedComponent.getCenterY(),
                        endComponent.getCenterX(),
                        endComponent.getCenterY()
                );
                relationshipLine.setStroke(Color.BLACK);
                relationshipLine.setStrokeWidth(3);
                getChildren().add(relationshipLine);

                // Add to relationships list
                relationships.add(new Relationship(selectedComponent, endComponent, relationshipLine));

                // Bind line endpoints to component centers
                System.out.println(selectedComponent);
                relationshipLine.startXProperty().bind(selectedComponent.shape.layoutXProperty().add(selectedComponent.shape.getParent().layoutXProperty()));
                relationshipLine.startYProperty().bind(selectedComponent.shape.layoutYProperty().add(selectedComponent.shape.getParent().layoutYProperty()));
                relationshipLine.endXProperty().bind(endComponent.shape.layoutXProperty().add(endComponent.shape.getParent().layoutXProperty()));
                relationshipLine.endYProperty().bind(endComponent.shape.layoutYProperty().add(endComponent.shape.getParent().layoutYProperty()));
            }

            getChildren().remove(tempLine);
            tempLine = null;
            selectedComponent = null;
            endComponent = null;
        }
    }

    private void onContextMenuRequested(ContextMenuEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addActor = new MenuItem("Add Actor");
        MenuItem addUserCase = new MenuItem("Add Use Case");
        MenuItem addRelationship = new MenuItem("Add Relationship");
        MenuItem deleteComponent = new MenuItem("Delete Component");

        addActor.setOnAction(e -> addActor(event.getSceneX(), event.getSceneY() ));
        addUserCase.setOnAction(e -> addUserCase(event.getSceneX(), event.getSceneY() , ""));
        addRelationship.setOnAction(e -> initiateRelationshipCreation());
        deleteComponent.setOnAction(e -> deleteSelectedComponent(event));

        contextMenu.getItems().addAll(addActor, addUserCase, addRelationship, deleteComponent);
        contextMenu.show(this, event.getScreenX(), event.getScreenY());
    }

    private void deleteSelectedComponent(ContextMenuEvent event) {
        // Find the component under the mouse event and remove it
        DiagramComponent selected = components.stream()
                .filter(component -> component.shape.contains(event.getSceneX(), event.getSceneY()))
                .findFirst()
                .orElse(null);

        if (selected != null) {
            components.remove(selected);
            getChildren().removeAll(selected.shape, selected.text);
        }
    }

    public void addActor(double x, double y) {
        Circle actor = new Circle( 30); // Centered at (0,0) in the StackPane
        actor.setFill(Color.LIGHTGRAY);
        actor.setStroke(Color.BLACK);

        //Circle hitbox = new Circle(50); // Larger transparent boundary
        //itbox.setFill(Color.TRANSPARENT); // Make it transparent
        //hitbox.setStroke(Color.LIGHTGRAY);

        Text actorText = new Text("Actor");

        StackPane actorContainer = new StackPane( actor, actorText);
        actorContainer.setLayoutX(x);
        actorContainer.setLayoutY(y);

        // Add event handlers to the container
        actorContainer.setOnMousePressed(this::onMousePressed);
        actorContainer.setOnMouseDragged(this::onMouseDragged);
        actorContainer.setOnMouseReleased(this::onMouseReleased);

        ActorComponent actorComponent = new ActorComponent(actorContainer, actor, actorText);
        components.add(actorComponent);
        getChildren().add(actorContainer);
    }

    public void addUserCase(double x, double y, String text) {
        Ellipse useCase = new Ellipse(0, 0, 50, 50); // Centered at (0,0) in StackPane
        useCase.setFill(Color.WHITE);
        useCase.setStroke(Color.BLACK);

        Circle hitbox = new Circle(0, 0, 90); // Larger transparent boundary
        hitbox.setFill(Color.TRANSPARENT);
        hitbox.setStroke(Color.LIGHTGRAY);

        Text useCaseText = new Text(text);

        StackPane useCaseContainer = new StackPane(hitbox, useCase, useCaseText);
        useCaseContainer.setLayoutX(x);
        useCaseContainer.setLayoutY(y);

        // Add event handlers to the container
        useCaseContainer.setOnMousePressed(this::onMousePressed);
        useCaseContainer.setOnMouseDragged(this::onMouseDragged);
        useCaseContainer.setOnMouseReleased(this::onMouseReleased);

        UseCaseComponent useCaseComponent = new UseCaseComponent(useCaseContainer, useCase, useCaseText);
        components.add(useCaseComponent);
        getChildren().add(useCaseContainer);
    }



    // Inner classes for components
    public class DiagramComponent {
        public final Shape shape;
        private final Text text;
        public final StackPane container;

        public DiagramComponent(StackPane container, Shape shape, Text text) {
            this.container = container;
            this.shape = shape;
            this.text = text;
        }

        public boolean contains(double x, double y) {
            Bounds boundsInScene = container.localToScene(container.getBoundsInLocal());
            System.out.println("Component bounds in scene: " + boundsInScene);
            return boundsInScene.contains(x, y);
        }


        public double getCenterX() {
            Bounds bounds = container.getBoundsInParent();
            return bounds.getMinX() + bounds.getWidth() / 2;
        }

        public double getCenterY() {
            Bounds bounds = container.getBoundsInParent();
            return bounds.getMinY() + bounds.getHeight() / 2;
        }
    }
    private class ActorComponent extends DiagramComponent {
        public ActorComponent(StackPane container, Circle actor, Text text) {
            super(container, actor, text);
        }
    }

    public class UseCaseComponent extends DiagramComponent {
        public UseCaseComponent(StackPane container, Ellipse useCase, Text text) {
            super(container, useCase, text);
        }
    }


    // Relationship class
    public class Relationship {
        public final DiagramComponent from;
        public final DiagramComponent to;
        public final Line line;

        public Relationship(DiagramComponent from, DiagramComponent to, Line line) {
            this.from = from;
            this.to = to;
            this.line = line;
        }
    }
}