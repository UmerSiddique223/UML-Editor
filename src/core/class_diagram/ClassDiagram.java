package core.class_diagram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a class diagram for storing class structures and relationships,
 * and supports operations like adding, removing classes, relationships, and generating code.
 */

public class ClassDiagram implements Serializable {

    /** The name of the class diagram. */
    public String Name;

    /** The list of classes in the diagram. */
    public ArrayList<ClassPanel> classes;

    /** The list of relationships between classes in the diagram. */
    private List<Relationship> relationships = new ArrayList<>();

    /**
     * Constructs a new {@code ClassDiagram} with the specified name.
     *
     * @param name the name of the class diagram
     */
    public ClassDiagram(String name) {
        classes = new ArrayList<>();
        Name = name;
    }

    /**
     * Gets the name of the class diagram.
     *
     * @return the name of the diagram
     */
    public String getName() {
        return Name;
    }

    /**
     * Gets the list of classes in the diagram.
     *
     * @return the list of {@code ClassPanel} instances
     */
    public ArrayList<ClassPanel> getClasses() {
        return classes;
    }

    /**
     * Adds a class to the diagram.
     *
     * @param c the {@code ClassPanel} instance to add
     */
    public void addClass(ClassPanel c) {
        classes.add(c);
    }

    /**
     * Adds a relationship to the diagram.
     *
     * @param relationship the {@code Relationship} instance to add
     */
    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }

    /**
     * Gets the list of relationships in the diagram.
     *
     * @return the list of {@code Relationship} instances
     */
    public List<Relationship> getRelationships() {
        return relationships;
    }

    /**
     * Gets a list of class names in the diagram.
     *
     * @return a list of class names as {@code String}
     */
    public ArrayList<String> getClassList() {
        ArrayList<String> list = new ArrayList<>();
        for (ClassPanel c : classes) {
            list.add(c.ClassName);
        }
        return list;
    }

    /**
     * Removes a class from the diagram by its name.
     *
     * @param className the name of the class to remove
     */
    public void removeClass(String className) {
        for (ClassPanel c : classes) {
            if (c.ClassName.equals(className)) {
                classes.remove(c);
                break;
            }
        }
    }

    /**
     * Removes a relationship from the diagram by its details.
     *
     * @param startClass the starting class of the relationship
     * @param endClass   the ending class of the relationship
     * @param type       the type of the relationship
     */
    public void removeRelationship(String startClass, String endClass, String type) {
        for (Relationship r : relationships) {
            if (r.getStartClass().equals(startClass) && r.getEndClass().equals(endClass) && r.getType().equals(type)) {
                r.deleteRelationship();
                relationships.remove(r);
                break;
            }
        }
    }

    public ClassPanel getClassAt(double x, double y) {
        for (ClassPanel classPanel : classes) {
            if (classPanel.contains(x, y)) {
                return classPanel;
            }
        }
        return null;
    }

    /**
     * Removes all relationships associated with a specific class from the diagram.
     *
     * @param className the name of the class whose relationships should be removed
     */
    public void removeRelations_of_a_Diagram(String className) {
        for (Relationship r : relationships) {
            if (r.getStartClass().equals(className) || r.getEndClass().equals(className)) {
                r.deleteRelationship();
                relationships.remove(r);
            }
        }
        // relationships.removeIf(r -> r.getStartClass().equals(className) || r.getEndClass().equals(className));
    }

    /**
     * Gets all relationships in the diagram that involve a specific class.
     *
     * @param className the name of the class whose relationships are to be retrieved
     * @return a list of {@code Relationship} instances
     */
    public ArrayList<Relationship> getRelations_of_a_Diagram(String className) {
        ArrayList<Relationship> list = new ArrayList<>();
        for (Relationship r : relationships) {
            if (r.getStartClass().equals(className) || r.getEndClass().equals(className)) {
                list.add(r);
            }
        }
        return list;
    }

    /**
     * Gets a class in the diagram by its name.
     *
     * @param className the name of the class to retrieve
     * @return the {@code ClassPanel} instance, or {@code null} if not found
     */
    public ClassPanel getClass(String className) {
        for (ClassPanel c : classes) {
            if (c.ClassName.equals(className)) {
                return c;
            }
        }
        return null;
    }
}
