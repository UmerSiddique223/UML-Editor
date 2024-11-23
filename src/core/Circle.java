package core;

import bean.DragResizeBean;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import ui.AbstractDiagramPanel;

public class Circle {
    public static class CanvasPanel extends Pane {
        private AbstractDiagramPanel startDiagram;
        private AbstractDiagramPanel endDiagram;
        private String drawingMode = "";

        private Line tempLine;

        public CanvasPanel() {
            setStyle("-fx-background-color: white;");
            setPrefSize(800, 600);

            setOnMouseClicked(this::handleMouseClick);
            setOnMouseMoved(this::handleMouseMove); // Add dynamic line updates.
        }

        private void showContextMenu(double screenX, double screenY, double x, double y) {
            ContextMenu contextMenu = new ContextMenu();

            MenuItem addClassDiagram = new MenuItem("Add Class Diagram");
            addClassDiagram.setOnAction(ev -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add Class Diagram");
                dialog.setHeaderText("Enter Class Diagram Name:");
                dialog.showAndWait().ifPresent(name -> addDiagramToCanvas(new DragResizeBean.ClassDiagramPanel(name), x, y));
            });

            contextMenu.getItems().add(addClassDiagram);
            contextMenu.show(this, screenX, screenY);
        }

        public void addDiagramToCanvas(AbstractDiagramPanel diagramPanel, double x, double y) {
            javafx.scene.shape.Rectangle border = new Rectangle(200, 150);
            border.setFill(Color.TRANSPARENT);
            border.setStroke(Color.BLACK);

            StackPane container = new StackPane(border, diagramPanel);
            container.setLayoutX(x);
            container.setLayoutY(y);

            DragResizeBean.apply(container, this);
            getChildren().add(container);
        }
        public void setDrawingMode(String mode) {
            this.drawingMode = mode;
            startDiagram = null;
            endDiagram = null;
            clearTempLine();
        }

        private void handleMouseClick(MouseEvent event) {
            if (drawingMode.isEmpty()) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    showContextMenu(event.getScreenX(), event.getScreenY(), event.getX(), event.getY());
                }
                return;
            }

            AbstractDiagramPanel clickedDiagram = findDiagramAt(event);

            if (clickedDiagram == null) {
                return;
            }

            if (startDiagram == null) {
                startDiagram = clickedDiagram;
                System.out.println("Start diagram selected.");
                createTempLine(startDiagram.getLayoutX() + startDiagram.getWidth() / 2,
                        startDiagram.getLayoutY() + startDiagram.getHeight() / 2);
            } else if (clickedDiagram != startDiagram) {
                endDiagram = clickedDiagram;
                System.out.println("End diagram selected.");
                finalizeRelationship();
            }

        }


        private void handleMouseMove(MouseEvent event) {
            if (tempLine != null) {
                updateTempLine(event.getX(), event.getY());
            }
        }

        private void createTempLine(double x, double y) {
            tempLine = new Line();
            tempLine.setStartX(x);
            tempLine.setStartY(y);
            tempLine.setEndX(x);
            tempLine.setEndY(y);
            tempLine.setStroke(Color.GRAY);
            tempLine.getStrokeDashArray().addAll(5.0, 5.0);

            getChildren().add(tempLine);
        }

        private void updateTempLine(double x, double y) {
            if (tempLine != null) {
                tempLine.setEndX(x);
                tempLine.setEndY(y);
            }
        }

        private void clearTempLine() {
            if (tempLine != null) {
                getChildren().remove(tempLine);
                tempLine = null;
            }
        }

        private void finalizeRelationship() {
            if (startDiagram == null || endDiagram == null || drawingMode.isEmpty()) {
                System.out.println("Relationship could not be finalized: missing start or end diagram.");
                return;
            }

            double[] startBorder = calculateBorderPoint(startDiagram, endDiagram);
            double[] endBorder = calculateBorderPoint(endDiagram, startDiagram);

            System.out.println("Start border: (" + startBorder[0] + ", " + startBorder[1] + ")");
            System.out.println("End border: (" + endBorder[0] + ", " + endBorder[1] + ")");

            Line relationshipLine = new Line(startBorder[0], startBorder[1], endBorder[0], endBorder[1]);
            relationshipLine.setStroke(Color.BLACK);

            getChildren().add(relationshipLine);
            System.out.println("Relationship line added.");

            clearTempLine();
            resetDrawingState();
        }

        private void resetDrawingState() {
            startDiagram = null;
            endDiagram = null;
            drawingMode = "";
        }

        private AbstractDiagramPanel findDiagramAt(MouseEvent event) {
            Node node = (Node) event.getTarget();
            while (node != null && !(node instanceof AbstractDiagramPanel)) {
                node = node.getParent();
            }
            if (node == null) {
                System.out.println("No diagram found at clicked position.");
            } else {

                System.out.println("Found diagram: " + ((AbstractDiagramPanel) node).getClass().getName());
            }
            return (AbstractDiagramPanel) node;
        }


        private double[] calculateBorderPoint(AbstractDiagramPanel from, AbstractDiagramPanel to) {
            double fromCenterX = from.getLayoutX() + from.getWidth() / 2;
            double fromCenterY = from.getLayoutY() + from.getHeight() / 2;
            double toCenterX = to.getLayoutX() + to.getWidth() / 2;
            double toCenterY = to.getLayoutY() + to.getHeight() / 2;

            double dx = toCenterX - fromCenterX;
            double dy = toCenterY - fromCenterY;

            double absDx = Math.abs(dx);
            double absDy = Math.abs(dy);

            double halfWidth = from.getWidth() / 2;
            double halfHeight = from.getHeight() / 2;

            if (absDx / halfWidth > absDy / halfHeight) {
                return new double[]{
                        fromCenterX + Math.signum(dx) * halfWidth,
                        fromCenterY + dy / absDx * halfWidth
                };
            } else {
                return new double[]{
                        fromCenterX + dx / absDy * halfHeight,
                        fromCenterY + Math.signum(dy) * halfHeight
                };
            }
        }
    }
}
