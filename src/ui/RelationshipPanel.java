package ui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class RelationshipPanel extends Line {
    private String relationshipType;

    public RelationshipPanel(double startX, double startY, double endX, double endY, String type) {
        super(startX, startY, endX, endY);
        this.relationshipType = type;
        setStyleBasedOnType(type);
    }

    private void setStyleBasedOnType(String type) {
        switch (type) {
            case "Inheritance":
                setStroke(Color.BLUE);
                getStrokeDashArray().addAll(10.0, 10.0);
                setStrokeWidth(2);
                break;
            case "Aggregation":
                setStroke(Color.GREEN);
                setStrokeWidth(2);
                break;
            case "Composition":
                setStroke(Color.BLACK);
                setStrokeWidth(3);
                break;
            case "Association":
                setStroke(Color.GRAY);
                setStrokeWidth(1.5);
                break;
            default:
                setStroke(Color.RED); // For unclassified relationships
        }
    }

    public String getRelationshipType() {
        return relationshipType;
    }
}
