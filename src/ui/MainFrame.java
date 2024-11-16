package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel cardPanel; // Container for switching between different views
    private JPanel homePanel;
    private CanvasPanel classDiagramPanel;

    public MainFrame() {
        setTitle("UML Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        setJMenuBar(new MenuBarUI(this)); // Menu bar remains constant

        initializeComponents();
        initializePanels();

        add(cardPanel);
    }

    private void initializeComponents() {
        // Initialize Home Panel Components
        JLabel heading = new JLabel("UML Editor", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setForeground(new Color(70, 130, 180));

        JButton classBtn = new JButton("Class Diagram");
        JButton useCaseBtn = new JButton("Use Case Diagram");

        styleButton(classBtn);
        styleButton(useCaseBtn);

        classBtn.addActionListener(e -> showClassDiagram());
        useCaseBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Use Case Diagram is not implemented yet."));

        homePanel = new JPanel(new GridBagLayout());
        homePanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        homePanel.add(heading, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        homePanel.add(classBtn, gbc);

        gbc.gridx = 1;
        homePanel.add(useCaseBtn, gbc);
    }

    private void initializePanels() {
        cardPanel = new JPanel(new CardLayout());

        // Add Home Panel
        cardPanel.add(homePanel, "Home");

        // Initialize Class Diagram Panel
        classDiagramPanel = new CanvasPanel();
        JPanel classPanelContainer = new JPanel(new BorderLayout());
        classPanelContainer.add(classDiagramPanel, BorderLayout.CENTER);
        classPanelContainer.setBackground(Color.LIGHT_GRAY);

        // Add Class Diagram Panel to CardLayout
        cardPanel.add(classPanelContainer, "ClassDiagram");
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void showClassDiagram() {
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, "ClassDiagram");

        // Add new diagram
        String name = JOptionPane.showInputDialog("Enter Class Diagram Name:");
        if (name != null) {
            classDiagramPanel.addClassDiagram(name);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
