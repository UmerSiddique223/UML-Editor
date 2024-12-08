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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ClassPanel extends VBox {
    public String ClassName;
    public ClassDiagramCanvasPanel ParentCanvas;
    private final ArrayList<Attribute> attributes = new ArrayList<>();
    private final ArrayList<Method> methods = new ArrayList<>();
    private final boolean isInterface;
    public double x; // X-coordinate on the canvas
    public double y; // Y-coordinate on the canvas

    private Label typeLabel; // <<interface>> or empty for classes
    public TextField titleField;
    public final TextArea attributesArea;
    public final TextArea methodsArea;
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

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

    private void propagateEvents(Control control) {
        control.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
            this.fireEvent(event);
        });
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

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

    private String parseAccessLevel(String symbol) throws IllegalArgumentException {
        switch (symbol) {
            case "-":
                return "private";
            case "+":
                return "public";
            case "#":
                return "protected";
            default:
                throw new IllegalArgumentException("Invalid access level. Use -, +, or #.");
        }
    }
    private void updateAttributes() {
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

    private void updateMethods() {
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

    public boolean isInterface() {
        return  isInterface;
    }

    public String getClassName() {
        return ClassName;
    }

    public ArrayList<Method> getMethods() {
        return methods;
    }

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

    public void addMethod(Method method) {
        methods.add(method);
    }
}
