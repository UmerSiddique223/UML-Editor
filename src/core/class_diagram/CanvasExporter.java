package core.class_diagram;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Utility class for exporting a JavaFX canvas or node as an image file.
 */
public class CanvasExporter {

    /**
     * Exports the provided JavaFX {@code Node} to an image file.
     *
     * @param canvas the JavaFX node to export, typically a canvas or pane
     * @param format the image format to export as (e.g., "png", "jpg")
     */
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
}
