package bean;

import core.class_diagram.Attribute;
import core.class_diagram.ClassDiagram;
import core.class_diagram.ClassPanel;
import core.class_diagram.Method;
import core.class_diagram.Relationship;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;

public class DiagramSaver {

    public static void saveDiagram(ClassDiagram diagram) throws Exception {
        // Define the directory and file path
        String folderPath = "User diagrams";
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
}
