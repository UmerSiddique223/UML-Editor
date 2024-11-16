package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import core.ClassShape;

public class CanvasPanel extends JPanel {
    private final ArrayList<ClassShape> shapes = new ArrayList<>();
    private ClassShape currentShape;

    public CanvasPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e);
                }
            }
        });


    }
    private void showContextMenu(MouseEvent e) {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem addClassDiagram = new JMenuItem("Add Class Diagram");
        addClassDiagram.addActionListener(ev -> {
            String name = JOptionPane.showInputDialog("Enter Class Diagram Name:");
            if (name != null && !name.isEmpty()) {
                addClassDiagramToCanvas(name, e.getPoint());
            }
        });

        contextMenu.add(addClassDiagram);
        contextMenu.show(this, e.getX(), e.getY());
    }

    private void addClassDiagramToCanvas(String name, Point location) {
        ClassDiagramPanel classPanel = new ClassDiagramPanel(name);
        classPanel.setBounds(location.x, location.y, 200, 150); // Default size
        add(classPanel);
        revalidate();
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (ClassShape shape : shapes) {
            shape.draw(g);
        }
    }
    public void addClassDiagram(String className) {
        ClassDiagramPanel classPanel = new ClassDiagramPanel(className);
        classPanel.setBounds(50, 50, 200, 150); // Default position and size
        add(classPanel);
        revalidate();
        repaint();
    }
}
