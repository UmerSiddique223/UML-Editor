package core.class_diagram;

/**
 * Represents an attribute in a class diagram, encapsulating its name, type, and access modifier.
 */
public class Attribute {
    /**
     * The name of the attribute.
     */
    public String name;

    /**
     * The data type of the attribute.
     */
    public String type;

    /**
     * The access modifier of the attribute (e.g., public, private, protected).
     */
    public String access;

    /**
     * Constructs an Attribute with the specified name, type, and access modifier.
     *
     * @param name   the name of the attribute
     * @param type   the data type of the attribute
     * @param access the access modifier of the attribute
     */
    public Attribute(String name, String type, String access) {
        this.name = name;
        this.type = type;
        this.access = access;
    }

    /**
     * Returns the name of the attribute.
     *
     * @return the name of the attribute
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the data type of the attribute.
     *
     * @return the data type of the attribute
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the access modifier of the attribute.
     *
     * @return the access modifier of the attribute
     */
    public String getAccess() {
        return access;
    }
}
