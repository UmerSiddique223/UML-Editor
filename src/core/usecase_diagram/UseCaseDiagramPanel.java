package core.usecase_diagram;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.TextInputDialog;
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
import java.util.Optional;

public class UseCaseDiagramPanel extends Pane {
    public List<DiagramComponent> components = new ArrayList<>();
    public List<Relationship> relationships = new ArrayList<>();
    private Line tempLine;
    private DiagramComponent startComponent;
    private boolean relationshipCreationMode = false;
    private boolean addActorMode = false;
    private boolean addUseCaseMode = false;
    private boolean dragMode = false;

    // Method to activate Add Actor mode
    public void setAddActorMode(boolean mode) {
        if (mode) {
            resetAllModes();
            addActorMode = true;
            setCursor(Cursor.CROSSHAIR);
            setOnMousePressed(this::onMousePressedToAddActor);
            setOnMouseDragged(null);
            setOnMouseReleased(null);
        } else {
            addActorMode = false;
            setOnMousePressed(this::onMousePressedDefault);
            setCursor(Cursor.DEFAULT);
        }
    }

    // Method to activate Add Use Case mode
    public void setAddUseCaseMode(boolean mode) {
        if (mode) {
            resetAllModes();
            addUseCaseMode = true;
            setCursor(Cursor.CROSSHAIR);
            setOnMousePressed(this::onMousePressedToAddUseCase);
            setOnMouseDragged(null);
            setOnMouseReleased(null);
        } else {
            addUseCaseMode = false;
            setOnMousePressed(this::onMousePressedDefault);
            setCursor(Cursor.DEFAULT);
        }
    }

    // Method to activate Drag mode
    public void setDragMode(boolean mode) {
        if (mode) {
            resetAllModes();
            dragMode = true;
            setCursor(Cursor.MOVE);
            setOnMousePressed(this::onMousePressedDefault);
            setOnMouseDragged(null);
            setOnMouseReleased(null);
        } else {
            dragMode = false;
            setCursor(Cursor.DEFAULT);
            setOnMousePressed(this::onMousePressedDefault);
            setOnMouseDragged(null);
            setOnMouseReleased(null);
        }
    }

    // Method to initiate Relationship Creation mode
    public void initiateRelationshipCreation() {
        resetAllModes();
        relationshipCreationMode = true;
        setCursor(Cursor.CROSSHAIR);
        setOnMousePressed(this::onMousePressedForRelationship);
        setOnMouseDragged(null);
        setOnMouseReleased(null);
    }

    // Toggle Relationship Creation mode
    public void setRelationshipCreationMode(boolean mode) {
        if (mode) {
            initiateRelationshipCreation();
        } else {
            relationshipCreationMode = false;
            setOnMousePressed(this::onMousePressedDefault);
            setCursor(Cursor.DEFAULT);
        }
    }

    // Reset all modes to default
    private void resetAllModes() {
        addActorMode = false;
        addUseCaseMode = false;
        dragMode = false;
        relationshipCreationMode = false;
        setCursor(Cursor.DEFAULT);
        setOnMousePressed(this::onMousePressedDefault);
    }

    // Prompt user for text input
    private String promptForText(String title) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter text:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    // Handler for adding Use Cases
    private void onMousePressedToAddUseCase(MouseEvent mouseEvent) {
        String useCaseText = promptForText("Enter Use Case Name");
        if (useCaseText != null && !useCaseText.trim().isEmpty()) {
            addUseCase(mouseEvent.getX(), mouseEvent.getY(), useCaseText.trim());
        } else {
            System.out.println("Use Case creation canceled or empty text.");
        }
    }

    // Handler for adding Actors
    private void onMousePressedToAddActor(MouseEvent mouseEvent) {
        addActor(mouseEvent.getX(), mouseEvent.getY());
    }

    // Handler for initiating Relationship Creation
    private void onMousePressedForRelationship(MouseEvent mouseEvent) {
        if (relationshipCreationMode) {
            // Use scene coordinates to correctly identify components
            double sceneX = mouseEvent.getSceneX();
            double sceneY = mouseEvent.getSceneY();

            DiagramComponent component = getComponentAt(sceneX, sceneY);
            if (component != null) {
                startComponent = component;
                // Start temp line from center of start component
                Point2D center = component.getCenterInScene();
                System.out.println("center ::: " + center.getX() + ", " + center.getY());
                tempLine = new Line(center.getX()/2, center.getY(), center.getX()/2, center.getY());
                tempLine.setStroke(Color.BLACK);
                tempLine.setStrokeWidth(2);
                tempLine.getStrokeDashArray().addAll(5.0, 5.0); // Dashed line for temp
                getChildren().add(tempLine);

                // Change event handlers to track dragging and release
                setOnMouseDragged(this::onMouseDraggedForRelationship);
                setOnMouseReleased(this::onMouseReleasedForRelationship);
            } else {
                System.out.println("No component selected to start relationship.");
            }
        }
    }

