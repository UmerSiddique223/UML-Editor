package UMLMain;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.MainFrame;
/**
 * The main entry point for the UML Editor application.
 * This class initializes the JavaFX application by launching the primary stage and starting the main frame of the application.
 */
public class Main extends Application {

    /**
     * Initializes the main application window and starts the main frame.
     * This method is called when the JavaFX application starts.
     *
     * @param primaryStage the primary stage for the application, which is passed from the JavaFX runtime
     */
    @Override
    public void start(Stage primaryStage) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.start(primaryStage);
    }

    /**
     * Launches the JavaFX application.
     * This method is the entry point to the application and invokes the JavaFX runtime.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}