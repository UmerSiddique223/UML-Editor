package core.class_diagram;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

// A panel representing a Class
public  class ClassPanel extends VBox {
    public String ClassName;
    private final ArrayList<Attribute> attributes = new ArrayList<>();
    private final ArrayList<Method> methods = new ArrayList<>();

    final Label titleLabel;
    private final TextArea attributesArea;
    private final TextArea methodsArea;

    public ClassPanel(String className) {
        ClassName = className;
        setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: white;");
        setPrefSize(200, 150);

        // Title
        titleLabel = new Label(className);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        propagateEvents(titleLabel);
        // Attributes Section
        attributesArea = new TextArea("Attributes...");
        attributesArea.setEditable(false);
        attributesArea.setWrapText(true);
        propagateEvents(attributesArea);
        // Methods Section
        methodsArea = new TextArea("Methods...");
        methodsArea.setEditable(false);
        methodsArea.setWrapText(true);
        propagateEvents(methodsArea);
        getChildren().addAll(titleLabel, attributesArea, methodsArea);
        setOnMouseClicked(this::handleContextMenu);
    }

    private void propagateEvents(Control control) {
        control.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
            this.fireEvent(event);
        });
    }
    private void handleContextMenu(MouseEvent e) {
        if (e.getButton() == MouseButton.SECONDARY) {
            ContextMenu contextMenu = new ContextMenu();

            MenuItem addAttribute = new MenuItem("Add Attribute");
            addAttribute.setOnAction(ev -> {
                // Dialog to get attribute details (name, type, access)
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
                            // Create an attribute object with the collected information
                            Attribute attribute = new Attribute(name, type, access);
                            attributes.add(attribute);
                            updateAttributes(); // Assuming you will update the UI with the new attribute list
                        });
                    });
                });
            });

            MenuItem addMethod = new MenuItem("Add Method");
            addMethod.setOnAction(ev -> {
                // Dialog to get method details (name, return type, parameters, access)
                TextInputDialog nameDialog = new TextInputDialog();
                nameDialog.setTitle("Add Method");
                nameDialog.setHeaderText("Enter Method Name:");
                nameDialog.showAndWait().ifPresent(name -> {
                    TextInputDialog returnTypeDialog = new TextInputDialog();
                    returnTypeDialog.setTitle("Add Method");
                    returnTypeDialog.setHeaderText("Enter Method Return Type:");
                    returnTypeDialog.showAndWait().ifPresent(returnType -> {
                        TextInputDialog accessDialog = new TextInputDialog();
                        accessDialog.setTitle("Add Method");
                        accessDialog.setHeaderText("Enter Method Access Level (public, private, etc.):");
                        accessDialog.showAndWait().ifPresent(access -> {
                            // Dialog to get method parameters
                            TextInputDialog paramsDialog = new TextInputDialog();
                            paramsDialog.setTitle("Add Method");
                            paramsDialog.setHeaderText("Enter Method Parameters (comma separated, e.g., int a, String b):");
                            paramsDialog.showAndWait().ifPresent(paramsStr -> {
                                // Split the parameters by commas and create an ArrayList of parameters
                                ArrayList<String> parameters = new ArrayList<>();
                                if (!paramsStr.isEmpty()) {
                                    String[] params = paramsStr.split(",");
                                    for (String param : params) {
                                        parameters.add(param.trim());
                                    }
                                }
                                // Create a method object with the collected information
                                Method method = new Method(name, returnType, parameters, access);
                                methods.add(method);
                                updateMethods(); // Assuming you will update the UI with the new method list
                            });
                        });
                    });
                });
            });


            MenuItem delete = new MenuItem("Delete Class");
            delete.setOnAction(ev -> {
                Pane parent = (Pane) getParent();
                if (parent instanceof StackPane stackPane) {
                    Pane grandParent = (Pane) stackPane.getParent();
                    grandParent.getChildren().remove(stackPane);
                }
            });

            contextMenu.getItems().addAll(addAttribute, addMethod, delete);
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
            String accessSymbol = getAccessSymbol(method.access);
            methodsText.append(accessSymbol)
                    .append(" ")
                    .append(method.name)
                    .append(" (");

            // Join method parameters with commas
            if (!method.parameters.isEmpty()) {
                methodsText.append(String.join(", ", method.parameters));
            }

            methodsText.append(") :")
                    .append(method.returnType)
                    .append("\n");
        }
        methodsArea.setText(methodsText.toString());
    }

    // Helper method to convert access type to symbol
    private String getAccessSymbol(String access) {
        switch (access) {
            case "public":
                return "+";
            case "private":
                return "-";
            case "protected":
                return "#";
            default:
                return "";  // Default case for unsupported access types
        }
    }


}