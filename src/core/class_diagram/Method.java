package core.class_diagram;

import java.util.ArrayList;

public class Method {
    public String name;
    public String returnType;
    public ArrayList<String> parameters;
    public String access;

    public Method(String name, String returnType, ArrayList<String> parameters, String access) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.access = access;
    }

}
