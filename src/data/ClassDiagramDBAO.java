package data;

import core.class_diagram.*;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ui.ClassDiagramPropertiesBar;
import ui.ClassDiagramToolbar;
import ui.MainFrame;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;

public class ClassDiagramDBAO {

    public static void saveDiagram(ClassDiagram diagram) throws Exception {
        // Define the directory and file path
        String folderPath = "User Diagrams/Class Diagrams";
        File folder = new File(folderPath);

        // Create the folder if it doesn't exist
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Create the file path for the XML file
        String filePath = folderPath + File.separator + diagram.getName() + ".xml";
        File file = new File(filePath);

        // Prepare the XML document
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        // Create the root element
        Element rootElement = document.createElement("Diagram");
        document.appendChild(rootElement);

        // Add diagram metadata
        Element nameElement = document.createElement("Name");
        nameElement.appendChild(document.createTextNode(diagram.getName()));
        rootElement.appendChild(nameElement);

        // Save classes
        Element classesElement = document.createElement("Classes");
        for (ClassPanel classPanel : diagram.getClasses()) {
            Element classElement = document.createElement("Class");
            classElement.setAttribute("name", classPanel.ClassName);
            classElement.setAttribute("type", classPanel.isInterface() ? "interface" : "class");
            classElement.setAttribute("x", String.valueOf(classPanel.x));
            classElement.setAttribute("y", String.valueOf(classPanel.y));


            Element attributesElement = document.createElement("Attributes");
            for (Attribute attribute : classPanel.getAttributes()) {
                Element attributeElement = document.createElement("Attribute");
                attributeElement.setAttribute("name", attribute.getName());
                attributeElement.setAttribute("type", attribute.getType());
                attributeElement.setAttribute("access", attribute.getAccess());
                attributesElement.appendChild(attributeElement);
            }
            classElement.appendChild(attributesElement);

            // Save methods
            Element methodsElement = document.createElement("Methods");
            for (Method method : classPanel.getMethods()) {
                Element methodElement = document.createElement("Method");
                methodElement.setAttribute("name", method.name);
                methodElement.setAttribute("returnType", method.returnType);
                methodElement.setAttribute("access", method.access);

                // Add method parameters
                Element parametersElement = document.createElement("Parameters");
                for (String parameter : method.parameters) {
                    Element parameterElement = document.createElement("Parameter");
                    parameterElement.appendChild(document.createTextNode(parameter));
                    parametersElement.appendChild(parameterElement);
                }
                methodElement.appendChild(parametersElement);

                methodsElement.appendChild(methodElement);
            }
            classElement.appendChild(methodsElement);

            classesElement.appendChild(classElement);
        }
        rootElement.appendChild(classesElement);

        // Save relationships
        Element relationshipsElement = document.createElement("Relationships");
        for (Relationship relationship : diagram.getRelationships()) {
            Element relationshipElement = document.createElement("Relationship");
            relationshipElement.setAttribute("startClass", relationship.getStartClass());
            relationshipElement.setAttribute("endClass", relationship.getEndClass());
            relationshipElement.setAttribute("type", relationship.getType());
            System.out.println("Relationship: " + relationship.getStartClass() + " -> " + relationship.getEndClass()+ " type: " + relationship.getType());
            relationshipsElement.appendChild(relationshipElement);
        }
        rootElement.appendChild(relationshipsElement);

        // Write the document to the file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(file));

        System.out.println("Diagram saved to: " + filePath);
    }



    public static void LoadClassDiagram(
            File file) throws Exception {


        ClassDiagramCanvasPanel classDiagramCanvasPanel =new ClassDiagramCanvasPanel();
    MainFrame.getClassDiagramCanvasPanel().setStyle("-fx-background-color: lightgray;");
        MainFrame.getClassDiagramCanvasPanel().setPrefSize(2000, 2000);

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

        // Update UI components
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(classDiagramCanvasPanel);
        scrollPane.setPannable(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);

        // Initialize the canvas panel with the loaded diagram
        ClassDiagram c_diagram = new ClassDiagram(diagramName);
        classDiagramCanvasPanel.setCurrentDiagram(c_diagram);

        ClassDiagramToolbar classDiagramToolbar = new ClassDiagramToolbar(classDiagramCanvasPanel);
        MainFrame.getRootPane().setLeft(classDiagramToolbar);
        MainFrame.getCardPane().getChildren().setAll(scrollPane);

        ClassDiagramPropertiesBar propertiesBar = new ClassDiagramPropertiesBar(diagramName, classDiagramCanvasPanel);
        MainFrame.getRootPane().setRight(propertiesBar);

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

        // Update current diagram panel in mainframe
        MainFrame.setClassDiagramCanvasPanel(classDiagramCanvasPanel);
        MainFrame.setCurrentDiagramPanel(classDiagramCanvasPanel);
    }
}
