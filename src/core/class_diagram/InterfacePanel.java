package core.class_diagram;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

// A panel representing an Interface
public class InterfacePanel extends VBox {
    public String interfaceName;
    private final ArrayList<Method> methods = new ArrayList<>();

    private final Label interfaceLabel;
    private final Label titleLabel;
    private final TextArea methodsArea;

    public InterfacePanel(String interfaceName) {
        this.interfaceName = interfaceName;
        setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: white; -fx-shape: 'M50,0 A50,50 0 1,0 50,-0.1 Z';");
        setPrefSize(200, 150);

        // Interface label
        interfaceLabel = new Label("<<interface>>");
        interfaceLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
        interfaceLabel.setMaxWidth(Double.MAX_VALUE);
        interfaceLabel.setAlignment(Pos.CENTER);

        // Title
        titleLabel = new Label(interfaceName);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);

        propagateEvents(titleLabel);

        // Methods Section
        methodsArea = new TextArea("");
        methodsArea.setEditable(false);
        methodsArea.setWrapText(true);
        propagateEvents(methodsArea);

        getChildren().addAll(interfaceLabel, titleLabel, methodsArea);
        setAlignment(Pos.CENTER);
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
                            Method method = new Method(name, returnType, parameters, "public"); // Default access is public for interfaces
                            methods.add(method);
                            updateMethods(); // Update the UI with the new method list
                        });
                    });
                });
            });

            MenuItem delete = new MenuItem("Delete Interface");
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

    private void updateMethods() {
        StringBuilder methodsText = new StringBuilder();
        for (Method method : methods) {
            methodsText.append("+ ") // Access modifier for interface methods is always public
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
}
