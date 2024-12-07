package tests;

import core.class_diagram.*;
import ui.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import javafx.embed.swing.JFXPanel;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ClassPanelTest {

    private ClassPanel classPanel;
    private ClassDiagramCanvasPanel mockCanvas;

    @BeforeAll
    static void initJFX() {
        new JFXPanel();
    }

    @BeforeEach
    void setUp() {
        // Mock canvas panel
        mockCanvas = new ClassDiagramCanvasPanel();

        // Create a ClassPanel instance for testing
        classPanel = new ClassPanel("TestClass", false, 100, 200, mockCanvas);
    }

    @Test
    void testInitialValues() {
        assertEquals("TestClass", classPanel.getClassName());
        assertEquals(100, classPanel.getX());
        assertEquals(200, classPanel.getY());
        assertFalse(classPanel.isInterface());
        assertTrue(classPanel.getAttributes().isEmpty());
        assertTrue(classPanel.getMethods().isEmpty());
    }

    @Test
    void testSetPosition() {
        classPanel.setPosition(150, 250);
        assertEquals(150, classPanel.getX());
        assertEquals(250, classPanel.getY());
    }

    @Test
    void testAddAttribute() {
        Attribute attribute = new Attribute("name", "String", "private");
        classPanel.addAttribute(attribute);
        assertEquals(1, classPanel.getAttributes().size());
        assertEquals("name", classPanel.getAttributes().get(0).name);
        assertEquals("String", classPanel.getAttributes().get(0).type);
    }

    @Test
    void testAddMethod() {
        Method method = new Method("doSomething", "void", new ArrayList<>(), "public");
        classPanel.addMethod(method);
        assertEquals(1, classPanel.getMethods().size());
        assertEquals("doSomething", classPanel.getMethods().get(0).name);
        assertEquals("void", classPanel.getMethods().get(0).returnType);
    }

    @Test
    void testUpdateAttributes() {
        Attribute attribute1 = new Attribute("id", "int", "private");
        Attribute attribute2 = new Attribute("name", "String", "public");
        classPanel.addAttribute(attribute1);
        classPanel.addAttribute(attribute2);

        // Verify that the attributes are reflected correctly
        classPanel.updateAttributes();
        String attributesText = classPanel.attributesArea.getText();
        System.out.println(attributesText);
        assertTrue(attributesText.contains("- id :int"));
        assertTrue(attributesText.contains("+ name :String"));
    }

    @Test
    void testUpdateMethods() {
        Method method1 = new Method("getId", "int", new ArrayList<>(), "public");
        Method method2 = new Method("setName", "void", new ArrayList<>(), "protected");
        classPanel.addMethod(method1);
        classPanel.addMethod(method2);

        classPanel.updateMethods();
        // Verify that the methods are reflected correctly
        String methodsText = classPanel.methodsArea.getText();
        assertTrue(methodsText.contains("+ getId () :int"));
        assertTrue(methodsText.contains("# setName () :void"));
    }

    @Test
    void testRenameClass() {
        classPanel.titleField.setText("RenamedClass");
        assertEquals("RenamedClass", classPanel.getClassName());
    }

//    @Test
//    void testContextMenu() {
//        // Simulate a right-click event to trigger the context menu
//        MouseEvent rightClick = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0,
//                MouseButton.SECONDARY, 1, true, true, true, true,
//                true, true, true, true, true, true, null);
//
//        classPanel.fireEvent(rightClick);
//
//        // Verify the context menu is populated with expected items
//        assertNotNull(classPanel.ContextMenu);
//        assertTrue(classPanel.contextMenu.getItems().stream()
//                .anyMatch(item -> item.getText().contains("Add Method")));
//
//        if (!classPanel.isInterface()) {
//            assertTrue(classPanel.getContextMenu().getItems().stream()
//                    .anyMatch(item -> item.getText().contains("Add Attribute")));
//        }
//    }
}
