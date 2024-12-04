package core.class_diagram;

import java.io.Serializable;

// Represents a relationship between two classes
public class Relationship implements Serializable {
    private static final long serialVersionUID = 1L;

    public String startClass; // Class name of the starting class
    public String endClass;   // Class name of the ending class
    public String type;       // Type of relationship: association, composition, etc.

    public Relationship(String startClass, String endClass, String type) {
        this.startClass = startClass;
        this.endClass = endClass;
        this.type = type;
    }
}
