package bean;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.input.MouseEvent;

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


}
