package ui;

import bean.CanvasExporterBean;
import core.class_diagram.ClassDiagramCanvasPanel;
import core.class_diagram.Relationship;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class ClassDiagramPropertiesBar extends VBox {

    private final TreeView<String> classHierarchyTreeView;
    private final TreeView<String> relationshipsTreeView;
    private final Label projectNameLabel;

    public ClassDiagramPropertiesBar(String projectName, ClassDiagramCanvasPanel classDiagramCanvasPanel) {
        setSpacing(10);
        setStyle("-fx-background-color: #e8e8e8; -fx-padding: 10px;");

        // Expandable/Collapsible Toggle
        TitledPane titledPane = new TitledPane();
        titledPane.setText("Properties");
        titledPane.setExpanded(true);
        setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #dcdcdc; -fx-border-width: 0 0 0 1px;");


        // Main content of the properties bar
        VBox contentBox = new VBox(10);
        contentBox.setStyle("-fx-padding: 10px;");

        // Project Name Section
        projectNameLabel = new Label("Project: " + projectName);
        projectNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        contentBox.getChildren().add(projectNameLabel);

        // Classes Section (Hierarchy)
        Label classesLabel = new Label("Classes:");
        classHierarchyTreeView = new TreeView<>();
        classHierarchyTreeView.setPrefHeight(200);

        populateClassHierarchy(classDiagramCanvasPanel);

        classDiagramCanvasPanel.setOnClassAdded(newClass -> populateClassHierarchy(classDiagramCanvasPanel));
        classDiagramCanvasPanel.setOnClassRemoved(removedClass -> populateClassHierarchy(classDiagramCanvasPanel));
        classDiagramCanvasPanel.setOnClassRename((renamedClass, oldName) -> populateClassHierarchy(classDiagramCanvasPanel));

        contentBox.getChildren().addAll(classesLabel, classHierarchyTreeView);

        // Relationships Section
        Label relationshipsLabel = new Label("Relationships:");
        relationshipsTreeView = new TreeView<>();
        relationshipsTreeView.setPrefHeight(200);

        populateRelationships(classDiagramCanvasPanel);

        contentBox.getChildren().addAll(relationshipsLabel, relationshipsTreeView);
        titledPane.setContent(contentBox);

        // Export Section
        VBox exportBox = new VBox(10);
        exportBox.setStyle("-fx-padding: 10px;");
        Label exportLabel = new Label("Export:");
        Button exportToPNGButton = createStyledButton("Export to PNG");
        Button exportToJPGButton = createStyledButton("Export to JPG");
        Button saveAsJavaCodeButton = createStyledButton("Save as Java Code");
        Button saveAsXMLButton = createStyledButton("Save in Projects");

        saveAsJavaCodeButton.setOnAction(event -> {
            try {
                String outputDirectory = chooseOutputDirectory((Stage) this.getScene().getWindow());
                CanvasExporterBean.exportToJavaCode(classDiagramCanvasPanel.getDiagram(), outputDirectory);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        exportBox.getChildren().add(saveAsJavaCodeButton);



        exportToPNGButton.setOnAction(event -> CanvasExporterBean.exportToImage(classDiagramCanvasPanel, "png"));
        exportToJPGButton.setOnAction(event -> CanvasExporterBean.exportToImage(classDiagramCanvasPanel, "jpg"));
        saveAsXMLButton.setOnAction(event -> {
            try {
                classDiagramCanvasPanel.saveDiagram();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        exportBox.getChildren().addAll(exportLabel, exportToPNGButton, exportToJPGButton, saveAsXMLButton);

        TitledPane exportTitledPane = new TitledPane();
        exportTitledPane.setText("Export");
        exportTitledPane.setExpanded(true);
        exportTitledPane.setContent(exportBox);

        // Add the titled pane to the VBox
        getChildren().add(titledPane);
        getChildren().add(exportTitledPane);
    }

    private void populateClassHierarchy(ClassDiagramCanvasPanel classDiagramCanvasPanel) {
        TreeItem<String> root = new TreeItem<>("Classes");
        root.setExpanded(true);

        for (var classPanel : classDiagramCanvasPanel.getDiagram().getClasses()) {
            TreeItem<String> classItem = new TreeItem<>(classPanel.getClassName());
            classItem.getChildren().add(new TreeItem<>("Attributes:"));
            for (var attribute : classPanel.getAttributes()) {
                classItem.getChildren().add(new TreeItem<>("  - " + attribute.getName() + " : " + attribute.getType()));
            }
            classItem.getChildren().add(new TreeItem<>("Methods:"));
            for (var method : classPanel.getMethods()) {
                classItem.getChildren().add(new TreeItem<>("  - " + method.getAccess()));
            }
            root.getChildren().add(classItem);
        }

        classHierarchyTreeView.setRoot(root);
    }

    private void populateRelationships(ClassDiagramCanvasPanel classDiagramCanvasPanel) {
        TreeItem<String> root = new TreeItem<>("Relationships");
        root.setExpanded(true);
        System.out.println(classDiagramCanvasPanel.getDiagram().getRelationships()+"  lll");
        for (Relationship relationship : classDiagramCanvasPanel.getDiagram().getRelationships()) {
            System.out.println(relationship.getStartClass()+relationship.getEndClass()+ relationship.getType());
            String relationshipText = relationship.getStartClass() + " -[" + relationship.getType() + "]-> " + relationship.getEndClass();
            root.getChildren().add(new TreeItem<>(relationshipText));
        }

        relationshipsTreeView.setRoot(root);
    }

    public void updateClassHierarchy() {
        TreeItem<String> root = new TreeItem<>("Classes");
        root.setExpanded(true);

        for (var classPanel : MainFrame.getClassDiagramCanvasPanel().getDiagram().getClasses()) {
            TreeItem<String> classItem = new TreeItem<>(classPanel.getClassName());
            classItem.getChildren().add(new TreeItem<>("Attributes:"));
            for (var attribute : classPanel.getAttributes()) {
                classItem.getChildren().add(new TreeItem<>("  - " + attribute.getName() + " : " + attribute.getType()));
            }
            classItem.getChildren().add(new TreeItem<>("Methods:"));
            for (var method : classPanel.getMethods()) {
                classItem.getChildren().add(new TreeItem<>("  - " + method.getAccess()));
            }
            root.getChildren().add(classItem);
        }

        classHierarchyTreeView.setRoot(root);
    }
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 5 10; " +
                        "-fx-font-size: 12px;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 5 10; " +
                        "-fx-font-size: 12px;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 5 10; " +
                        "-fx-font-size: 12px;"
        ));
        return button;
    }
    public void updateRelationships() {
        TreeItem<String> root = new TreeItem<>("Relationships");
        root.setExpanded(true);

        for (Relationship relationship : MainFrame.getClassDiagramCanvasPanel().getDiagram().getRelationships()) {
            String relationshipText = relationship.getStartClass() + " -[" + relationship.getType() + "]-> " + relationship.getEndClass();
            root.getChildren().add(new TreeItem<>(relationshipText));
        }

        relationshipsTreeView.setRoot(root);
    }

    /**
     * Updates all sections of the properties bar.
     */
    public void refresh() {
        updateClassHierarchy();
        updateRelationships();
    }

    public static String chooseOutputDirectory(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Output Directory");

        // Show directory chooser
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            return selectedDirectory.getAbsolutePath();
        }
        return null; // User cancelled
    }

    // Update the project name dynamically if needed
    public void updateProjectName(String newProjectName) {
        projectNameLabel.setText("Project: " + newProjectName);
    }
}