    // Handler for dragging the temp relationship line
    private void onMouseDraggedForRelationship(MouseEvent mouseEvent) {
        if (tempLine != null) {
            double sceneX = mouseEvent.getSceneX();
            double sceneY = mouseEvent.getSceneY();
            Point2D localPoint = this.sceneToLocal(sceneX, sceneY);

            tempLine.setEndX(localPoint.getX());
            tempLine.setEndY(localPoint.getY());
        }
    }

    // Handler for releasing the temp relationship line and finalizing the relationship
    private void onMouseReleasedForRelationship(MouseEvent mouseEvent) {
        if (tempLine != null && startComponent != null) {
            double sceneX = mouseEvent.getSceneX();
            double sceneY = mouseEvent.getSceneY();

            DiagramComponent endComponent = getComponentAt(sceneX, sceneY);
            if (endComponent != null && endComponent != startComponent) {
                String relationshipText = promptForText("Enter Relationship Label (optional)");

                // Create a solid relationship line
                Line relationshipLine = new Line();
                relationshipLine.setStroke(Color.BLACK);
                relationshipLine.setStrokeWidth(2);

                // Bind the line's start and end to the components' centers
                relationshipLine.startXProperty().bind(startComponent.centerInPaneXBinding());
                relationshipLine.startYProperty().bind(startComponent.centerInPaneYBinding());
                relationshipLine.endXProperty().bind(endComponent.centerInPaneXBinding());
                relationshipLine.endYProperty().bind(endComponent.centerInPaneYBinding());

                // Add the relationship line behind components
                getChildren().add(0, relationshipLine);

                // Add label if provided
                Text label = null;
                if (relationshipText != null && !relationshipText.trim().isEmpty()) {
                    label = new Text(relationshipText);
                    label.setFill(Color.BLUE);
                    label.setStyle("-fx-font-weight: bold;");
                    // Position the label at the midpoint
                    label.layoutXProperty().bind(relationshipLine.startXProperty().add(relationshipLine.endXProperty()).divide(2));
                    label.layoutYProperty().bind(relationshipLine.startYProperty().add(relationshipLine.endYProperty()).divide(2).subtract(5)); // Slightly above
                    getChildren().add(label);
                }

                // Create and store the relationship
                Relationship relationship = new Relationship(startComponent, endComponent, relationshipLine, label);
                relationships.add(relationship);

                System.out.println("Relationship created between " + startComponent.getText() + " and " + endComponent.getText());
            } else {
                System.out.println("Invalid end component for relationship.");
            }

            // Remove the temp line and reset states
            getChildren().remove(tempLine);
            tempLine = null;
            startComponent = null;
            setOnMouseDragged(null);
            setOnMouseReleased(null);
        }
    }

    // Default mouse pressed handler when no specific mode is active
    private void onMousePressedDefault(MouseEvent mouseEvent) {
        if (dragMode) {
            // Do nothing here; dragging is handled by individual components
        } else {
            // Potential future functionality for default mode
        }
    }

    // Method to add an Actor to the diagram
    public void addActor(double x, double y) {
        Circle actor = new Circle(30);
        actor.setFill(Color.LIGHTGRAY);
        actor.setStroke(Color.BLACK);

        Text actorText = new Text("Actor");

        StackPane actorContainer = new StackPane(actor, actorText);
        actorContainer.setLayoutX(x);
        actorContainer.setLayoutY(y);

        ActorComponent actorComponent = new ActorComponent(actorContainer, actor, actorText);
        components.add(actorComponent);
        getChildren().add(actorContainer);

        System.out.println("Actor added at (" + x + ", " + y + ")");
    }

