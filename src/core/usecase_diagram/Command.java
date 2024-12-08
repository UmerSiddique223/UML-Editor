package core.usecase_diagram;

public interface Command {
    void execute();
    void undo();
}
