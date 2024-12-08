package core.class_diagram;

import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.io.Serializable;
import java.io.Serializable;

/**
 * Represents a relationship between two classes in a UML diagram.
 * This class stores the information about the classes involved in the relationship,
 * the type of relationship, and the graphical representation (line and shape).
 */
public class Relationship implements Serializable {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /** The class name of the starting class in the relationship. */
    public String startClass;

    /** The class name of the ending class in the relationship. */
    public String endClass;

    /** The type of relationship (e.g., association, composition, etc.). */
    public String type;

    /** The line representing the relationship in the diagram. */
    public Line line;

    /** The shape representing the relationship in the diagram. */
    public Polygon shape;

    /**
     * Constructs a new Relationship with the specified starting class, ending class,
     * relationship type, line, and shape.
     *
     * @param startClass the name of the starting class.
     * @param endClass the name of the ending class.
     * @param type the type of relationship.
     * @param line the line representing the relationship.
     * @param shape the shape representing the relationship.
     */
    public Relationship(String startClass, String endClass, String type, Line line, Polygon shape) {
        this.startClass = startClass;
        this.endClass = endClass;
        this.type = type;
        this.line = line;
        this.shape = shape;
    }

    /**
     * Constructs a new Relationship with the specified starting class, ending class,
     * and relationship type. The line and shape are set to null.
     *
     * @param startClass the name of the starting class.
     * @param endClass the name of the ending class.
     * @param type the type of relationship.
     */
    public Relationship(String startClass, String endClass, String type) {
        this.startClass = startClass;
        this.endClass = endClass;
        this.type = type;
        this.line = null;
        this.shape = null;
    }

    /**
     * Deletes the relationship by making the line and shape representing it invisible.
     * If the line and shape are not null, their visibility is set to false.
     */
    public void deleteRelationship() {
        if (line != null)
            line.setVisible(false);
        if (shape != null)
            shape.setVisible(false);
    }

    /**
     * Gets the name of the starting class in the relationship.
     *
     * @return the name of the starting class.
     */
    public String getStartClass() {
        return startClass;
    }

    /**
     * Sets the name of the starting class in the relationship.
     *
     * @param startClass the name of the starting class.
     */
    public void setStartClass(String startClass) {
        this.startClass = startClass;
    }

    /**
     * Gets the name of the ending class in the relationship.
     *
     * @return the name of the ending class.
     */
    public String getEndClass() {
        return endClass;
    }

    /**
     * Sets the name of the ending class in the relationship.
     *
     * @param endClass the name of the ending class.
     */
    public void setEndClass(String endClass) {
        this.endClass = endClass;
    }

    /**
     * Gets the type of relationship.
     *
     * @return the type of relationship.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of relationship.
     *
     * @param type the type of relationship.
     */
    public void setType(String type) {
        this.type = type;
    }
}

