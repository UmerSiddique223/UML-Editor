package core.class_diagram;

import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.io.Serializable;

// Represents a relationship between two classes
public class Relationship implements Serializable {
    private static final long serialVersionUID = 1L;

    public String startClass; // Class name of the starting class
    public String endClass;   // Class name of the ending class
    public String type;       // Type of relationship: association, composition, etc.
    public Line line;
    public Polygon shape;

    public Relationship(String startClass, String endClass, String type, Line line, Polygon shape) {
        this.startClass = startClass;
        this.endClass = endClass;
        this.type = type;
        this.line = line;
        this.shape = shape;
    }
    public Relationship(String startClass, String endClass, String type) {
        this.startClass = startClass;
        this.endClass = endClass;
        this.type = type;
        this.line = null;
        this.shape = null;
    }
    public void deleteRelationship(){
        if (line != null)
            line.setVisible(false);
        if (shape != null)
            shape.setVisible(false);
    }

    public String getStartClass() {
        return startClass;
    }
    public void setStartClass(String startClass) {
        this.startClass = startClass;
    }
    public String getEndClass() {
        return endClass;
    }
    public void setEndClass(String endClass) {
        this.endClass = endClass;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

}
