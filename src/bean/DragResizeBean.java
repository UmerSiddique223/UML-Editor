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
import java.util.Objects;

public class DragResizeBean {

    private static final double RESIZE_MARGIN = 10;

    public static void apply(Region target, Pane parent, String name) {
        enableDragAndResize(target, parent,name);
    }

    private static void enableDragAndResize(Region target, Pane parent,String name) {
        final double[] dragData = new double[6];
        final boolean[] resizing = {false};

        target.setOnMouseMoved(event -> {
            if (Objects.equals(MainFrame.getClassDiagramCanvasPanel().getDrawingMode(), "")){

                updateCursor(target, event);}
            else {
                event.consume();

            }
        });

        target.setOnMousePressed(event -> {
            if (Objects.equals(MainFrame.getClassDiagramCanvasPanel().getDrawingMode(), "")){

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
        }}
            else {
                event.consume();
            }
        });

        target.setOnMouseDragged(event -> {

            if (Objects.equals(MainFrame.getClassDiagramCanvasPanel().getDrawingMode(), "")){

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
            }}
            else {
                event.consume();
            }
        });

        target.setOnMouseReleased(event -> {
            if (MainFrame.getClassDiagramCanvasPanel().getDrawingMode()=="") {

                target.setCursor(Cursor.DEFAULT);
                resizing[0] = false;
            }
            else {
                event.consume();
            }
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
