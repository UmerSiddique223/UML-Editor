package bean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DragListener extends MouseAdapter {
    private final JComponent targetComponent;
    private Point initialMouseLocation;
    private Point initialComponentLocation;

    public DragListener(JComponent targetComponent) {
        this.targetComponent = targetComponent;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Record initial positions
        initialMouseLocation = SwingUtilities.convertPoint(targetComponent, e.getPoint(), targetComponent.getParent());
        initialComponentLocation = targetComponent.getLocation();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (initialMouseLocation == null || initialComponentLocation == null) return;

        Point currentMouseLocation = SwingUtilities.convertPoint(targetComponent, e.getPoint(), targetComponent.getParent());

        int deltaX = currentMouseLocation.x - initialMouseLocation.x;
        int deltaY = currentMouseLocation.y - initialMouseLocation.y;

        int newX = initialComponentLocation.x + deltaX;
        int newY = initialComponentLocation.y + deltaY;

        Container parent = targetComponent.getParent();
        if (parent != null) {
            Rectangle parentBounds = parent.getBounds();
            Insets insets = parent.getInsets();

            int borderThickness = targetComponent.getBorder() != null ? targetComponent.getBorder().getBorderInsets(targetComponent).top : 0;

            newX = Math.max(insets.left + borderThickness, Math.min(newX, parentBounds.width - insets.right - targetComponent.getWidth() + borderThickness));
            newY = Math.max(insets.top + borderThickness, Math.min(newY, parentBounds.height - insets.bottom - targetComponent.getHeight() + borderThickness));

            for (Component sibling : parent.getComponents()) {
                if (sibling == targetComponent) continue;
                if (sibling.getBounds().intersects(new Rectangle(newX, newY, targetComponent.getWidth(), targetComponent.getHeight()))) {
                    return;
                }
            }

            targetComponent.setLocation(newX, newY);
        }
    }
}