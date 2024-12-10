package core.class_diagram;


import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
//    public final TextArea attributesArea;
//    public final TextArea methodsArea;
    private final VBox attributesBox; // Container for attribute labels
    private final VBox methodsBox;    // Container for method labels
    private final VBox attributesContainer; // Container for attributes
    private final VBox methodsContainer;    // Container for methods
    private final Label emptyAttributesLabel = new Label("No attributes added");
    private final Label emptyMethodsLabel = new Label("No methods added");


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
            MainFrame.getPropertiesBar().refresh();

        });

        propagateEvents(titleField);

        attributesBox = new VBox();
        attributesBox.setSpacing(5);
        attributesBox.setPadding(new Insets(5));

        methodsBox = new VBox();
        methodsBox.setSpacing(5);
        methodsBox.setPadding(new Insets(5));


        // Add UI elements conditionally
        getChildren().add(typeLabel);
        getChildren().add(this.titleField);

        // Attributes Section (hidden for interfaces)
        attributesContainer = new VBox();
        attributesContainer.setSpacing(5);
        attributesContainer.setStyle("-fx-padding: 5; -fx-border-color: gray; -fx-border-width: 1;");
        if (!isInterface) {
            emptyAttributesLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
            attributesContainer.getChildren().add(emptyAttributesLabel); // Add placeholder
            getChildren().add(attributesContainer);
        }

        // Methods Section
        methodsContainer = new VBox();
        methodsContainer.setSpacing(5);
        methodsContainer.setStyle("-fx-padding: 5; -fx-border-color: gray; -fx-border-width: 1;");
        emptyMethodsLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
        methodsContainer.getChildren().add(emptyMethodsLabel); // Add placeholder
        getChildren().add(methodsContainer);



        if (!isInterface) {
            getChildren().add(attributesBox); // Classes have attributes
        }
        getChildren().add(methodsBox); // Both have methods

        setAlignment(Pos.CENTER);
        if (MainFrame.getClassDiagramCanvasPanel().getDrawingMode()==""){

            setOnMouseClicked(this::handleContextMenu);


        }

        propagateEventsToCanvas();

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
                MenuItem addAttribute = new MenuItem("Add Attribute");
                addAttribute.setOnAction(ev -> {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Add Attribute");
                    dialog.setHeaderText("Enter Attribute in Format: [access] [type] [name]");
                    dialog.setContentText("Example: + int age");

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
                            addAttribute(attribute); // Updated to use the new logic
                        } catch (IllegalArgumentException error) {
                            showError("Invalid Attribute Format", error.getMessage());
                        }
                        MainFrame.getPropertiesBar().refresh();
                    });
                });
                contextMenu.getItems().add(addAttribute);
            }


            // Method
//            if (!isInterface) {
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

                            addMethod(method); // Updated to use the new logic
                        } catch (IllegalArgumentException error) {
                            showError("Invalid Method Format", error.getMessage());
                        }
                    });
                    MainFrame.getPropertiesBar().refresh();
                });

                contextMenu.getItems().add(addMethod);
                MainFrame.getPropertiesBar().refresh();

//            }
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
                MainFrame.getPropertiesBar().refresh();

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
//    private void updateAttributes() {
//        StringBuilder attributesText = new StringBuilder();
//        for (Attribute attribute : attributes) {
//            String accessSymbol = getAccessSymbol(attribute.access);
//            attributesText.append(accessSymbol)
//                    .append(" ")
//                    .append(attribute.name)
//                    .append(" :")
//                    .append(attribute.type)
//                    .append("\n");
//        }
//        attributesArea.setText(attributesText.toString());
//    }
//
//    private void updateMethods() {
//        StringBuilder methodsText = new StringBuilder();
//        for (Method method : methods) {
//            String accessSymbol = isInterface ? "+" : getAccessSymbol(method.access);
//            methodsText.append(accessSymbol)
//                    .append(" ")
//                    .append(method.name)
//                    .append(" (");
//            if (!method.parameters.isEmpty()) {
//                methodsText.append(String.join(", ", method.parameters));
//            }
//            methodsText.append(") :")
//                    .append(method.returnType)
//                    .append("\n");
//        }
//        methodsArea.setText(methodsText.toString());
//    }

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

        HBox attributeBox = new HBox();
        attributeBox.setSpacing(10);

        String accessSymbol = getAccessSymbol(attribute.access);
        Label attributeLabel = new Label(accessSymbol + " " + attribute.name + " : " + attribute.type);
        attributeLabel.setStyle("-fx-font-size: 12px;");

        Button deleteButton = new Button("X");
        deleteButton.setOnAction(event -> {
            attributes.remove(attribute);
            attributesContainer.getChildren().remove(attributeBox);
            togglePlaceholder(attributesContainer, emptyAttributesLabel, attributes.isEmpty());
        });

        attributeBox.getChildren().addAll(attributeLabel, deleteButton);
        attributesContainer.getChildren().add(attributeBox);
        togglePlaceholder(attributesContainer, emptyAttributesLabel, attributes.isEmpty());
    }



    public void setMethods(ArrayList<Method> methods) {
        this.methods.clear();
        this.methods.addAll(methods);
        methodsBox.getChildren().clear();
        for (Method method : methods) {
            addMethod(method);
        }
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes.clear();
        this.attributes.addAll(attributes);
        attributesBox.getChildren().clear();
        for (Attribute attribute : attributes) {
            addAttribute(attribute);
        }
    }

    public void addMethod(Method method) {
        methods.add(method);

        HBox methodBox = new HBox();
        methodBox.setSpacing(10);

        String accessSymbol = isInterface ? "+" : getAccessSymbol(method.access);
        Label methodLabel = new Label(accessSymbol + " " + method.name + " ("
                + String.join(", ", method.parameters) + ") : " + method.returnType);
        methodLabel.setStyle("-fx-font-size: 12px;");

        Button deleteButton = new Button("X");
        deleteButton.setOnAction(event -> {
            methods.remove(method);
            methodsContainer.getChildren().remove(methodBox);
            togglePlaceholder(methodsContainer, emptyMethodsLabel, methods.isEmpty());
        });

        methodBox.getChildren().addAll(methodLabel, deleteButton);
        methodsContainer.getChildren().add(methodBox);
        togglePlaceholder(methodsContainer, emptyMethodsLabel, methods.isEmpty());
    }

    private void togglePlaceholder(VBox container, Label placeholder, boolean isEmpty) {
        if (isEmpty) {
            if (!container.getChildren().contains(placeholder)) {
                container.getChildren().add(placeholder);
            }
        } else {
            container.getChildren().remove(placeholder);
        }
    }



    private String formatAttribute(Attribute attribute) {
        String accessSymbol = getAccessSymbol(attribute.access);
        return accessSymbol + " " + attribute.name + " : " + attribute.type;
    }

    private String formatMethod(Method method) {
        String accessSymbol = isInterface ? "+" : getAccessSymbol(method.access);
        String params = String.join(", ", method.parameters);
        return accessSymbol + " " + method.name + "(" + params + ") : " + method.returnType;
    }
}
