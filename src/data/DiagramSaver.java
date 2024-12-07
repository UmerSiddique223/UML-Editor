package data;

import core.class_diagram.Attribute;
import core.class_diagram.ClassDiagram;
import core.class_diagram.ClassPanel;
import core.class_diagram.Method;
import core.class_diagram.Relationship;
import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javafx.scene.text.Text;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DiagramSaver {

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

            // Save attributes
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
            relationshipElement.setAttribute("startClass", relationship.startClass);
            relationshipElement.setAttribute("endClass", relationship.endClass);
            relationshipElement.setAttribute("type", relationship.type);
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

    public static void saveUseCaseDiagram(UseCaseDiagramPanel diagram) throws Exception {
        // Define the directory and file path
        String folderPath = "User diagrams/Use Case Diagrams";
        File folder = new File(folderPath);

        // Create the folder if it doesn't exist
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Create the file path for the XML file
        String filePath = folderPath + File.separator + "UseCaseDiagram.xml"; // Customize filename logic if needed
        File file = new File(filePath);

        // Prepare the XML document
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        // Create the root element
        Element rootElement = document.createElement("UseCaseDiagram");
        document.appendChild(rootElement);

        // Save components
        Element componentsElement = document.createElement("Components");
        for (UseCaseDiagramPanel.DiagramComponent component : diagram.components) {
            Element componentElement = document.createElement("Component");

            // Save position
            componentElement.setAttribute("x", String.valueOf(component.container.getLayoutX()));
            componentElement.setAttribute("y", String.valueOf(component.container.getLayoutY()));

            // Save shape type (actor or use case) and text
            if (component.shape instanceof Ellipse) {
                componentElement.setAttribute("type", "UseCase");
            } else if (component.shape instanceof Rectangle) {
                componentElement.setAttribute("type", "Actor");
            }
            componentElement.setAttribute("label", component.getText());

            componentsElement.appendChild(componentElement);
        }
        rootElement.appendChild(componentsElement);

        // Save relationships
        Element relationshipsElement = document.createElement("Relationships");
        for (UseCaseDiagramPanel.Relationship relationship : diagram.relationships) {
            Element relationshipElement = document.createElement("Relationship");
            relationshipElement.setAttribute("from", relationship.from.getText());
            relationshipElement.setAttribute("to", relationship.to.getText());
            relationshipElement.setAttribute("label", relationship.label.getText());
            relationshipsElement.appendChild(relationshipElement);
        }
        rootElement.appendChild(relationshipsElement);

        // Write the document to the file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(file));

        System.out.println("Use case diagram saved to: " + filePath);
    }



    public static UseCaseDiagramPanel loadUseCaseDiagram(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);

        Element rootElement = document.getDocumentElement();
        UseCaseDiagramPanel diagram = new UseCaseDiagramPanel(rootElement.getAttribute("name"));



        NodeList componentNodes = document.getElementsByTagName("Component");
        for (int i = 0; i < componentNodes.getLength(); i++) {
            Element componentElement = (Element) componentNodes.item(i);

            String type = componentElement.getAttribute("type");
            String label = componentElement.getAttribute("label");
            double x = Double.parseDouble(componentElement.getAttribute("x"));
            double y = Double.parseDouble(componentElement.getAttribute("y"));

            StackPane container = new StackPane();
            Text text = new Text(label);
            Shape shape = "Actor".equals(type) ? new Rectangle(50, 50) : new Ellipse(40, 20);
            container.getChildren().addAll(shape, text);
            container.setLayoutX(x);
            container.setLayoutY(y);

            diagram.components.add(new UseCaseDiagramPanel.DiagramComponent(container, shape, text));
        }

        NodeList relationshipNodes = document.getElementsByTagName("Relationship");
        for (int i = 0; i < relationshipNodes.getLength(); i++) {
            Element relationshipElement = (Element) relationshipNodes.item(i);

            String fromLabel = relationshipElement.getAttribute("from");
            String toLabel = relationshipElement.getAttribute("to");
            String label = relationshipElement.getAttribute("label");

            UseCaseDiagramPanel.DiagramComponent from = findComponentByLabel(diagram.components, fromLabel);
            UseCaseDiagramPanel.DiagramComponent to = findComponentByLabel(diagram.components, toLabel);

            if (from != null && to != null) {
                Line line = new Line();
                Text labelText = new Text(label);
                diagram.relationships.add(new UseCaseDiagramPanel.Relationship(from, to, line, labelText));
            }
        }

        return diagram;
    }

    private static UseCaseDiagramPanel.DiagramComponent findComponentByLabel(List<UseCaseDiagramPanel.DiagramComponent> components, String label) {
        for (UseCaseDiagramPanel.DiagramComponent component : components) {
            if (component.getText().equals(label)) {
                return component;
            }
        }
        return null;
    }

}
