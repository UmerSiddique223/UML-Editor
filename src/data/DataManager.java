package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DataManager {
    private static final String DB_URL = "jdbc:sqlite:uml_editor.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initializeDatabase() {
        String createShapesTable = """
                CREATE TABLE IF NOT EXISTS shapes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type TEXT NOT NULL,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    width INTEGER,
                    height INTEGER,
                    radius INTEGER,
                    canvas_state_id INTEGER,
                    FOREIGN KEY (canvas_state_id) REFERENCES canvas_states (id)
                );
                """;

        String createCanvasStatesTable = """
                CREATE TABLE IF NOT EXISTS canvas_states (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                """;

        String createUserPreferencesTable = """
                CREATE TABLE IF NOT EXISTS user_preferences (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    default_shape TEXT NOT NULL,
                    color TEXT NOT NULL
                );
                """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createShapesTable);
            stmt.execute(createCanvasStatesTable);
            stmt.execute(createUserPreferencesTable);
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
