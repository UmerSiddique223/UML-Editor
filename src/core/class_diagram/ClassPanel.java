package core.class_diagram;


import javafx.geometry.Pos;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import ui.MainFrame;

import java.util.ArrayList;

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

        // Update ClassName when text is changed
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                String previousName = ClassName; // Store the old name
                ClassName = newValue.trim();

                canvas.onClassRename.accept(this, previousName); // Pass both the class and old name
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

    /**
     * Gets the list of attributes.
     *
     * @return the list of attributes
     */
    public ArrayList<Attribute> getAttributes() {
        return attributes;
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
                    TextInputDialog nameDialog = new TextInputDialog();
                    nameDialog.setTitle("Add Attribute");
                    nameDialog.setHeaderText("Enter Attribute Name:");
                    nameDialog.showAndWait().ifPresent(name -> {
                        TextInputDialog typeDialog = new TextInputDialog();
                        typeDialog.setTitle("Add Attribute");
                        typeDialog.setHeaderText("Enter Attribute Type:");
                        typeDialog.showAndWait().ifPresent(type -> {
                            TextInputDialog accessDialog = new TextInputDialog();
                            accessDialog.setTitle("Add Attribute");
                            accessDialog.setHeaderText("Enter Attribute Access Level (public, private, etc.):");
                            accessDialog.showAndWait().ifPresent(access -> {
                                Attribute attribute = new Attribute(name, type, access);
                                attributes.add(attribute);
                                updateAttributes();
                            });
                        });
                    });
                });
                contextMenu.getItems().add(addAttribute);
            }

            // Add Method
            MenuItem addMethod = new MenuItem("Add Method");
            addMethod.setOnAction(ev -> {
                TextInputDialog nameDialog = new TextInputDialog();
                nameDialog.setTitle("Add Method");
                nameDialog.setHeaderText("Enter Method Name:");
                nameDialog.showAndWait().ifPresent(name -> {
                    TextInputDialog returnTypeDialog = new TextInputDialog();
                    returnTypeDialog.setTitle("Add Method");
                    returnTypeDialog.setHeaderText("Enter Method Return Type:");
                    returnTypeDialog.showAndWait().ifPresent(returnType -> {
                        TextInputDialog paramsDialog = new TextInputDialog();
                        paramsDialog.setTitle("Add Method");
                        paramsDialog.setHeaderText("Enter Method Parameters (comma separated, e.g., int a, String b):");
                        paramsDialog.showAndWait().ifPresent(paramsStr -> {
                            ArrayList<String> parameters = new ArrayList<>();
                            if (!paramsStr.isEmpty()) {
                                String[] params = paramsStr.split(",");
                                for (String param : params) {
                                    parameters.add(param.trim());
                                }
                            }
                            Method method = new Method(name, returnType, parameters, "public"); // Default public for interfaces
                            methods.add(method);
                            updateMethods();
                        });
                    });
                });
            });

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

            contextMenu.getItems().addAll(addMethod, delete);
            contextMenu.show(this, e.getScreenX(), e.getScreenY());
            e.consume();
        }

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

    /**
     * Adds a method to the class.
     *
     * @param method the method to add
     */
    public void addMethod(Method method) {
        methods.add(method);
    }
}
