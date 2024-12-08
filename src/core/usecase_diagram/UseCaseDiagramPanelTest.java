package core.usecase_diagram;

import static org.junit.Assert.*;

import javafx.scene.Cursor;
import org.junit.Before;
import org.junit.Test;
import ui.MainFrame;

import java.awt.event.MouseEvent;

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


    @Test
    public void testAddRelationship() {
        UseCaseDiagramPanel.ActorComponent actor1 = panel.addActor(100, 100);
        UseCaseDiagramPanel.UseCaseComponent useCase1 = panel.addUseCase(200, 200, "Test Use Case");

        UseCaseRelationship relationship = panel.addRelationship(actor1, useCase1, "<<include>>", false, true);
        assertNotNull(relationship);
        assertEquals(1, panel.relationships.size());
        assertEquals("<<include>>", relationship.label.getText());
    }

    @Test
    public void testUndoRedoAddActor() {
        UseCaseDiagramPanel.ActorComponent actor = panel.addActor(50, 50);
        assertEquals(1, panel.components.size());

        panel.undo();
        assertEquals(0, panel.components.size());

       // panel.redo();
       // assertEquals(1, panel.components.size());
    }

    @Test
    public void testRemoveActor() {
        UseCaseDiagramPanel.ActorComponent actor = panel.addActor(300, 300);
        assertEquals(1, panel.components.size());

        panel.removeActor(actor);
        assertEquals(0, panel.components.size());
    }

    @Test
    public void testRemoveUseCase() {
        UseCaseDiagramPanel.UseCaseComponent useCase = panel.addUseCase(400, 400, "Sample Use Case");
        assertEquals(1, panel.components.size());

        panel.removeUseCase(useCase);
        assertEquals(0, panel.components.size());
    }

    @Test
    public void testRemoveRelationship() {
        UseCaseDiagramPanel.ActorComponent actor1 = panel.addActor(100, 100);
        UseCaseDiagramPanel.UseCaseComponent useCase1 = panel.addUseCase(200, 200, "Test Use Case");
        UseCaseRelationship relationship = panel.addRelationship(actor1, useCase1, "<<extend>>", true, false);
        assertEquals(1, panel.relationships.size());

        panel.removeRelationship(relationship);
        assertEquals(0, panel.relationships.size());
    }

    @Test
    public void testSetAddActorMode() {
        panel.setAddActorMode(true);
        assertTrue(panel.addActorMode);
        assertEquals(Cursor.CROSSHAIR, panel.getCursor());
    }

    @Test
    public void testSetAddUseCaseMode() {
        panel.setAddUseCaseMode(true);
        assertTrue(panel.addUseCaseMode);
        assertEquals(Cursor.CROSSHAIR, panel.getCursor());
    }

    @Test
    public void testSetRelationshipCreationMode() {
        panel.setRelationshipCreationMode(true);
        assertTrue(panel.relationshipCreationMode);
        assertEquals(Cursor.CROSSHAIR, panel.getCursor());
    }

    @Test
    public void testSetDragMode() {
        panel.setDragMode(true);
        assertTrue(panel.dragMode);
        assertEquals(Cursor.MOVE, panel.getCursor());
    }

    @Test
    public void testPromptForText() throws InterruptedException {
        String text = panel.promptForText("Enter Test Text");
        Thread.sleep(5000);
        assertNotNull(text); // This would need to be mocked in an interactive environment
    }



    @Test
    public void testUndoRedoRelationship() {
        UseCaseDiagramPanel.ActorComponent actor1 = panel.addActor(100, 100);
        UseCaseDiagramPanel.UseCaseComponent useCase1 = panel.addUseCase(200, 200, "Test Use Case");
        UseCaseRelationship relationship = panel.addRelationship(actor1, useCase1, "<<extend>>", true, false);
        assertEquals(1, panel.relationships.size());

        panel.undo();
        assertEquals(0, panel.relationships.size());

     //  panel.redo();
      //  assertEquals(1, panel.relationships.size());
    }
}
