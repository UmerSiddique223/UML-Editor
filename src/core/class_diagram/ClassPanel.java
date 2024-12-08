package core.class_diagram;


import javafx.geometry.Pos;

import javafx.scene.Node;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import ui.MainFrame;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a visual panel for a class or interface in a class diagram.
 * Allows for displaying and editing attributes, methods, and other properties.
 */
public class ClassPanel extends VBox {
    /** The name of the class or interface. */
    public String ClassName;

    /** The parent canvas on which this class panel resides. */
    public ClassDiagramCanvasPanel ParentCanvas;

    /** The list of attributes for the class. */
    private final ArrayList<Attribute> attributes = new ArrayList<>();

    /** The list of methods for the class. */
    private final ArrayList<Method> methods = new ArrayList<>();

    /** Whether this panel represents an interface. */
    private final boolean isInterface;

    /** The X-coordinate of the panel on the canvas. */
    public double x;

    /** The Y-coordinate of the panel on the canvas. */
    public double y;

    /** Label to indicate the type (class or interface). */
    private Label typeLabel;

    /** Editable text field for the class name. */
    public TextField titleField;

    /** Text area to display attributes. */
    public final TextArea attributesArea;

    /** Text area to display methods. */
    public final TextArea methodsArea;

    /**
     * Constructs a new {@code ClassPanel} with the specified properties.
     *
     * @param name        the name of the class or interface
     * @param isInterface whether this represents an interface
     * @param x           the initial X-coordinate on the canvas
     * @param y           the initial Y-coordinate on the canvas
     * @param canvas      the parent canvas to which this panel belongs
     */
    public ClassPanel(String name, boolean isInterface, double x, double y, ClassDiagramCanvasPanel canvas) {
        this.ClassName = name;
        this.isInterface = isInterface;
        this.x = x;
        this.y = y;
        this.ParentCanvas = canvas;
        setStyle(isInterface
                ? "-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: white;"
                : "-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: white;");
        setPrefSize(200, 150);

        // Type Label (<<interface>> if it's an interface)
        typeLabel = new Label(isInterface ? "<<interface>>" : "");
        typeLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
        typeLabel.setMaxWidth(Double.MAX_VALUE);
        typeLabel.setAlignment(Pos.CENTER);

        // Title TextField (editable)
        titleField = new TextField(name);
        titleField.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center;");
        titleField.setMaxWidth(Double.MAX_VALUE);
        titleField.setAlignment(Pos.CENTER);
        titleField.focusedProperty().addListener((observable, oldFocus, newFocus) -> {
            if (!newFocus) { // Lost focus
                titleField.setText(ClassName);
                }
        });

        // Update ClassName when text is changed
        titleField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String newValue = titleField.getText().trim();

                if (!newValue.isEmpty()) {
                    String previousName = ClassName; // Store the old name

                    // Check if a class with the same name already exists
                    if (MainFrame.getClassDiagramCanvasPanel().getDiagram()
                            .getClasses().stream()
                            .anyMatch(existingClass -> existingClass.getClassName().equalsIgnoreCase(newValue))) {
                        showError("Error", "Class with the name '" + newValue + "' already exists.");
                        // Revert to the previous name
                        titleField.setText(previousName);
                        return;
                    }

                    // Update the class name
                    ClassName = newValue;
                    canvas.onClassRename.accept(this, previousName); // Trigger rename event
                } else {
                    showError("Error", "Class name cannot be empty.");
                    titleField.setText(ClassName); // Revert to the current class name
                }
            }
        });

        propagateEvents(titleField);

        // Attributes Section (hidden for interfaces)
        attributesArea = new TextArea("");
        attributesArea.setEditable(false);
        attributesArea.setWrapText(true);
        propagateEvents(attributesArea);

        // Methods Section
        methodsArea = new TextArea("");
        methodsArea.setEditable(false);
        methodsArea.setWrapText(true);
        propagateEvents(methodsArea);

        // Add UI elements conditionally
        getChildren().add(typeLabel);
        getChildren().add(this.titleField);
        if (!isInterface) {
            getChildren().add(attributesArea); // Classes have attributes
        }
        getChildren().add(methodsArea); // Both have methods

        setAlignment(Pos.CENTER);
        if (MainFrame.getClassDiagramCanvasPanel().getDrawingMode()==""){

            setOnMouseClicked(this::handleContextMenu);


        }

    /**
     * Sets the position of the panel on the canvas.
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
    /**
     * Gets the list of attributes.
     *
     * @return the list of attributes
     */
    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }


    private void propagateEventsToCanvas() {
        this.addEventFilter(MouseEvent.ANY, event -> {
            if (!ParentCanvas.getDrawingMode().isEmpty()) {
                event.consume(); // Prevent interactions if in drawing mode
            }
        });

        for (Node child : getChildren()) {
            child.addEventFilter(MouseEvent.ANY, event -> {
                if (!ParentCanvas.getDrawingMode().isEmpty()) {
                    event.consume(); // Prevent child-specific interactions in drawing mode
                }
            });
        }
    }

    // Helper Method to Propagate Events from a Specific Node
    private void propagateEvents(Node node) {
        node.addEventFilter(MouseEvent.ANY, event -> {
            if (!ParentCanvas.getDrawingMode().isEmpty()) {
                event.consume(); // Prevent interactions in drawing mode
            } else {
                // Allow propagation to parent (ClassPanel)
                event.fireEvent(this, event.copyFor(this, this));
            }
        });
    }

    private void propagateEvents(Control control) {
        control.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
            this.fireEvent(event);
        });
    }

    /**
     * Gets the X-coordinate of the panel.
     *
     * @return the X-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the X-coordinate of the panel.
     *
     * @param x the X-coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the Y-coordinate of the panel.
     *
     * @return the Y-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the Y-coordinate of the panel.
     *
     * @param y the Y-coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    private void handleContextMenu(MouseEvent e) {
        if (e.getButton() == MouseButton.SECONDARY) {
            ContextMenu contextMenu = new ContextMenu();

            if (!isInterface) {
                // Add Attribute (only for classes)
                MenuItem addAttribute = new MenuItem("Add Attribute");
                addAttribute.setOnAction(ev -> {
                    // Single dialog for attribute input
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Add Attribute");
                    dialog.setHeaderText("Enter Attribute in Format: [access] [type] [name]");
                    dialog.setContentText("Example: -\n+ int age (for public)");

                    dialog.showAndWait().ifPresent(input -> {
                        try {
                            // Split the input into access, type, and name
                            String[] parts = input.trim().split("\\s+");
                            if (parts.length != 3) {
                                throw new IllegalArgumentException("Invalid format. Expected: [access] [type] [name]");
                            }

                            // Parse shorthand access modifiers
                            String accessSymbol = parts[0];
                            String access = parseAccessLevel(accessSymbol);

                            String type = parts[1];
                            String name = parts[2];
                            if (attributes.stream().anyMatch(attr -> attr.getName().equals(name))) {
                                throw new IllegalArgumentException("Attribute with the name '" + name + "' already exists.");
                            }
                            // Create and add the attribute
                            Attribute attribute = new Attribute(name, type, access);
                            attributes.add(attribute);
                            updateAttributes();
                        } catch (IllegalArgumentException error) {
                            showError("Invalid Attribute Format", error.getMessage());
                        }
                    });
                });
                contextMenu.getItems().add(addAttribute);
            }


            // Method
            if (!isInterface) {
            MenuItem addMethod = new MenuItem("Add Method");
            addMethod.setOnAction(ev -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add Method");
                dialog.setHeaderText("Enter Method in Format: [access] [returnType] [name](params)");
                dialog.setContentText("Example: + int calculateSum(int a, int b)");

                dialog.showAndWait().ifPresent(input -> {
                    try {
                        // Parse the method input
                        Method method = parseMethodInput(input);
                        if (methods.stream().anyMatch(existingMethod ->
                                existingMethod.getName().equals(method.getName()) &&
                                        existingMethod.getParameters().equals(method.getParameters()))) {
                            throw new IllegalArgumentException("Method with the same name and parameters already exists.");
                        }
                        methods.add(method);
                        updateMethods();
                    } catch (IllegalArgumentException error)
                    {
                        showError("Invalid Method Format", error.getMessage());
                    }
                });
            });
            contextMenu.getItems().add(addMethod);
}
            // Delete Panel
            MenuItem delete = new MenuItem("Delete " + (isInterface ? "Interface" : "Class"));
            delete.setOnAction(ev -> {
                Pane parent = (Pane) getParent();
                if (parent instanceof StackPane stackPane) {
                    Pane grandParent = (Pane) stackPane.getParent();
                    grandParent.getChildren().remove(stackPane);
                }
                MainFrame.getClassDiagramCanvasPanel().getDiagram().removeClass(ClassName);
                MainFrame.getClassDiagramCanvasPanel().getDiagram().removeRelations_of_a_Diagram(ClassName);

            });

            contextMenu.getItems().addAll( delete);
            contextMenu.show(this, e.getScreenX(), e.getScreenY());
            e.consume();
        }

    }
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private Method parseMethodInput(String input) throws IllegalArgumentException {
        String accessRegex = "[+\\-#]"; // Valid symbols for access modifiers
        String methodRegex = String.format(
                "^\\s*(%s)\\s+(\\w+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*$",
                accessRegex
        );
        Pattern pattern = Pattern.compile(methodRegex);
        Matcher matcher = pattern.matcher(input.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid format. Expected: [access] [returnType] [name](params)");
        }

        // Extract components
        String accessSymbol = matcher.group(1);
        String returnType = matcher.group(2);
        String name = matcher.group(3);
        String paramsStr = matcher.group(4);

        String access = parseAccessLevel(accessSymbol);

        // Parse parameters
        ArrayList<String> parameters = new ArrayList<>();
        if (!paramsStr.isEmpty()) {
            String[] params = paramsStr.split(",");
            for (String param : params) {
                parameters.add(param.trim());
            }
        }

        return new Method(name, returnType, parameters, access);
    }

    /**
     * Updates the displayed attributes on the panel.
     */
    public void updateAttributes() {
        StringBuilder attributesText = new StringBuilder();
        for (Attribute attribute : attributes) {
            String accessSymbol = getAccessSymbol(attribute.access);
            attributesText.append(accessSymbol)
                    .append(" ")
                    .append(attribute.name)
                    .append(" :")
                    .append(attribute.type)
                    .append("\n");
        }
        attributesArea.setText(attributesText.toString());
    }

    /**
     * Updates the displayed methods on the panel.
     */
    public void updateMethods() {
        StringBuilder methodsText = new StringBuilder();
        for (Method method : methods) {
            String accessSymbol = isInterface ? "+" : getAccessSymbol(method.access);
            methodsText.append(accessSymbol)
                    .append(" ")
                    .append(method.name)
                    .append(" (");
            if (!method.parameters.isEmpty()) {
                methodsText.append(String.join(", ", method.parameters));
            }
            methodsText.append(") :")
                    .append(method.returnType)
                    .append("\n");
        }
        methodsArea.setText(methodsText.toString());
    }

    /**
     * Gets the relevant Access symbol based on the access type.
     */
    private String getAccessSymbol(String access) {
        switch (access) {
            case "public":
                return "+";
            case "private":
                return "-";
            case "protected":
                return "#";
            default:
                return ""; // Default for unsupported access types
        }
    }

    /**
     * Checks if the panel represents an interface.
     *
     * @return {@code true} if it is an interface, otherwise {@code false}
     */
    public boolean isInterface() {
        return isInterface;
    }

    public String getClassName() {
        return ClassName;
    }

    /**
     * Gets the list of methods.
     *
     * @return the list of methods
     */
    public ArrayList<Method> getMethods() {
        return methods;
    }

    /**
     * Adds an attribute to the class.
     *
     * @param attribute the attribute to add
     */
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);

    }

    public void setMethods(ArrayList<Method> methods) {
        this.methods.clear();
        this.methods.addAll(methods);
        updateMethods();
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes.clear();
        this.attributes.addAll(attributes);
        updateAttributes();
    }

    /**
     * Adds a method to the class.
     *
     * @param method the method to add
     */
    public void addMethod(Method method) {
        methods.add(method);
    }
}
