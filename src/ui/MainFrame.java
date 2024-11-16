package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JLabel heading;
    private JButton Classbtn;
    private JButton UseCasebtn;

    public MainFrame() {
        setTitle("UML Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        initializeComponents();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(heading, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        mainPanel.add(Classbtn, gbc);

        gbc.gridx = 1;
        mainPanel.add(UseCasebtn, gbc);

        add(mainPanel);
    }

    private void initializeComponents() {
        heading = new JLabel("UML Editor", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setForeground(new Color(70, 130, 180));

        Classbtn = new JButton("Class Diagram");
        UseCasebtn = new JButton("Use Case Diagram");

        styleButton(Classbtn);
        styleButton(UseCasebtn);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }


}
