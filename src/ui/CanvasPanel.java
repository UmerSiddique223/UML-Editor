package ui;

import core.ClassShape;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class CanvasPanel extends Pane {
    private final ArrayList<ClassShape> shapes = new ArrayList<>();
    private ClassShape currentShape;

    public CanvasPanel() {
        setStyle("-fx-background-color: white;");
        setPrefSize(800, 600);

        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                showContextMenu(e.getScreenX(), e.getScreenY(), e.getX(), e.getY());
            }
        });
    }

    private void showContextMenu(double screenX, double screenY, double x, double y) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem addClassDiagram = new MenuItem("Add Class Diagram");
        addClassDiagram.setOnAction(ev -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Class Diagram");
            dialog.setHeaderText("Enter Class Diagram Name:");
            dialog.showAndWait().ifPresent(name -> addClassDiagramToCanvas(name, x, y));
        });

        contextMenu.getItems().add(addClassDiagram);
        contextMenu.show(this, screenX, screenY);
    }

    private void addClassDiagramToCanvas(String name, double x, double y) {
        ClassDiagramPanel classPanel = new ClassDiagramPanel(name);

        // Create a draggable rectangle around the diagram
        Rectangle rect = new Rectangle(200, 150);
        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);

        // Use StackPane to combine the rectangle and the diagram
        StackPane container = new StackPane();
        container.setLayoutX(x);
        container.setLayoutY(y);
        container.setPrefSize(200, 150);
        container.getChildren().addAll(rect, classPanel);

        enableDragging(container, classPanel);
//        enableResizing(container);

        getChildren().add(container);
    }
    private void enableDragging(StackPane container, ClassDiagramPanel classPanel) {
        // Track double-tap timing
        final long[] lastTapTime = {0};

        classPanel.titleLabel.setOnMousePressed(event -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTapTime[0] < 300) { // Double-tap detected within 300ms
                container.setUserData(new double[]{event.getSceneX(), event.getSceneY(), container.getLayoutX(), container.getLayoutY()});
                lastTapTime[0] = 0; // Reset the timer
            } else {
                lastTapTime[0] = currentTime; // Update the timer
            }
        });

        classPanel.titleLabel.setOnMouseDragged(event -> {
            double[] userData = (double[]) container.getUserData();
            if (userData != null) {
                double deltaX = event.getSceneX() - userData[0];
                double deltaY = event.getSceneY() - userData[1];

                // Ensure the container doesn't go outside parent bounds
                double newX = Math.max(0, Math.min(getWidth() - container.getWidth(), userData[2] + deltaX));
                double newY = Math.max(0, Math.min(getHeight() - container.getHeight(), userData[3] + deltaY));

                container.setLayoutX(newX);
                container.setLayoutY(newY);
            }
        });
    }



    private void enableResizing(StackPane container) {
        final double RESIZE_MARGIN = 10;

        container.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            double width = container.getWidth();
            double height = container.getHeight();

            if (mouseX > width - RESIZE_MARGIN && mouseY > height - RESIZE_MARGIN) {
                container.setCursor(Cursor.SE_RESIZE);
            } else if (mouseX < RESIZE_MARGIN && mouseY > height - RESIZE_MARGIN) {
                container.setCursor(Cursor.SW_RESIZE);
            } else if (mouseX > width - RESIZE_MARGIN && mouseY < RESIZE_MARGIN) {
                container.setCursor(Cursor.NE_RESIZE);
            } else if (mouseX < RESIZE_MARGIN && mouseY < RESIZE_MARGIN) {
                container.setCursor(Cursor.NW_RESIZE);
            } else if (mouseX > width - RESIZE_MARGIN) {
                container.setCursor(Cursor.E_RESIZE);
            } else if (mouseX < RESIZE_MARGIN) {
                container.setCursor(Cursor.W_RESIZE);
            } else if (mouseY > height - RESIZE_MARGIN) {
                container.setCursor(Cursor.S_RESIZE);
            } else if (mouseY < RESIZE_MARGIN) {
                container.setCursor(Cursor.N_RESIZE);
            } else {
                container.setCursor(Cursor.DEFAULT);
            }
        });

        container.setOnMousePressed(event -> {
            if (container.getCursor() != Cursor.DEFAULT) {
                container.setUserData(new double[]{
                        event.getSceneX(), event.getSceneY(),
                        container.getWidth(), container.getHeight(),
                        container.getLayoutX(), container.getLayoutY()
                });
            }
        });

        container.setOnMouseDragged(event -> {
            Cursor cursor = container.getCursor();
            if (cursor != Cursor.DEFAULT) {
                double[] userData = (double[]) container.getUserData();
                double initialX = userData[0];
                double initialY = userData[1];
                double initialWidth = userData[2];
                double initialHeight = userData[3];
                double initialLayoutX = userData[4];
                double initialLayoutY = userData[5];

                double deltaX = event.getSceneX() - initialX;
                double deltaY = event.getSceneY() - initialY;

                if (cursor == Cursor.SE_RESIZE) {
                    container.setPrefSize(Math.max(50, initialWidth + deltaX), Math.max(50, initialHeight + deltaY));
                } else if (cursor == Cursor.SW_RESIZE) {
                    container.setPrefSize(Math.max(50, initialWidth - deltaX), Math.max(50, initialHeight + deltaY));
                    container.setLayoutX(initialLayoutX + deltaX);
                } else if (cursor == Cursor.NE_RESIZE) {
                    container.setPrefSize(Math.max(50, initialWidth + deltaX), Math.max(50, initialHeight - deltaY));
                    container.setLayoutY(initialLayoutY + deltaY);
                } else if (cursor == Cursor.NW_RESIZE) {
                    container.setPrefSize(Math.max(50, initialWidth - deltaX), Math.max(50, initialHeight - deltaY));
                    container.setLayoutX(initialLayoutX + deltaX);
                    container.setLayoutY(initialLayoutY + deltaY);
                } else if (cursor == Cursor.E_RESIZE) {
                    container.setPrefSize(Math.max(50, initialWidth + deltaX), initialHeight);
                } else if (cursor == Cursor.W_RESIZE) {
                    container.setPrefSize(Math.max(50, initialWidth - deltaX), initialHeight);
                    container.setLayoutX(initialLayoutX + deltaX);
                } else if (cursor == Cursor.S_RESIZE) {
                    container.setPrefSize(initialWidth, Math.max(50, initialHeight + deltaY));
                } else if (cursor == Cursor.N_RESIZE) {
                    container.setPrefSize(initialWidth, Math.max(50, initialHeight - deltaY));
                    container.setLayoutY(initialLayoutY + deltaY);
                }
            }
        });

        container.setOnMouseReleased(event -> container.setCursor(Cursor.DEFAULT));
    }

    public void addClassDiagram(String className) {
        addClassDiagramToCanvas(className, 50, 50);
    }
}