    // Method to add a Use Case to the diagram
    public void addUseCase(double x, double y, String text) {
        Ellipse useCase = new Ellipse(50, 30);
        useCase.setFill(Color.WHITE);
        useCase.setStroke(Color.BLACK);

        Text useCaseText = new Text(text);

        StackPane useCaseContainer = new StackPane(useCase, useCaseText);
        useCaseContainer.setLayoutX(x);
        useCaseContainer.setLayoutY(y);

        UseCaseComponent useCaseComponent = new UseCaseComponent(useCaseContainer, useCase, useCaseText);
        components.add(useCaseComponent);
        getChildren().add(useCaseContainer);

        System.out.println("Use Case '" + text + "' added at (" + x + ", " + y + ")");
    }

    // Helper method to get the component at specific scene coordinates
    private DiagramComponent getComponentAt(double sceneX, double sceneY) {
        for (DiagramComponent component : components) {
            if (component.containsSceneCoords(sceneX, sceneY)) {
                return component;
            }
        }
        return null;
    }

    // Inner class representing a generic Diagram Component
    public class DiagramComponent {
        public final Shape shape;
        private final Text text;
        public final StackPane container;

        public DiagramComponent(StackPane container, Shape shape, Text text) {
            this.container = container;
            this.shape = shape;
            this.text = text;
            // Add mouse event handlers for dragging if in drag mode
            container.setOnMousePressed(this::componentOnMousePressed);
            container.setOnMouseDragged(this::componentOnMouseDragged);
            container.setOnMouseReleased(this::componentOnMouseReleased);
        }

        private double initialX;
        private double initialY;
        private double dragOffsetX;
        private double dragOffsetY;

        // Handler for mouse pressed on component
        private void componentOnMousePressed(MouseEvent event) {
            if (dragMode) {
                initialX = container.getLayoutX();
                initialY = container.getLayoutY();
                dragOffsetX = event.getSceneX() - initialX;
                dragOffsetY = event.getSceneY() - initialY;
                container.toFront(); // Bring the dragged component to the front
                event.consume();
            }
        }

        // Handler for mouse dragged on component
        private void componentOnMouseDragged(MouseEvent event) {
            if (dragMode) {
                double newX = event.getSceneX() - dragOffsetX;
                double newY = event.getSceneY() - dragOffsetY;
                container.setLayoutX(newX);
                container.setLayoutY(newY);
                event.consume();
            }
        }

        // Handler for mouse released on component
        private void componentOnMouseReleased(MouseEvent event) {
            if (dragMode) {
                // Update relationships if needed (handled by bindings)
                event.consume();
            }
        }

        // Method to check if a point in scene coordinates is within the component
        public boolean containsSceneCoords(double sceneX, double sceneY) {
            Bounds boundsInScene = container.localToScene(container.getBoundsInLocal());
            return boundsInScene.contains(sceneX, sceneY);
        }

        // Method to get the center position in scene coordinates
        public Point2D getCenterInScene() {
            Bounds boundsInScene = container.localToScene(container.getBoundsInLocal());
            double centerX = boundsInScene.getMinX() + boundsInScene.getWidth() / 2;
            double centerY = boundsInScene.getMinY() + boundsInScene.getHeight() / 2;
            return new Point2D(centerX, centerY);
        }

        // Binding for the center X position in the pane
        public DoubleBinding centerInPaneXBinding() {
            return container.layoutXProperty().add(container.getBoundsInLocal().getWidth() / 2);
        }

        // Binding for the center Y position in the pane
        public DoubleBinding centerInPaneYBinding() {
            return container.layoutYProperty().add(container.getBoundsInLocal().getHeight() / 2);
        }

        // Method to get the text of the component (for debugging)
        public String getText() {
            return text.getText();
        }
    }

    // Inner class representing an Actor Component
    private class ActorComponent extends DiagramComponent {
        public ActorComponent(StackPane container, Circle actor, Text text) {
            super(container, actor, text);
        }
    }

    // Inner class representing a Use Case Component
    public class UseCaseComponent extends DiagramComponent {
        public UseCaseComponent(StackPane container, Ellipse useCase, Text text) {
            super(container, useCase, text);
        }
    }

    // Inner class representing a Relationship between two components
    public class Relationship {
        public final DiagramComponent from;
        public final DiagramComponent to;
        public final Line line;
        private final Text label;

        public Relationship(DiagramComponent from, DiagramComponent to, Line line, Text label) {
            this.from = from;
            this.to = to;
            this.line = line;
            this.label = label;
        }

        // Additional methods can be added here if needed
    }
}
