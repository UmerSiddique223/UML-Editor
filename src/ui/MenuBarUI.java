package ui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MenuBarUI extends JMenuBar {

    public MenuBarUI(JFrame parentFrame) {
        JMenu fileMenu = new JMenu("File");
        JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem exitApp = new JMenuItem("Exit");
        exitApp.addActionListener(e -> System.exit(0));

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.addSeparator();
        fileMenu.add(exitApp);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem undo = new JMenuItem("Undo");
        JMenuItem redo = new JMenuItem("Redo");
        JMenuItem delete = new JMenuItem("Delete");

        editMenu.add(undo);
        editMenu.add(redo);
        editMenu.add(delete);

        JMenu viewMenu = new JMenu("View");
        JMenuItem zoomIn = new JMenuItem("Zoom In");
        JMenuItem zoomOut = new JMenuItem("Zoom Out");
        JMenuItem switchDiagram = new JMenuItem("Switch Diagram");

        viewMenu.add(zoomIn);
        viewMenu.add(zoomOut);
        viewMenu.addSeparator();
        viewMenu.add(switchDiagram);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(parentFrame, "UML Editor v1.0"));

        helpMenu.add(about);

        this.add(fileMenu);
        this.add(editMenu);
        this.add(viewMenu);
        this.add(helpMenu);
    }
}
