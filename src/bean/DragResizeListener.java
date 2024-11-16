package bean;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class DragResizeListener extends MouseAdapter {
    private final JComponent component;
    private Point initialClick;
    private int initialWidth;
    private int initialHeight;
    private int cursorType;

    // Resize direction constants
    private static final int NONE = 0;
    private static final int NW_RESIZE = 1;
    private static final int NE_RESIZE = 2;
    private static final int SW_RESIZE = 3;
    private static final int SE_RESIZE = 4;
    private static final int W_RESIZE = 5;
    private static final int E_RESIZE = 6;
    private static final int N_RESIZE = 7;
    private static final int S_RESIZE = 8;

    private static final int MIN_WIDTH = 50;
    private static final int MIN_HEIGHT = 50;

    public DragResizeListener(JComponent component) {
        this.component = component;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        initialClick = e.getPoint();
        initialWidth = component.getWidth();
        initialHeight = component.getHeight();
        cursorType = getResizeCursor(e.getPoint());
        System.out.println("Cursor Type: " + cursorType);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (cursorType == NONE) return;

        int deltaX = e.getX() - initialClick.x;
        int deltaY = e.getY() - initialClick.y;

        int newWidth = initialWidth;
        int newHeight = initialHeight;
        int newX = component.getX();
        int newY = component.getY();

        switch (cursorType) {
            case NW_RESIZE -> {
                newWidth = Math.max(initialWidth - deltaX, MIN_WIDTH);
                newHeight = Math.max(initialHeight - deltaY, MIN_HEIGHT);
                newX += deltaX;
                newY += deltaY;
            }
            case NE_RESIZE -> {
                newWidth = Math.max(initialWidth + deltaX, MIN_WIDTH);
                newHeight = Math.max(initialHeight - deltaY, MIN_HEIGHT);
                newY += deltaY;
            }
            case SW_RESIZE -> {
                newWidth = Math.max(initialWidth - deltaX, MIN_WIDTH);
                newHeight = Math.max(initialHeight + deltaY, MIN_HEIGHT);
                newX += deltaX;
            }
            case SE_RESIZE -> {
                newWidth = Math.max(initialWidth + deltaX, MIN_WIDTH);
                newHeight = Math.max(initialHeight + deltaY, MIN_HEIGHT);
            }
            case W_RESIZE -> {
                newWidth = Math.max(initialWidth - deltaX, MIN_WIDTH);
                newX += deltaX;
            }
            case E_RESIZE -> newWidth = Math.max(initialWidth + deltaX, MIN_WIDTH);
            case N_RESIZE -> {
                newHeight = Math.max(initialHeight - deltaY, MIN_HEIGHT);
                newY += deltaY;
            }
            case S_RESIZE -> newHeight = Math.max(initialHeight + deltaY, MIN_HEIGHT);
        }

//        // Boundary check (ensure component stays within parent bounds)
//        Container parent = component.getParent();
//        if (parent != null) {
//            Rectangle bounds = parent.getBounds();
//            newX = Math.max(bounds.x, Math.min(newX, bounds.x + bounds.width - newWidth));
//            newY = Math.max(bounds.y, Math.min(newY, bounds.y + bounds.height - newHeight));
//        }

        component.setSize(newWidth, newHeight);
        component.setLocation(newX, newY);
        component.revalidate();
        component.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int cursor = getResizeCursor(e.getPoint());
        Cursor resizeCursor = switch (cursor) {
            case NW_RESIZE -> Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
            case NE_RESIZE -> Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
            case SW_RESIZE -> Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
            case SE_RESIZE -> Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
            case W_RESIZE -> Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
            case E_RESIZE -> Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
            case N_RESIZE -> Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
            case S_RESIZE -> Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
            default -> Cursor.getDefaultCursor();
        };

        component.setCursor(resizeCursor);
    }

    private int getResizeCursor(Point p) {
        int x = p.x;
        int y = p.y;
        int width = component.getWidth();
        int height = component.getHeight();
        int margin = 10;

        // Check corners
        if (x < margin && y < margin) return NW_RESIZE;
        if (x > width - margin && y < margin) return NE_RESIZE;
        if (x < margin && y > height - margin) return SW_RESIZE;
        if (x > width - margin && y > height - margin) return SE_RESIZE;

        // Check edges
        if (x < margin) return W_RESIZE;
        if (x > width - margin) return E_RESIZE;
        if (y < margin) return N_RESIZE;
        if (y > height - margin) return S_RESIZE;

        return NONE;
    }
}
