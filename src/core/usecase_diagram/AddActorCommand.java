package core.usecase_diagram;

import java.util.logging.Logger;

/**
 * Command to add an actor to the use case diagram.
 * If executed, adds a new actor component; undo removes it.
 */
public class AddActorCommand implements Command {
    private static final Logger LOGGER = Logger.getLogger(AddActorCommand.class.getName());

    private UseCaseDiagramPanel diagramPanel;
    private double x, y;
    private UseCaseDiagramPanel.ActorComponent actorComponent;

    public AddActorCommand(UseCaseDiagramPanel diagramPanel, double x, double y) {
        this.diagramPanel = diagramPanel;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute() {
        try {
            actorComponent = diagramPanel.addActor(x, y);
            LOGGER.info("Actor added via command at (" + x + "," + y + ")");
        } catch (Exception e) {
            LOGGER.severe("Failed to execute AddActorCommand: " + e.getMessage());
        }
    }

    @Override
    public void undo() {
        try {
            diagramPanel.removeActor(actorComponent);
            LOGGER.info("Actor removed via undo command.");
        } catch (Exception e) {
            LOGGER.severe("Failed to undo AddActorCommand: " + e.getMessage());
        }
    }
}
