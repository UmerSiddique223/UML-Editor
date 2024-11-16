import ui.MainFrame;
import javax.swing.*;

import static data.DataManager.initializeDatabase;

public class Main {
    public static void main(String[] args) {
        // Launch the home page
//            initializeDatabase();
        
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
    }
}   


