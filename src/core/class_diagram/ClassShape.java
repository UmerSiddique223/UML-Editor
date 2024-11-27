package core.class_diagram;

import java.awt.*;

public class ClassShape {
    private int x, y, width, height;
    private static final int MIN_WIDTH = 100;
    private static final int MIN_HEIGHT = 50;

    public ClassShape(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = MIN_WIDTH;
        this.height = MIN_HEIGHT;
    }

    public void updateSize(int newX, int newY) {
        this.width = Math.max(MIN_WIDTH, newX - x);
        this.height = Math.max(MIN_HEIGHT, newY - y);
    }

    public void draw(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x, y, width, height);

        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        // Header line for the class name
        g.drawLine(x, y + 20, x + width, y + 20);
        g.drawString("Class Name", x + 10, y + 15);
    }
}
