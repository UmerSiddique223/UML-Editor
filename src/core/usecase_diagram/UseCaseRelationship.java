package core.usecase_diagram;

import core.usecase_diagram.UseCaseDiagramPanel;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class UseCaseRelationship {
    public final UseCaseDiagramPanel.DiagramComponent from;
    public final UseCaseDiagramPanel.DiagramComponent to;
    public final Line line;
    public final Text label;

    public UseCaseRelationship(UseCaseDiagramPanel.DiagramComponent from, UseCaseDiagramPanel.DiagramComponent to, Line line, Text label) {
        this.from = from;
        this.to = to;
        this.line = line;
        this.label = label;
    }
}