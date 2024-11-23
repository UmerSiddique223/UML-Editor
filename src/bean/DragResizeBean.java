package bean;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import ui.AbstractDiagramPanel;

import java.util.ArrayList;

public class DragResizeBean {

    private static final double RESIZE_MARGIN = 10;

    public static void apply(Region target, Pane parent) {
        enableDragAndResize(target, parent);
    }

    private static void enableDragAndResize(Region target, Pane parent) {
        final double[] dragData = new double[6];
        final boolean[] resizing = {false};

        target.setOnMouseMoved(event -> {
            updateCursor(target, event);
        });

        target.setOnMousePressed(event -> {
            Cursor cursor = target.getCursor();
            resizing[0] = cursor != Cursor.DEFAULT;

            if (resizing[0]) {
                // Prepare for resizing
                dragData[0] = event.getSceneX();
                dragData[1] = event.getSceneY();
                dragData[2] = target.getWidth();
                dragData[3] = target.getHeight();
                dragData[4] = target.getLayoutX();
                dragData[5] = target.getLayoutY();
            } else {
                // Prepare for dragging
                dragData[0] = event.getSceneX();
                dragData[1] = event.getSceneY();
                dragData[4] = target.getLayoutX();
                dragData[5] = target.getLayoutY();
            }
        });

        target.setOnMouseDragged(event -> {
            if (resizing[0]) {
                // Handle resizing
                resize(target, event, parent, dragData);
            } else {
                // Handle dragging
                double deltaX = event.getSceneX() - dragData[0];
                double deltaY = event.getSceneY() - dragData[1];

                double newX = Math.max(0, Math.min(parent.getWidth() - target.getWidth(), dragData[4] + deltaX));
                double newY = Math.max(0, Math.min(parent.getHeight() - target.getHeight(), dragData[5] + deltaY));

                target.setLayoutX(newX);
                target.setLayoutY(newY);
            }
        });

        target.setOnMouseReleased(event -> {
            target.setCursor(Cursor.DEFAULT);
            resizing[0] = false;
        });
    }

    private static void updateCursor(Region target, MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        double width = target.getWidth();
        double height = target.getHeight();

        if (mouseX > width - RESIZE_MARGIN && mouseY > height - RESIZE_MARGIN) {
            target.setCursor(Cursor.SE_RESIZE);
        } else if (mouseX < RESIZE_MARGIN && mouseY > height - RESIZE_MARGIN) {
            target.setCursor(Cursor.SW_RESIZE);
        } else if (mouseX > width - RESIZE_MARGIN && mouseY < RESIZE_MARGIN) {
            target.setCursor(Cursor.NE_RESIZE);
        } else if (mouseX < RESIZE_MARGIN && mouseY < RESIZE_MARGIN) {
            target.setCursor(Cursor.NW_RESIZE);
        } else if (mouseX > width - RESIZE_MARGIN) {
            target.setCursor(Cursor.E_RESIZE);
        } else if (mouseX < RESIZE_MARGIN) {
            target.setCursor(Cursor.W_RESIZE);
        } else if (mouseY > height - RESIZE_MARGIN) {
            target.setCursor(Cursor.S_RESIZE);
        } else if (mouseY < RESIZE_MARGIN) {
            target.setCursor(Cursor.N_RESIZE);
        } else {
            target.setCursor(Cursor.DEFAULT);
        }
    }

    private static void resize(Region target, MouseEvent event, Pane parent, double[] dragData) {
        Cursor cursor = target.getCursor();
        double deltaX = event.getSceneX() - dragData[0];
        double deltaY = event.getSceneY() - dragData[1];

        double newWidth = dragData[2];
        double newHeight = dragData[3];
        double newLayoutX = dragData[4];
        double newLayoutY = dragData[5];

        if (cursor == Cursor.SE_RESIZE) {
            newWidth = Math.min(Math.max(50, dragData[2] + deltaX), parent.getWidth() - newLayoutX);
            newHeight = Math.min(Math.max(50, dragData[3] + deltaY), parent.getHeight() - newLayoutY);
        } else if (cursor == Cursor.SW_RESIZE) {
            newWidth = Math.min(Math.max(50, dragData[2] - deltaX), newLayoutX + dragData[2]);
            newHeight = Math.min(Math.max(50, dragData[3] + deltaY), parent.getHeight() - newLayoutY);
            newLayoutX = Math.max(0, Math.min(newLayoutX + deltaX, parent.getWidth() - newWidth));
        } else if (cursor == Cursor.NE_RESIZE) {
            newWidth = Math.min(Math.max(50, dragData[2] + deltaX), parent.getWidth() - newLayoutX);
            newHeight = Math.min(Math.max(50, dragData[3] - deltaY), newLayoutY + dragData[3]);
            newLayoutY = Math.max(0, Math.min(newLayoutY + deltaY, parent.getHeight() - newHeight));
        } else if (cursor == Cursor.NW_RESIZE) {
            newWidth = Math.min(Math.max(50, dragData[2] - deltaX), newLayoutX + dragData[2]);
            newHeight = Math.min(Math.max(50, dragData[3] - deltaY), newLayoutY + dragData[3]);
            newLayoutX = Math.max(0, Math.min(newLayoutX + deltaX, parent.getWidth() - newWidth));
            newLayoutY = Math.max(0, Math.min(newLayoutY + deltaY, parent.getHeight() - newHeight));
        } else if (cursor == Cursor.E_RESIZE) {
            newWidth = Math.min(Math.max(50, dragData[2] + deltaX), parent.getWidth() - newLayoutX);
        } else if (cursor == Cursor.W_RESIZE) {
            newWidth = Math.min(Math.max(50, dragData[2] - deltaX), newLayoutX + dragData[2]);
            newLayoutX = Math.max(0, Math.min(newLayoutX + deltaX, parent.getWidth() - newWidth));
        } else if (cursor == Cursor.S_RESIZE) {
            newHeight = Math.min(Math.max(50, dragData[3] + deltaY), parent.getHeight() - newLayoutY);
        } else if (cursor == Cursor.N_RESIZE) {
            newHeight = Math.min(Math.max(50, dragData[3] - deltaY), newLayoutY + dragData[3]);
            newLayoutY = Math.max(0, Math.min(newLayoutY + deltaY, parent.getHeight() - newHeight));
        }

        target.setPrefSize(newWidth, newHeight);
        target.setLayoutX(newLayoutX);
        target.setLayoutY(newLayoutY);
    }

    public static class ClassDiagramPanel extends AbstractDiagramPanel {
        private final ArrayList<String> attributes = new ArrayList<>();
        private final ArrayList<String> methods = new ArrayList<>();
        final Label titleLabel;
        private final TextArea attributesArea;
        private final TextArea methodsArea;

        public ClassDiagramPanel(String className) {
            super(className);
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
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Add Attribute");
                    dialog.setHeaderText("Enter Attribute:");
                    dialog.showAndWait().ifPresent(attribute -> {
                        attributes.add(attribute);
                        updateAttributes();
                    });
                });

                MenuItem addMethod = new MenuItem("Add Method");
                addMethod.setOnAction(ev -> {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Add Method");
                    dialog.setHeaderText("Enter Method:");
                    dialog.showAndWait().ifPresent(method -> {
                        methods.add(method);
                        updateMethods();
                    });
                });

                MenuItem delete = new MenuItem("Delete Class Diagram");
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
            attributesArea.setText(String.join("\n", attributes));
        }

        private void updateMethods() {
            methodsArea.setText(String.join("\n", methods));
        }
    }
}
