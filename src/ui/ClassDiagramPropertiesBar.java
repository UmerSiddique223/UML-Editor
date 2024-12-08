package ui;

import core.class_diagram.ClassDiagramCanvasPanel;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import core.class_diagram.CanvasExporter;


/**
 * A UI component that displays the properties bar for a class diagram.
 * It includes sections for the project name, list of classes in the diagram,
 * and options to export or save the diagram.
 */
public class ClassDiagramPropertiesBar extends VBox {

    /** The ListView displaying the names of classes in the diagram. */
    private final ListView<String> classListView;

    /** The label displaying the project name. */
    private final Label projectNameLabel;

    /**
     * Constructs a ClassDiagramPropertiesBar with the specified project name
     * and class diagram canvas panel.
     *
     * @param projectName the name of the project to display in the properties bar.
     * @param classDiagramCanvasPanel the class diagram canvas panel containing the diagram data.
     */
    public ClassDiagramPropertiesBar(String projectName, ClassDiagramCanvasPanel classDiagramCanvasPanel) {
        setSpacing(10);
        setStyle("-fx-background-color: #e8e8e8; -fx-padding: 10px;");

        // Expandable/Collapsible Toggle
        TitledPane titledPane = new TitledPane();
        titledPane.setText("Properties");
        titledPane.setExpanded(true);

        // Main content of the properties bar
        VBox contentBox = new VBox(10);
        contentBox.setStyle("-fx-padding: 10px;");

        // Project Name Section
        projectNameLabel = new Label("Project: " + projectName);
        projectNameLabel.setStyle("-fx-font-weight: bold;");
        contentBox.getChildren().add(projectNameLabel);

        // Classes Section
        Label classesLabel = new Label("Classes:");
        classListView = new ListView<>();
        classListView.setPrefHeight(200);

        // Add classes dynamically from the diagram panel
        classDiagramCanvasPanel.getDiagram().getClasses().forEach(classPanel -> classListView.getItems().add(classPanel.getClassName()));

        classDiagramCanvasPanel.setOnClassAdded(newClass -> classListView.getItems().add(newClass.getClassName()));
        classDiagramCanvasPanel.setOnClassRemoved(removedClass -> classListView.getItems().remove(removedClass.getClassName()));
        classDiagramCanvasPanel.setOnClassRename((renamedClass, oldName) -> {
            int index = classListView.getItems().indexOf(oldName); // Find using the old name
            if (index != -1) {
                classListView.getItems().set(index, renamedClass.getClassName()); // Update with new name
            }
        });

        contentBox.getChildren().addAll(classesLabel, classListView);
        titledPane.setContent(contentBox);

        VBox ExportBox = new VBox(10);
        ExportBox.setStyle("-fx-padding: 10px;");
        Label exportLabel = new Label("Export:");
        Button exportToPNGButton = new Button("Export to PNG");
        Button exportToJPGButton = new Button("Export to JPG");
        Button saveAsXMLButton = new Button("Save as XML");

        exportToPNGButton.setOnAction(event -> CanvasExporter.exportToImage(classDiagramCanvasPanel, "png"));
        exportToJPGButton.setOnAction(event -> CanvasExporter.exportToImage(classDiagramCanvasPanel, "jpg"));
        saveAsXMLButton.setOnAction(event -> System.out.println("Saving as XML..."));

        ExportBox.getChildren().addAll(exportLabel, exportToPNGButton, exportToJPGButton, saveAsXMLButton);

        TitledPane exportTiltedPane = new TitledPane();
        exportTiltedPane.setText("Export");
        exportTiltedPane.setExpanded(true);
        exportTiltedPane.setContent(ExportBox);

        // Add the titled pane to the VBox
        getChildren().add(titledPane);
        getChildren().add(exportTiltedPane);
    }

    /**
     * Updates the project name dynamically in the properties bar.
     *
     * @param newProjectName the new project name to display.
     */
    public void updateProjectName(String newProjectName) {
        projectNameLabel.setText("Project: " + newProjectName);
    }
}
