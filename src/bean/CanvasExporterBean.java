package bean;

import core.class_diagram.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CanvasExporterBean {

    public static void exportToImage(Node canvas, String format) {
        // Capture the canvas as an image
        WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), null);

        // Show file chooser for save location
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as " + format.toUpperCase());
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(format.toUpperCase() + " files", "*." + format.toLowerCase())
        );

        File file = fileChooser.showSaveDialog(canvas.getScene().getWindow());
        if (file != null) {
            try {
                // Write the image to the selected file
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), format, file);
                System.out.println("Exported to " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error exporting image: " + e.getMessage());
            }
        }
    }


    public static void exportToJavaCode(ClassDiagram diagram, String outputDirectory) throws Exception {
        // Create the output directory if it doesn't exist
        File folder = new File(outputDirectory);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Iterate through classes in the diagram
        for (ClassPanel classPanel : diagram.getClasses()) {
            String className = classPanel.ClassName; // Class name
            boolean isInterface = classPanel.isInterface(); // Check if it's an interface

            // Generate Java code for the class or interface
            String javaCode = generateJavaCodeForClass(classPanel, isInterface,diagram.getRelationships());

            // Save the code to a .java file
            File file = new File(outputDirectory + File.separator + className + ".java");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(javaCode);
                System.out.println("Generated: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error writing file for class: " + className);
                e.printStackTrace();
            }
        }
    }

    private static String generateJavaCodeForClass(ClassPanel classPanel, boolean isInterface, List<Relationship> relationships) {
        StringBuilder code = new StringBuilder();

        // Filter relationships where this class is the start class
        List<Relationship> relatedClasses = relationships.stream()
                .filter(rel -> rel.startClass.equals(classPanel.ClassName))
                .toList();

        // Determine parent class for inheritance
        String parentClass = relatedClasses.stream()
                .filter(rel -> rel.type.equals("inheritance"))
                .map(rel -> rel.endClass)
                .findFirst()
                .orElse(null);

        // Generate class or interface declaration
        if (isInterface) {
            code.append("public interface ").append(classPanel.ClassName).append(" {\n\n");
        } else {
            code.append("public class ").append(classPanel.ClassName);
            if (parentClass != null) {
                code.append(" extends ").append(parentClass);
            }
            code.append(" {\n\n");
        }

        // Add fields for relationships (aggregation, composition, association)
        code.append("    // Relationship Attributes\n");
        for (Relationship relationship : relatedClasses) {
            if (relationship.type.equals("association") || relationship.type.equals("aggregation")) {
                code.append("    private ").append(relationship.endClass).append(" ").append(camelCase(relationship.endClass)).append(";\n");
            } else if (relationship.type.equals("composition")) {
                code.append("    private ").append(relationship.endClass).append(" ").append(camelCase(relationship.endClass)).append(" = new ").append(relationship.endClass).append("();\n");
            }
        }
        code.append("\n");

        // Add attributes
        code.append("    // Attributes\n");
        for (Attribute attribute : classPanel.getAttributes()) {
            code.append("    ")
                    .append(attribute.getAccess())
                    .append(" ")
                    .append(attribute.getType())
                    .append(" ")
                    .append(attribute.getName())
                    .append(";\n");
        }
        code.append("\n");

        // Add methods
        code.append("    // Methods\n");
        for (Method method : classPanel.getMethods()) {
            code.append("    ")
                    .append(method.access)
                    .append(" ")
                    .append(method.returnType)
                    .append(" ")
                    .append(method.name)
                    .append("(")
                    .append(generateMethodParameters(method))
                    .append(") {\n");

            if (!isInterface) {
                code.append("        // TODO: Implement method logic\n");
            }

            code.append("    }\n\n");
        }

        // Close class or interface declaration
        code.append("}\n");

        return code.toString();
    }

    private static String camelCase(String className) {
        if (className == null || className.isEmpty()) return className;
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

    private static String generateMethodParameters(Method method) {
        StringBuilder params = new StringBuilder();
        for (String parameter : method.parameters) {
            if (params.length() > 0) params.append(", ");
            String[] paramParts = parameter.split(" ");
            if (paramParts.length == 2) {
                params.append(paramParts[0]) // Type
                        .append(" ")
                        .append(paramParts[1]); // Name
            }
        }
        return params.toString();
    }

}
