package ui;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;

public class UseCaseShape extends StackPane {
    public UseCaseShape(String name) {
        // Create oval
        Ellipse oval = new Ellipse(50, 25); // Width and height radii
        oval.setFill(Color.LIGHTBLUE);
        oval.setStroke(Color.BLACK);

        // Add name label
        Text nameLabel = new Text(name);

        // Add components to StackPane
        getChildren().addAll(oval, nameLabel);

        // Enable dragging (future feature)
        setLayoutX(0);
        setLayoutY(0);
    }
}
