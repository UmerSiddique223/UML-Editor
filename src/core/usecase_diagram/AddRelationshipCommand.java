package core.usecase_diagram;

import java.util.logging.Logger;

/**
 * Command to add a relationship between two components.
 * Executing adds the relationship; undo removes it.
 */
public class AddRelationshipCommand implements Command {
    private static final Logger LOGGER = Logger.getLogger(AddRelationshipCommand.class.getName());

    private UseCaseDiagramPanel diagramPanel;
    private UseCaseDiagramPanel.DiagramComponent fromComponent;
    private UseCaseDiagramPanel.DiagramComponent toComponent;
    private String relationshipText;
    private boolean isExtend;
    private boolean isInclude;
    private UseCaseRelationship relationship;

    public AddRelationshipCommand(UseCaseDiagramPanel diagramPanel,
                                  UseCaseDiagramPanel.DiagramComponent fromComponent,
                                  UseCaseDiagramPanel.DiagramComponent toComponent,
                                  String relationshipText,
                                  boolean isExtend,
                                  boolean isInclude) {
        this.diagramPanel = diagramPanel;
        this.fromComponent = fromComponent;
        this.toComponent = toComponent;
        this.relationshipText = relationshipText;
        this.isExtend = isExtend;
        this.isInclude = isInclude;
    }

    @Override
    public void execute() {
        try {
            // Pass the isExtend and isInclude flags along when adding the relationship.
            relationship = diagramPanel.addRelationship(fromComponent, toComponent, relationshipText, isExtend, isInclude);
            LOGGER.info("Relationship '" + relationshipText + "' added between " + fromComponent.getText() + " and " + toComponent.getText() +
                    (isExtend ? " [<<extend>>]" : isInclude ? " [<<include>>]" : ""));
        } catch (Exception e) {
            LOGGER.severe("Failed to execute AddRelationshipCommand: " + e.getMessage());
        }
    }

    @Override
    public void undo() {
        try {
            if (relationship != null) {
                diagramPanel.removeRelationship(relationship);
                LOGGER.info("Relationship '" + relationshipText + "' removed via undo.");
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to undo AddRelationshipCommand: " + e.getMessage());
        }
    }
}
