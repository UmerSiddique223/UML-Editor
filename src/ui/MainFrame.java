package ui;

import core.class_diagram.*;
import core.usecase_diagram.UseCaseDiagramPanel;
import data.UseCaseDBAO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javafx.stage.Modality;


import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class MainFrame extends Application {

    private static BorderPane rootPane; // Main container
    private static StackPane cardPane; // For switching between views
    private static VBox homePanel;
    private static ClassDiagramCanvasPanel classDiagramCanvasPanel;
    private static ClassDiagramToolbar classDiagramToolbar; // Left-side toolbar
    private static UseCaseDiagramPanel useCaseDiagramPanel;
    private static Pane currentDiagramPanel;
    private Stage stage;
    private  static ClassDiagramPropertiesBar propertiesBar;

//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("UML Editor");
//
//        initializeComponents(primaryStage);
//
//        // Get screen dimensions
//        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
//        double screenWidth = screenBounds.getWidth();
//        double screenHeight = screenBounds.getHeight();
//
//        // Set the Scene and Stage size based on screen dimensions
//        Scene scene = new Scene(rootPane, screenWidth, screenHeight);
//        primaryStage.setScene(scene);
//        primaryStage.setX(screenBounds.getMinX());
//        primaryStage.setY(screenBounds.getMinY());
//        primaryStage.setWidth(screenWidth);
//        primaryStage.setHeight(screenHeight);
//
//        primaryStage.show();
//    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("UML Editor");

        // Create a root pane
initializeComponents(primaryStage);
        // Load the SVG as an image


        // Get screen dimensions
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Set the Scene and Stage size based on screen dimensions
        Scene scene = new Scene(rootPane, screenWidth, screenHeight);
        primaryStage.setScene(scene);
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenWidth);
        primaryStage.setHeight(screenHeight);
    stage=primaryStage;
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
        homePanel = new VBox(30); // Increased spacing for modern layout
        homePanel.setAlignment(Pos.CENTER);

        // Load background image
        String svgFilePath = "file:resources/bg.png"; // Replace with your SVG path
        Image svgImage = new Image(svgFilePath);

        // Set the SVG as a background
        BackgroundImage backgroundImage = new BackgroundImage(
                svgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, // Center the background
                new BackgroundSize(
                        BackgroundSize.DEFAULT.getWidth(),
                        BackgroundSize.DEFAULT.getHeight(),
                        false,
                        false,
                        true,
                        true // Ensure it scales to fit
                )
        );
        homePanel.setBackground(new Background(backgroundImage));

        // Heading
        Label heading = new Label("UML Diagram Maker");
        heading.setStyle(
                "-fx-font-size: 40px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #000; ");

        // Paragraph description
        Label description = new Label(
                "Design and visualize your UML diagrams effortlessly. " +
                        "Our editor provides powerful tools to create Class and Usecase diagrams."

        );
        description.setWrapText(true); // Wrap text for better readability
        description.setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-text-fill: #555555; " +
                        "-fx-line-spacing: 5; " +
                        "-fx-text-alignment: center;" // Center align for a clean look
        );
        description.setMaxWidth(600); // Restrict width for a focused view

        // Buttons
        Button classBtn = new Button("Class Diagram");
        Button useCaseBtn = new Button("Use Case Diagram");
        applyHoverEffects(classBtn, filledButtonStyle, filledButtonHoverStyle);
        applyHoverEffects(useCaseBtn, outlinedButtonStyle, outlinedButtonHoverStyle);

        // Set initial styles
        classBtn.setStyle(filledButtonStyle);
        useCaseBtn.setStyle(outlinedButtonStyle);

        classBtn.setOnAction(e -> {
            classBtn.setStyle(filledButtonStyle);
            useCaseBtn.setStyle(outlinedButtonStyle);
            showProjectDialog("Class Diagram");
        });

        useCaseBtn.setOnAction(e -> {
            useCaseBtn.setStyle(filledButtonStyle);
            classBtn.setStyle(outlinedButtonStyle);
            showProjectDialog("Use Case Diagram");
        });

        HBox buttonBox = new HBox(20); // Increased spacing for a modern look
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(classBtn, useCaseBtn);

        // Add elements to the home panel
        homePanel.getChildren().addAll(heading, description, buttonBox);
    }


    private void showProjectDialog(String diagramType) {
        // Create a new Stage for the dialog
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Manage " + diagramType + " Project");

        // Dialog layout
        VBox dialogLayout = new VBox(30);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d3d3d3; -fx-border-radius: 10;");

        // Title Label
        Label titleLabel = new Label("Manage " + diagramType + " Project");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4682b4;");

        // Text field for project name
        HBox createSection = new HBox(10);
        createSection.setAlignment(Pos.CENTER);

        TextField projectNameField = new TextField();
        projectNameField.setPromptText("Enter project name...");
        projectNameField.setPrefWidth(200); // Set preferred width for better usability

        // Create button
        Button createButton = new Button("Create");
        styleButton(createButton);

        createButton.setOnAction(e -> {
            String projectName = projectNameField.getText().trim();
            if (projectName.isEmpty()) {
                showAlert("Project name cannot be empty!");
            } else {
                if ("Class Diagram".equals(diagramType)) {
                    showClassDiagram(projectName);
                } else if ("Use Case Diagram".equals(diagramType)) {
                    showUseCaseDiagram(projectName);
                }
                dialogStage.close(); // Close the dialog after creating the project
            }
        });

        createSection.getChildren().addAll(projectNameField, createButton);
        Label orLabel = new Label("OR");
        orLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6a6a6a; -fx-font-weight: bold;");
        orLabel.setAlignment(Pos.CENTER);
        orLabel.setPrefWidth(50); // Keep the width compact

        // Load button
        Button loadButton = new Button("Load Existing Project");
        loadButton.setPrefWidth(200); // Adjust width for uniformity
        styleButton(loadButton);

        loadButton.setOnAction(e -> {
            MenuBarUI.showLoadDiagramWindow(stage);

            dialogStage.close(); // Close the dialog after loading
        });

        // Add elements to the layout
        dialogLayout.getChildren().addAll(titleLabel, createSection, orLabel,loadButton);

        // Set the scene and show the dialog
        Scene dialogScene = new Scene(dialogLayout, 500, 350); // Increased dimensions
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }
    /**
     * Helper method to create an action card for "Create" or "Load"
     */


    // Styles for buttons
    private final String filledButtonStyle =
            "-fx-font-size: 16px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: #4682b4; " + // Primary color
                    "-fx-text-fill: white; " +
                    "-fx-padding: 8 16 8 16; " + // Reduced padding for a compact look
                    "-fx-border-radius: 8px; " + // Rounded corners for modern feel
                    "-fx-background-radius: 8px;" + // Match the border radius
                    "-fx-cursor: hand;"; // Change cursor to hand on hover

    private final String filledButtonHoverStyle =
            "-fx-font-size: 16px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: #2d76b2; " + // Lighter hover color
                    "-fx-text-fill: white; " +
                    "-fx-padding: 8 16 8 16; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px;" +
                    "-fx-cursor: hand;";

    private final String outlinedButtonStyle =
            "-fx-font-size: 16px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: transparent; " +
                    "-fx-border-color: #4682b4; " + // Border to match the theme
                    "-fx-text-fill: #4682b4; " +
                    "-fx-padding: 8 16 8 16; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px;" +
                    "-fx-cursor: hand;";

    private final String outlinedButtonHoverStyle =
            "-fx-font-size: 16px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: rgba(70, 130, 180, 0.1); " + // Light background hover effect
                    "-fx-border-color: #4682b4; " +
                    "-fx-text-fill: #4682b4; " +
                    "-fx-padding: 8 16 8 16; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px;" +
                    "-fx-cursor: hand;";


    private void styleButton(Button button) {
        button.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #4682b4; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 5 10 5 10; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #1e90ff; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 5 10 5 10; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #4682b4; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 5 10 5 10; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        ));
    }

    private void applyHoverEffects(Button button, String baseStyle, String hoverStyle) {
        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
    }
    static void showHomePanel() {
        // Replace the contents of the cardPane with the home panel
        cardPane.getChildren().setAll(homePanel);

        // Hide or reset any toolbars or properties bar
        rootPane.setLeft(null);
        rootPane.setRight(null);

        // Optionally reset currentDiagramPanel if needed
        currentDiagramPanel = null;
    }


    // Function to initiate class diagram canvas
    static void showClassDiagram(String name) {
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
         propertiesBar = new ClassDiagramPropertiesBar(name, classDiagramCanvasPanel);
        rootPane.setRight(propertiesBar);

        // Make the toolbar visible
        classDiagramToolbar.setVisible(true);
        currentDiagramPanel=classDiagramCanvasPanel;

    }

    public static ClassDiagramPropertiesBar getPropertiesBar() {
        return propertiesBar;
    }
    static void showUseCaseDiagram(String name) {

        UseCaseDiagramPanel useCaseDiagramPanel = new UseCaseDiagramPanel(name);
        useCaseDiagramPanel.setStyle("-fx-background-color: lightgray;");

        UsecaseToolbar usecaseToolbar = new UsecaseToolbar(useCaseDiagramPanel);
        rootPane.setLeft(usecaseToolbar);
        usecaseToolbar.setVisible(true);
        rootPane.setRight(null);

        cardPane.getChildren().setAll(useCaseDiagramPanel);
        currentDiagramPanel = useCaseDiagramPanel;

    }


    public static Pane getCurrentDiagramPanel() {
        return currentDiagramPanel;
    }


    public static ClassDiagramCanvasPanel getClassDiagramCanvasPanel() {
        return classDiagramCanvasPanel;
    }
    public static void setClassDiagramCanvasPanel(ClassDiagramCanvasPanel c) {
         classDiagramCanvasPanel=c;
    }

    public static void setRootPane(BorderPane rootPane) {
        MainFrame.rootPane = rootPane;
    }

    public static void setCardPane(StackPane cardPane) {
        MainFrame.cardPane = cardPane;
    }

    public static void setHomePanel(VBox homePanel) {
        MainFrame.homePanel = homePanel;
    }

    public static void setClassDiagramToolbar(ClassDiagramToolbar classDiagramToolbar) {
        MainFrame.classDiagramToolbar = classDiagramToolbar;
    }

    public static void setUseCaseDiagramPanel(UseCaseDiagramPanel useCaseDiagramPanel) {
        MainFrame.useCaseDiagramPanel = useCaseDiagramPanel;
    }

    public static void setCurrentDiagramPanel(Pane currentDiagramPanel) {
        MainFrame.currentDiagramPanel = currentDiagramPanel;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void setPropertiesBar(ClassDiagramPropertiesBar propertiesBar) {
        MainFrame.propertiesBar = propertiesBar;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }



    public static BorderPane getRootPane() {
        return rootPane;
    }

    public static StackPane getCardPane() {
        return cardPane;
    }

    public static VBox getHomePanel() {
        return homePanel;
    }

    public static ClassDiagramToolbar getClassDiagramToolbar() {
        return classDiagramToolbar;
    }

    public static UseCaseDiagramPanel getUseCaseDiagramPanel() {
        return useCaseDiagramPanel;
    }
    public static void loadClassDiagram(File file) throws Exception {
        classDiagramCanvasPanel = new ClassDiagramCanvasPanel();
        classDiagramCanvasPanel.setStyle("-fx-background-color: lightgray;");
        classDiagramCanvasPanel.setPrefSize(2000, 2000);
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
        cardPane.getChildren().setAll(scrollPane);

        propertiesBar = new ClassDiagramPropertiesBar(diagramName, classDiagramCanvasPanel);
        rootPane.setRight(propertiesBar);

        for (ClassPanel c : classDiagram.getClasses()) {
            ClassPanel cp = new ClassPanel(c.getClassName(), c.isInterface(), c.getX(), c.getY(), classDiagramCanvasPanel);
            cp.setAttributes(c.getAttributes());
            cp.setMethods(c.getMethods());
            classDiagramCanvasPanel.addClassToCanvas(cp, c.getX(), c.getY());


        }
        Platform.runLater(() -> {
            for (Relationship r : classDiagram.getRelationships()) {
                classDiagramCanvasPanel.setRelationship(r.getType(), r.getStartClass(), r.getEndClass());
            }
            propertiesBar.refresh();
        });

        currentDiagramPanel=classDiagramCanvasPanel;

    }
    static void loadUseCaseDiagram(File file) throws Exception {
        UseCaseDiagramPanel loadedDiagram = UseCaseDBAO.loadUseCaseDiagram(file);

        if (loadedDiagram != null) {
            useCaseDiagramPanel = loadedDiagram;
            useCaseDiagramPanel.setStyle("-fx-background-color: lightgray;");
            UsecaseToolbar useCaseToolbar = new UsecaseToolbar(useCaseDiagramPanel);
            rootPane.setLeft(useCaseToolbar);
            cardPane.getChildren().setAll(useCaseDiagramPanel);
            useCaseToolbar.setVisible(true);
            currentDiagramPanel = useCaseDiagramPanel;
        } else {
            throw new RuntimeException("Failed to load use case diagram.");
        }
    }

//    public static void main(String[] args) {
//        launch(args);
//    }
}
