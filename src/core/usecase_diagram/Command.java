package core.usecase_diagram;

/**
 * An interface used to represent a command (Related to Undo/Redo feature).
 */
public interface Command {
    void execute();
    void undo();
}
