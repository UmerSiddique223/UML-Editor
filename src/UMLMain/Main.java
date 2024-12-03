package UMLMain;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.MainFrame;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
