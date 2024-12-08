package core.class_diagram;

import java.io.Serializable;
import java.util.ArrayList;
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


    public ClassPanel getClass(String className) {
        for (ClassPanel c : classes) {
            if (c.ClassName.equals(className)) {
                return c;
            }
        }
        return null;
    }
}
