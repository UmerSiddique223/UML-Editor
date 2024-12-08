package bean;

import core.class_diagram.ClassPanel;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import ui.MainFrame;

import java.util.ArrayList;

/**
 * A utility class that provides functionality for dragging and resizing UI components.
 * This class enables the target component (a {@link Region}) to be resized and dragged
 * within its parent container ({@link Pane}) using mouse events.
 */
public class DragResizeBean {

    private static final double RESIZE_MARGIN = 10;

    /**
     * Applies drag and resize functionality to the target region within the given parent container.
     *
     * @param target the region (UI element) to be dragged and resized
     * @param parent the parent pane that holds the target region
     * @param name the name associated with the target region (e.g., for identifying elements in the class diagram)
     */
    public static void apply(Region target, Pane parent, String name) {
        enableDragAndResize(target, parent,name);
    }

    /**
     * Enables drag and resize functionality on the target region.
     *
     * @param target the region (UI element) to be dragged and resized
     * @param parent the parent pane that holds the target region
     * @param name the name associated with the target region
     */
    private static void enableDragAndResize(Region target, Pane parent,String name) {
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

                double newX = Math.max(0, dragData[4] + deltaX);
                double newY = Math.max(0, dragData[5] + deltaY);

                // Extend canvas only for right and bottom
                if (newX + target.getWidth() > parent.getWidth()) {
                    parent.setPrefWidth(newX + target.getWidth());
                }
                if (newY + target.getHeight() > parent.getHeight()) {
                    parent.setPrefHeight(newY + target.getHeight());
                }

                target.setLayoutX(newX);
                target.setLayoutY(newY);

                // Update position in MainFrame
                MainFrame.getClassDiagramCanvasPanel().updatePosition(name, newX, newY);
            }
        });

        target.setOnMouseReleased(event -> {
            target.setCursor(Cursor.DEFAULT);
            resizing[0] = false;

        });
    }

    /**
     * Updates the cursor based on the mouse position relative to the target region.
     * The cursor changes when the mouse hovers over a resize edge of the target region.
     *
     * @param target the region (UI element) being dragged or resized
     * @param event the mouse event containing the current mouse position
     */
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

    /**
     * Handles resizing of the target region based on the mouse drag event and the cursor state.
     *
     * @param target the region (UI element) to be resized
     * @param event the mouse event containing the current mouse position
     * @param parent the parent pane containing the target region
     * @param dragData an array containing previous drag/resize data for calculating the new size and position
     */
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

            // Extend canvas only for right and bottom
            if (newLayoutX + newWidth > parent.getWidth()) {
                parent.setPrefWidth(newLayoutX + newWidth);
            }
            if (newLayoutY + newHeight > parent.getHeight()) {
                parent.setPrefHeight(newLayoutY + newHeight);
            }
        } else if (cursor == Cursor.E_RESIZE) {
            newWidth = Math.min(Math.max(50, dragData[2] + deltaX), parent.getWidth() - newLayoutX);
            if (newLayoutX + newWidth > parent.getWidth()) {
                parent.setPrefWidth(newLayoutX + newWidth);
            }
        } else if (cursor == Cursor.S_RESIZE) {
            newHeight = Math.min(Math.max(50, dragData[3] + deltaY), parent.getHeight() - newLayoutY);
            if (newLayoutY + newHeight > parent.getHeight()) {
                parent.setPrefHeight(newLayoutY + newHeight);
            }
        }

        target.setPrefSize(newWidth, newHeight);
        target.setLayoutX(newLayoutX);
        target.setLayoutY(newLayoutY);

        // Update ClassPanel position if applicable
        if (target instanceof StackPane && ((StackPane) target).getChildren().get(1) instanceof ClassPanel) {
            ClassPanel classPanel = (ClassPanel) ((StackPane) target).getChildren().get(1);
            classPanel.x = newLayoutX;
            classPanel.y = newLayoutY;
            System.out.println("Updated after resize: x=" + classPanel.x + ", y=" + classPanel.y);
        }
    }




}
