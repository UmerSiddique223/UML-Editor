package core.class_diagram;

import java.util.ArrayList;

/**
 * Represents a method in a class diagram.
 * This class contains information about the method's name, return type,
 * parameters, and access modifier.
 */
public class Method {

    /** The name of the method. */
    public String name;

    /** The return type of the method. */
    public String returnType;

    /** The list of parameters for the method. */
    public ArrayList<String> parameters;

    /** The access modifier of the method (e.g., public, private). */
    public String access;

    /**
     * Gets the name of the method.
     *
     * @return the name of the method.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the return type of the method.
     *
     * @return the return type of the method.
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Gets the list of parameters for the method.
     *
     * @return the list of parameters for the method.
     */
    public ArrayList<String> getParameters() {
        return parameters;
    }

    /**
     * Gets the access modifier of the method.
     *
     * @return the access modifier of the method.
     */
    public String getAccess() {
        return access;
    }

    /**
     * Constructs a new Method object with the specified name, return type,
     * parameters, and access modifier.
     *
     * @param name the name of the method.
     * @param returnType the return type of the method.
     * @param parameters the list of parameters for the method.
     * @param access the access modifier of the method.
     */
    public Method(String name, String returnType, ArrayList<String> parameters, String access) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.access = access;
    }
}
