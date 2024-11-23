package ui;

import javafx.scene.layout.VBox;

public abstract class AbstractDiagramPanel extends VBox implements Diagram {
    private final String title;

    public AbstractDiagramPanel(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    // Additional shared behaviors for diagrams can be added here.
}


