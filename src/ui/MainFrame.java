package ui;

import core.class_diagram.*;
import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
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

        styleButton(classBtn);
        styleButton(useCaseBtn);
        useCaseBtn.setOnAction(e -> showUseCaseDiagram());

        classBtn.setOnAction(e -> showClassDiagram());

        homePanel.getChildren().addAll(heading, classBtn, useCaseBtn);
    }

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

    private void showClassDiagram() {
        // Create the canvas panel
        classDiagramCanvasPanel = new ClassDiagramCanvasPanel();
        classDiagramCanvasPanel.setStyle("-fx-background-color: lightgray;");
        classDiagramCanvasPanel.setPrefSize(2000, 2000); // Set a large preferred size for scrolling

        // Wrap the canvas panel in a ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(classDiagramCanvasPanel); // Set the canvas as content
        scrollPane.setPannable(true);                   // Allow panning
        scrollPane.setFitToWidth(false);                // Disable auto-fit for width
        scrollPane.setFitToHeight(false);               // Disable auto-fit for height

        // Display a dialog to get the diagram name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Class Diagram");
        dialog.setHeaderText("Enter Class Diagram Name:");
        dialog.showAndWait().ifPresent(name -> {
            if (name.trim().isEmpty()) {
                System.out.println("Class diagram name cannot be empty.");
                return;
            }

            // Create a new class diagram
            ClassDiagram classDiagram = new ClassDiagram(name);

            // Create the toolbar and associate it with the canvas
            classDiagramToolbar = new ClassDiagramToolbar(classDiagramCanvasPanel);
            rootPane.setLeft(classDiagramToolbar);

            // Set the current diagram in the canvas
            classDiagramCanvasPanel.setCurrentDiagram(classDiagram);

            // Replace the contents of cardPane with the scrollable canvas
            cardPane.getChildren().setAll(scrollPane);

            // Make the toolbar visible
            classDiagramToolbar.setVisible(true);
        });
    }


    private void showUseCaseDiagram() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Use Case Diagram");
        dialog.setHeaderText("Enter Use Case Diagram Name:");
        dialog.showAndWait().ifPresent(name -> {
            UseCaseDiagramPanel useCaseDiagramPanel = new UseCaseDiagramPanel(name);
            usecaseToolbar = new UsecaseToolbar(useCaseDiagramPanel);
            usecaseToolbar.loadToolsForDiagramType("UseCaseDiagram");
            rootPane.setLeft(usecaseToolbar);
            usecaseToolbar.setVisible(true);

            cardPane.getChildren().setAll(useCaseDiagramPanel);
        });

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


            ClassPanel classPanel = new ClassPanel(className, isInterface,x, y);

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

                Method method = new Method(methodName, returnType,  parameters,access);
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

        // Initialize the canvas panel with the loaded diagram
        classDiagramCanvasPanel = new ClassDiagramCanvasPanel();
        ClassDiagram c_diagram=new ClassDiagram(diagramName);
        classDiagramCanvasPanel.setCurrentDiagram(c_diagram);

        // Update the UI
        classDiagramToolbar = new ClassDiagramToolbar(classDiagramCanvasPanel);
        rootPane.setLeft(classDiagramToolbar);
        cardPane.getChildren().setAll(classDiagramCanvasPanel);

        for(ClassPanel c: classDiagram.getClasses()){
        classDiagramCanvasPanel.addClassToCanvas(c,100,100);


        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
