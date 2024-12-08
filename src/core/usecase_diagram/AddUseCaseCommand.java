package core.usecase_diagram;

import java.util.logging.Logger;

/**
 * Command to add a use case to the diagram.
 * Executing adds a new use case; undo removes it.
 */
public class AddUseCaseCommand implements Command {
    private static final Logger LOGGER = Logger.getLogger(AddUseCaseCommand.class.getName());

    private UseCaseDiagramPanel diagramPanel;
    private double x, y;
    private String text;
    private UseCaseDiagramPanel.UseCaseComponent useCaseComponent;

    public AddUseCaseCommand(UseCaseDiagramPanel diagramPanel, double x, double y, String text) {
        this.diagramPanel = diagramPanel;
        this.x = x;
        this.y = y;
        this.text = text;
    }

    @Override
    public void execute() {
        try {
            useCaseComponent = diagramPanel.addUseCase(x, y, text);
            LOGGER.info("Use case '" + text + "' added at (" + x + "," + y + ")");
        } catch (Exception e) {
            LOGGER.severe("Failed to execute AddUseCaseCommand: " + e.getMessage());
        }
    }

    @Override
    public void undo() {
        try {
            diagramPanel.removeUseCase(useCaseComponent);
            LOGGER.info("Use case removed via undo command.");
        } catch (Exception e) {
            LOGGER.severe("Failed to undo AddUseCaseCommand: " + e.getMessage());
        }
    }
}
