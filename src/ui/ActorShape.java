package ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * A pane representing the shape of the Actor in Use Case Diagram.
 */
public class ActorShape extends Pane {
    public ActorShape(String name) {
        // Create head (circle)
        Circle head = new Circle(15);
        head.setFill(Color.TRANSPARENT);
        head.setStroke(Color.BLACK);
        head.setCenterX(0); // Place at center (relative to Pane)
        head.setCenterY(15);

        // Create body (line from head down)
        Line body = new Line(0, 30, 0, 70); // Adjusted for head's position

        // Create arms (horizontal line at the middle of the body)
        Line arms = new Line(-20, 50, 20, 50);

        // Create legs (two diagonal lines from the end of the body)
        Line leftLeg = new Line(0, 70, -20, 100);
        Line rightLeg = new Line(0, 70, 20, 100);

        // Add name label below the actor
        Text nameLabel = new Text(name);
        nameLabel.setX(-nameLabel.getLayoutBounds().getWidth() / 2); // Center the text
        nameLabel.setY(120);

        // Add all components to the Pane
        getChildren().addAll(head, body, arms, leftLeg, rightLeg, nameLabel);

        // Enable dragging (optional future feature)
        setLayoutX(0);
        setLayoutY(0);
    }
}
