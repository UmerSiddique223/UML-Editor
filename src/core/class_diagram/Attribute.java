package core.class_diagram;

public class Attribute {
    public String name;
    public String type;
    public String access;

    public Attribute(String name, String type, String access) {
        this.name = name;
        this.type = type;
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAccess() {
        return access;
    }
}
