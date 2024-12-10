package core.class_diagram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// For Storing Class Diagrams and Generating Code
public class ClassDiagram implements Serializable {
    public String Name;
    public ArrayList<ClassPanel> classes;
    private List<Relationship> relationships = new ArrayList<>();

    public ClassDiagram(String name) {
        classes = new ArrayList<>();
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public ArrayList<ClassPanel> getClasses() {
        return classes;
    }

    public void addClass(ClassPanel c) {
        classes.add(c);
    }
    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }
    public List<Relationship> getRelationships() {
        return relationships;
    }
    public ArrayList<String> getClassList() {
        ArrayList<String> list = new ArrayList<>();
        for (ClassPanel c : classes) {
            list.add(c.ClassName);
        }
        return list;
    }
    public void removeClass(String className) {
        for (ClassPanel c : classes) {
            if (c.ClassName.equals(className)) {
                classes.remove(c);
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
    public void removeRelationship(String startClass, String endClass, String type) {
        for (Relationship r : relationships) {
            if (r.getStartClass().equals(startClass) && r.getEndClass().equals(endClass) && r.getType().equals(type)) {
                r.deleteRelationship();
                relationships.remove(r);
                break;
            }
        }
    }

    public void removeRelations_of_a_Diagram(String className) {
        Iterator<Relationship> iterator = relationships.iterator();
        while (iterator.hasNext()) {
            Relationship r = iterator.next();
            if (r.getStartClass().equals(className) || r.getEndClass().equals(className)) {
                r.deleteRelationship();
                iterator.remove(); // Safe removal using iterator
            }
        }
    }


    public ArrayList<Relationship> getRelations_of_a_Diagram(String className) {
        ArrayList<Relationship> list = new ArrayList<>();
        for (Relationship r : relationships) {
            if (r.getStartClass().equals(className) || r.getEndClass().equals(className)) {
                list.add(r);
            }
        }
        return list;
    }
    public ClassPanel getClass(String className) {
        for (ClassPanel c : classes) {
            if (c.ClassName.equals(className)) {
                return c;
            }
        }
        return null;
    }
}
