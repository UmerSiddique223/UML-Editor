package ui;

import core.class_diagram.*;
import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class MainFrame extends Application {

    private static BorderPane rootPane; // Main container
    private static StackPane cardPane; // For switching between views
    private VBox homePanel;
    private static ClassDiagramCanvasPanel classDiagramCanvasPanel;
    private UsecaseToolbar usecaseToolbar; // Left-side toolbar
    private static ClassDiagramToolbar classDiagramToolbar; // Left-side toolbar

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("UML Editor");

        initializeComponents(primaryStage);

        Scene scene = new Scene(rootPane, 1100, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeComponents(Stage stage) {
        rootPane = new BorderPane();

        // Initialize MenuBar
        MenuBar menuBar = new MenuBarUI(stage);
        rootPane.setTop(menuBar);

        // Initialize Panels
        cardPane = new StackPane();
        initializeHomePanel();

        cardPane.getChildren().add(homePanel);
        rootPane.setCenter(cardPane);
    }

    private void initializeHomePanel() {
        homePanel = new VBox(20);
        homePanel.setAlignment(Pos.CENTER);
        homePanel.setStyle("-fx-background-color: white;");

        Label heading = new Label("UML Editor");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4682b4;");

        Button classBtn = new Button("Class Diagram");
        Button useCaseBtn = new Button("Use Case Diagram");

        // Set initial styles
        classBtn.setStyle(filledButtonStyle);
        useCaseBtn.setStyle(outlinedButtonStyle);

        classBtn.setOnAction(e -> {
            classBtn.setStyle(filledButtonStyle);
            useCaseBtn.setStyle(outlinedButtonStyle);
            updateHomePanel("Class Diagram");
        });

        useCaseBtn.setOnAction(e -> {
            useCaseBtn.setStyle(filledButtonStyle);
            classBtn.setStyle(outlinedButtonStyle);
            updateHomePanel("Use Case Diagram");
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(classBtn, useCaseBtn);

        homePanel.getChildren().addAll(heading, buttonBox);
    }


    private void updateHomePanel(String diagramType) {
        homePanel.getChildren().removeIf(node -> node instanceof VBox || node instanceof Label);

        // Adding Description text
        Label description = new Label();
        description.setStyle("-fx-font-size: 16px; -fx-text-fill: #4682b4; -fx-font-weight: bold;");
        if ("Class Diagram".equals(diagramType)) {
            description.setText("Create or Load a Class Diagram\nUse this tool to create class diagrams.");
        } else if ("Use Case Diagram".equals(diagramType)) {
            description.setText("Create or Load a Use Case Diagram\nUse this tool to create use case diagrams.");
        }

        // Main Container for all the elements
        VBox actionBox = new VBox(20);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(20));
        actionBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-padding: 10;");

        // Create Class diagram Section
        HBox createSection = new HBox(10);
        createSection.setAlignment(Pos.CENTER);

        TextField createTextField = new TextField();
        createTextField.setPromptText("Enter project name...");
        Button createButton = new Button("Create");
        styleButton(createButton);

        createButton.setOnAction(e -> {
            String projectName = createTextField.getText().trim();
            if (projectName.isEmpty()) {
                showAlert("Project name cannot be empty!");
            } else {
                if (diagramType.equals("Class Diagram")) {
                    showClassDiagram(projectName);
                } else if (diagramType.equals("Use Case Diagram")) {
                    showUseCaseDiagram(projectName);
                }
            }
        });

        createSection.getChildren().addAll(createTextField, createButton);

        // Load Section
        HBox loadSection = new HBox(10);
        loadSection.setAlignment(Pos.CENTER);

        TextField loadTextField = new TextField();
        loadTextField.setPromptText("Select project file...");
        Button explorerButton = new Button("..."); // Explorer icon
        Button loadButton = new Button("Load");
        styleButton(loadButton);

        explorerButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Project File");
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                loadTextField.setText(selectedFile.getAbsolutePath());
            }
        });

        loadButton.setOnAction(e -> {
            String filePath = loadTextField.getText().trim();
            if (filePath.isEmpty()) {
                showAlert("Please select a project file to load!");
            } else {
                showAlert("Loading " + diagramType + " project from: " + filePath);
                // Add logic to load project
            }
        });

        loadSection.getChildren().addAll(loadTextField, explorerButton, loadButton);

        // Add sections to actionBox
        actionBox.getChildren().addAll(
                new Label("Create New Project:"),
                createSection,
                new Label("Load Existing Project:"),
                loadSection
        );

        homePanel.getChildren().addAll(description, actionBox);
    }

    // Styles for buttons
    private final String filledButtonStyle =
            "-fx-font-size: 16px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: #4682b4; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 10 20 10 20; " +
                    "-fx-border-radius: 5px; " +
                    "-fx-background-radius: 5px;";

    private final String outlinedButtonStyle =
            "-fx-font-size: 16px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: transparent; " +
                    "-fx-border-color: #4682b4; " +
                    "-fx-text-fill: #4682b4; " +
                    "-fx-padding: 10 20 10 20; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 5px; " +
                    "-fx-background-radius: 5px;";


    private void styleButton(Button button) {
        button.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #4682b4; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 20 10 20; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #1e90ff; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 20 10 20; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #4682b4; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 20 10 20; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        ));
    }

    // Function to initiate class diagram canvas
    private void showClassDiagram(String name) {
        // Main canvas panel for Class Diagram:
        classDiagramCanvasPanel = new ClassDiagramCanvasPanel();
        classDiagramCanvasPanel.setStyle("-fx-background-color: lightgray;");
        classDiagramCanvasPanel.setPrefSize(2000, 2000); // Set a large preferred size for scrolling

        // Wrap the canvas panel in a ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(classDiagramCanvasPanel); // Set the canvas as content
        scrollPane.setPannable(false);                   // Allow panning
        scrollPane.setFitToWidth(false);                // Disable auto-fit for width
        scrollPane.setFitToHeight(false);               // Disable auto-fit for height

        // Create a new class diagram
        ClassDiagram classDiagram = new ClassDiagram(name);
        // Set the current diagram in the canvas
        classDiagramCanvasPanel.setCurrentDiagram(classDiagram);

        // Create the toolbar and associate it with the canvas
        classDiagramToolbar = new ClassDiagramToolbar(classDiagramCanvasPanel);
        rootPane.setLeft(classDiagramToolbar);

        // Replace the contents of cardPane with the scrollable canvas
        cardPane.getChildren().setAll(scrollPane);

        // Create the properties bar
        ClassDiagramPropertiesBar propertiesBar = new ClassDiagramPropertiesBar(name, classDiagramCanvasPanel);
        rootPane.setRight(propertiesBar);

        // Make the toolbar visible
        classDiagramToolbar.setVisible(true);

    }


    private void showUseCaseDiagram(String name) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Use Case Diagram");
        dialog.setHeaderText("Enter Use Case Diagram Name:");
        UseCaseDiagramPanel useCaseDiagramPanel = new UseCaseDiagramPanel(name);
        usecaseToolbar = new UsecaseToolbar(useCaseDiagramPanel);
        usecaseToolbar.loadToolsForDiagramType("UseCaseDiagram");
        rootPane.setLeft(usecaseToolbar);
        usecaseToolbar.setVisible(true);

        cardPane.getChildren().setAll(useCaseDiagramPanel);

    }

    public static ClassDiagramCanvasPanel getClassDiagramCanvasPanel() {
        return classDiagramCanvasPanel;
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static void loadDiagram(File file) throws Exception {
        classDiagramCanvasPanel = new ClassDiagramCanvasPanel();

        // Parse the XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);

        // Get the root element
        Element root = document.getDocumentElement();

        // Get the diagram name
        String diagramName = root.getElementsByTagName("Name").item(0).getTextContent();

        // Create a new ClassDiagram
        ClassDiagram classDiagram = new ClassDiagram(diagramName);
        System.out.println("Class Name: " + diagramName);

        // Load classes
        NodeList classNodes = root.getElementsByTagName("Class");
        for (int i = 0; i < classNodes.getLength(); i++) {
            Element classElement = (Element) classNodes.item(i);
            String className = classElement.getAttribute("name");

            boolean isInterface = "interface".equals(classElement.getAttribute("type"));
            double x = Double.parseDouble(classElement.getAttribute("x"));
            double y = Double.parseDouble(classElement.getAttribute("y"));


            ClassPanel classPanel = new ClassPanel(className, isInterface, x, y, classDiagramCanvasPanel);

            // Load attributes
            NodeList attributeNodes = ((Element) classElement.getElementsByTagName("Attributes").item(0)).getElementsByTagName("Attribute");
            for (int j = 0; j < attributeNodes.getLength(); j++) {
                Element attributeElement = (Element) attributeNodes.item(j);
                String attributeName = attributeElement.getAttribute("name");
                String attributeType = attributeElement.getAttribute("type");
                String attributeAccess = attributeElement.getAttribute("access");

                Attribute attribute = new Attribute(attributeName, attributeType, attributeAccess);
                classPanel.addAttribute(attribute);
            }

            // Load methods
            NodeList methodNodes = ((Element) classElement.getElementsByTagName("Methods").item(0)).getElementsByTagName("Method");
            for (int j = 0; j < methodNodes.getLength(); j++) {
                Element methodElement = (Element) methodNodes.item(j);
                String methodName = methodElement.getAttribute("name");
                String returnType = methodElement.getAttribute("returnType");
                String access = methodElement.getAttribute("access");

                ArrayList<String> parameters = new ArrayList<>();
                NodeList parameterNodes = methodElement.getElementsByTagName("Parameter");
                for (int k = 0; k < parameterNodes.getLength(); k++) {
                    parameters.add(parameterNodes.item(k).getTextContent());
                }

                Method method = new Method(methodName, returnType, parameters, access);
                classPanel.addMethod(method);
            }

            classDiagram.addClass(classPanel);
        }

        // Load relationships
        NodeList relationshipNodes = root.getElementsByTagName("Relationship");
        for (int i = 0; i < relationshipNodes.getLength(); i++) {
            Element relationshipElement = (Element) relationshipNodes.item(i);
            String startClass = relationshipElement.getAttribute("startClass");
            String endClass = relationshipElement.getAttribute("endClass");
            String type = relationshipElement.getAttribute("type");

            Relationship relationship = new Relationship(startClass, endClass, type);
            classDiagram.addRelationship(relationship);
        }


        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(classDiagramCanvasPanel); // Set the canvas as content
        scrollPane.setPannable(false);                   // Allow panning
        scrollPane.setFitToWidth(false);                // Disable auto-fit for width
        scrollPane.setFitToHeight(false);
        // Initialize the canvas panel with the loaded diagram
        ClassDiagram c_diagram = new ClassDiagram(diagramName);
        classDiagramCanvasPanel.setCurrentDiagram(c_diagram);

        // Update the UI
        classDiagramToolbar = new ClassDiagramToolbar(classDiagramCanvasPanel);
        rootPane.setLeft(classDiagramToolbar);
        cardPane.getChildren().setAll(classDiagramCanvasPanel);

        for (ClassPanel c : classDiagram.getClasses()) {
            classDiagramCanvasPanel.addClassToCanvas(new ClassPanel(c.getClassName(),c.isInterface(), c.getX(), c.getY(), classDiagramCanvasPanel), c.getX(), c.getY());


        }
        Platform.runLater(() -> {
            for (Relationship r : classDiagram.getRelationships()) {
                classDiagramCanvasPanel.setRelationship(r.type, r.startClass, r.endClass);
            }
        });

    }
}
