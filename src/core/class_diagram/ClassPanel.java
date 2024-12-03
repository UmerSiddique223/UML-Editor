package core.class_diagram;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ClassPanel extends VBox {
    public String ClassName;
    private final ArrayList<Attribute> attributes = new ArrayList<>();
    private final ArrayList<Method> methods = new ArrayList<>();
    private final boolean isInterface;
    public double x; // X-coordinate on the canvas
    public double y; // Y-coordinate on the canvas

    private final Label typeLabel; // <<interface>> or empty for classes
    private final Label titleLabel;
    private final TextArea attributesArea;
    private final TextArea methodsArea;
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public ClassPanel(String name, boolean isInterface, double x, double y) {
        this.ClassName = name;
        this.isInterface = isInterface;
        this.x = x;
        this.y = y;
        setStyle(isInterface
                ? "-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: white;"
                : "-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: white;");
        setPrefSize(200, 150);

        // Type Label (<<interface>> if it's an interface)
        typeLabel = new Label(isInterface ? "<<interface>>" : "");
        typeLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
        typeLabel.setMaxWidth(Double.MAX_VALUE);
        typeLabel.setAlignment(Pos.CENTER);

        // Title Label
        titleLabel = new Label(name);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        propagateEvents(titleLabel);

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
        getChildren().add(titleLabel);
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
            });

            contextMenu.getItems().addAll(addMethod, delete);
            contextMenu.show(this, e.getScreenX(), e.getScreenY());
            e.consume();
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
}
