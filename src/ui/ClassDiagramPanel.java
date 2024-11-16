package ui;

import bean.DragListener;
import bean.DragResizeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ClassDiagramPanel extends JPanel {

    private final ArrayList<String> attributes = new ArrayList<>();
    private final ArrayList<String> methods = new ArrayList<>();
    private final JLabel titleLabel;
    private final JTextArea attributesArea;
    private final JTextArea methodsArea;

    public ClassDiagramPanel(String className) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(200, 150));

        // Title
        titleLabel = new JLabel(className, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(titleLabel, BorderLayout.NORTH);

        // Attributes Section
        attributesArea = new JTextArea("Attributes...");
        attributesArea.setEditable(false);
        add(attributesArea, BorderLayout.CENTER);

        // Methods Section
        methodsArea = new JTextArea("Methods...");
        methodsArea.setEditable(false);
        add(methodsArea, BorderLayout.SOUTH);

        // Context Menu
        addMouseListener(new ContextMenuListener());

        // Dragging
        DragResizeListener dragResizeListener = new DragResizeListener(this);
        addMouseMotionListener(dragResizeListener);
        addMouseListener(dragResizeListener);
        DragListener DragListener = new DragListener(this);
        addMouseMotionListener(DragListener);
        addMouseListener(DragListener);

    }

    private class ContextMenuListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                showContextMenu(e);
            }
        }

        private void showContextMenu(MouseEvent e) {
            JPopupMenu contextMenu = new JPopupMenu();

            JMenuItem addAttribute = new JMenuItem("Add Attribute");
            addAttribute.addActionListener(ev -> {
                String attribute = JOptionPane.showInputDialog("Enter Attribute:");
                if (attribute != null) {
                    attributes.add(attribute);
                    updateAttributes();
                }
            });

            JMenuItem addMethod = new JMenuItem("Add Method");
            addMethod.addActionListener(ev -> {
                String method = JOptionPane.showInputDialog("Enter Method:");
                if (method != null) {
                    methods.add(method);
                    updateMethods();
                }
            });

            JMenuItem delete = new JMenuItem("Delete Class Diagram");
            delete.addActionListener(ev -> getParent().remove(ClassDiagramPanel.this));

            contextMenu.add(addAttribute);
            contextMenu.add(addMethod);
            contextMenu.add(delete);
            contextMenu.show(ClassDiagramPanel.this, e.getX(), e.getY());
        }
    }

    private void updateAttributes() {
        attributesArea.setText(String.join("\n", attributes));
    }

    private void updateMethods() {
        methodsArea.setText(String.join("\n", methods));
    }
}
