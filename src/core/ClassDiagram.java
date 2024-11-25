package core;

import java.util.ArrayList;

public class ClassDiagram {
    public String Name;
    public ArrayList<ClassPanel> classes = new ArrayList<ClassPanel>();

    public ClassDiagram(String name) {
        Name = name;
    }

    public void addClass(ClassPanel c) {
        classes.add(c);
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
