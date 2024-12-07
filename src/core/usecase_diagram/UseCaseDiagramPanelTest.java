package core.usecase_diagram;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class UseCaseDiagramPanelTest {
    private UseCaseDiagramPanel panel;

    @Before
    public void setup() {
        panel = new UseCaseDiagramPanel("Sample");
    }

    @Test
    public void testAddActor() {
        assertEquals(0, panel.components.size());
        AddActorCommand cmd = new AddActorCommand(panel, 100, 100);
        cmd.execute();
        assertEquals(1, panel.components.size());
        assertTrue(panel.components.get(0) instanceof UseCaseDiagramPanel.ActorComponent);
        cmd.undo();
        assertEquals(0, panel.components.size());
    }

    @Test
    public void testAddUseCase() {
        AddUseCaseCommand cmd = new AddUseCaseCommand(panel, 200, 200, "Login");
        cmd.execute();
        assertEquals(1, panel.components.size());
        assertEquals("Login", panel.components.get(0).getText());
        cmd.undo();
        assertEquals(0, panel.components.size());
    }


}
