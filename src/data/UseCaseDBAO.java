package data;

import core.class_diagram.Attribute;
import core.class_diagram.ClassDiagram;
import core.class_diagram.ClassPanel;
import core.class_diagram.Method;
import core.class_diagram.Relationship;
import core.usecase_diagram.UseCaseDiagramPanel;
import core.usecase_diagram.UseCaseRelationship;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UseCaseDBAO {



    public static void saveUseCaseDiagram(UseCaseDiagramPanel diagram) throws Exception {
        // Define the directory and file path
        String folderPath = "User diagrams/Use Case Diagrams";
        File folder = new File(folderPath);

        // Create the folder if it doesn't exist
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Create the file path for the XML file
        String filePath = folderPath + File.separator + diagram.getName()+ ".xml"; // Customize filename logic if needed
        File file = new File(filePath);

        // Prepare the XML document
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        // Create the root element
        Element rootElement = document.createElement("UseCaseDiagram");
        document.appendChild(rootElement);

        Element nameElement = document.createElement("Name");
        nameElement.appendChild(document.createTextNode(diagram.getName()));
        rootElement.appendChild(nameElement);


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
        for (UseCaseRelationship relationship : diagram.relationships) {
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

        // Root element and diagram initialization
        Element rootElement = document.getDocumentElement();
        UseCaseDiagramPanel diagram = new UseCaseDiagramPanel( (String) rootElement.getElementsByTagName("Name").item(0).getTextContent());
        diagram.setStyle("-fx-background-color: lightgray;");

        // Load components (Actors and Use Cases)
        NodeList componentNodes = document.getElementsByTagName("Component");
        Map<String, UseCaseDiagramPanel.DiagramComponent> componentMap = new HashMap<>();

        for (int i = 0; i < componentNodes.getLength(); i++) {
            Element componentElement = (Element) componentNodes.item(i);

            String label = componentElement.getAttribute("label");
            String type = componentElement.getAttribute("type"); // Optional
            double x = Double.parseDouble(componentElement.getAttribute("x"));
            double y = Double.parseDouble(componentElement.getAttribute("y"));

            UseCaseDiagramPanel.DiagramComponent component;

            if ("Actor".equals(label)) {
                // Add actor using the proper method
                component = diagram.addActor(x, y);
            } else if ("UseCase".equals(type)) {
                // Add use case using the proper method
                component = diagram.addUseCase(x, y, label);
            } else {
                throw new IllegalArgumentException("Unknown component type: " + type);
            }

            componentMap.put(label, component); // Map for relationships
        }

        // Load relationships
        NodeList relationshipNodes = document.getElementsByTagName("Relationship");
        for (int i = 0; i < relationshipNodes.getLength(); i++) {
            Element relationshipElement = (Element) relationshipNodes.item(i);

            String fromLabel = relationshipElement.getAttribute("from");
            String toLabel = relationshipElement.getAttribute("to");
            String label = relationshipElement.getAttribute("label");

            UseCaseDiagramPanel.DiagramComponent from = componentMap.get(fromLabel);
            UseCaseDiagramPanel.DiagramComponent to = componentMap.get(toLabel);

            if (from != null && to != null) {
                // Add relationship using the proper method
                diagram.addRelationship(from, to, label, false, false); // Assuming isExtend/isInclude are not part of the XML
            } else {
                throw new IllegalArgumentException("Invalid relationship between " + fromLabel + " and " + toLabel);
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
