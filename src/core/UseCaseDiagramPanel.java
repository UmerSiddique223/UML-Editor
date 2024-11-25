package core;

import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import ui.UseCaseRelationship;

import java.util.ArrayList;
import java.util.List;

public class UseCaseDiagramPanel extends Pane {
    private DiagramComponent startComponent;
    private DiagramComponent endComponent;
    private Line tempLine;
    private final List<UseCaseRelationship> relationships = new ArrayList<>();
    private final List<DiagramComponent> components = new ArrayList<>(); // Tracks DiagramComponents
    private String currentTool;

    public UseCaseDiagramPanel(String title) {
        setStyle("-fx-background-color: lightblue;");
        setPrefSize(800, 600);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        setOnMouseClicked(this::handleMouseClick);
        setOnMouseMoved(this::handleMouseMove);
    }

    public void setCurrentTool(String tool) {
        this.currentTool = tool;
    }

    private void handleMouseClick(MouseEvent event) {
        if ("Actor".equals(currentTool)) {
            createActor(event.getX(), event.getY());
            currentTool = null;
        } else if ("UseCase".equals(currentTool)) {
            createUseCase(event.getX(), event.getY());
            currentTool = null;
        } else if (event.getButton() == MouseButton.PRIMARY) {
            DiagramComponent clickedComponent = findDiagramComponent(event);

            if (clickedComponent == null) {
                return;
            }

            if (startComponent == null) {
                startComponent = clickedComponent;
                System.out.println("Start component selected: " + startComponent.getTitle());
                createTempLine(startComponent.getCenterX(), startComponent.getCenterY());
            } else if (clickedComponent != startComponent) {
                endComponent = clickedComponent;
                System.out.println("End component selected: " + endComponent.getTitle());
                finalizeRelationship();
            }
        }
    }

    private void handleMouseMove(MouseEvent event) {
        if (tempLine != null) {
            tempLine.setEndX(event.getX());
            tempLine.setEndY(event.getY());
        }
    }

    private void createActor(double x, double y) {
        javafx.scene.shape.Circle actor = new Circle(x, y, 20);
        actor.setFill(Color.LIGHTGRAY);
        actor.setStroke(Color.BLACK);
        Text label = new Text(x - 15, y - 30, "Actor");

        DiagramComponent actorComponent = new DiagramComponent(actor, label, "Actor");
        components.add(actorComponent); // Track the DiagramComponent
        getChildren().addAll(actor, label); // Add visual elements to the Pane
    }

    // This is to be done instead of above create actor function

    //    private void createActor(double x, double y) {
    //        ActorShape actor = new ActorShape("Actor");
    //        actor.setLayoutX(x - actor.getWidth() / 2);
    //        actor.setLayoutY(y - actor.getHeight() / 2);
    //        getChildren().add(actor);
    //    }


    private void createUseCase(double x, double y) {
        Ellipse useCase = new Ellipse(x, y, 50, 30);
        useCase.setFill(Color.LIGHTYELLOW);
        useCase.setStroke(Color.BLACK);
        Text label = new Text(x - 25, y - 40, "Use Case");

        DiagramComponent useCaseComponent = new DiagramComponent(useCase, label, "Use Case");
        components.add(useCaseComponent); // Track the DiagramComponent
        getChildren().addAll(useCase, label); // Add visual elements to the Pane
    }

    private DiagramComponent findDiagramComponent(MouseEvent event) {
        return components.stream()
                .filter(component -> component.contains(event.getX(), event.getY()))
                .findFirst()
                .orElse(null);
    }

    private void createTempLine(double startX, double startY) {
        tempLine = new Line(startX, startY, startX, startY);
        tempLine.setStroke(Color.GRAY);
        tempLine.getStrokeDashArray().addAll(5.0, 5.0);

        getChildren().add(tempLine);
    }

    private void finalizeRelationship() {
        if (startComponent == null || endComponent == null) {
            return;
        }

        Line relationshipLine = new Line(
                startComponent.getCenterX(), startComponent.getCenterY(),
                endComponent.getCenterX(), endComponent.getCenterY()
        );

        relationshipLine.setStroke(Color.BLACK);
        getChildren().add(relationshipLine);

        UseCaseRelationship relationship = new UseCaseRelationship(
                startComponent.getTitle(),
                endComponent.getTitle(),
                "Association"
        );
        relationships.add(relationship);
        System.out.println("Relationship stored: " + relationship);

        resetRelationshipState();
    }

    private void resetRelationshipState() {
        startComponent = null;
        endComponent = null;
        if (tempLine != null) {
            getChildren().remove(tempLine);
            tempLine = null;
        }
    }

    public List<UseCaseRelationship> getRelationships() {
        return relationships;
    }

    // Helper class for managing diagram components
    private static class DiagramComponent {
        private final javafx.scene.shape.Shape shape;
        private final Text label;
        private final String title;

        public DiagramComponent(javafx.scene.shape.Shape shape, Text label, String title) {
            this.shape = shape;
            this.label = label;
            this.title = title;
        }

        public double getCenterX() {
            return shape.getLayoutBounds().getMinX() + shape.getLayoutBounds().getWidth() / 2;
        }

        public double getCenterY() {
            return shape.getLayoutBounds().getMinY() + shape.getLayoutBounds().getHeight() / 2;
        }

        public boolean contains(double x, double y) {
            return shape.contains(x, y);
        }

        public String getTitle() {
            return title;
        }
    }
}