package tests;

import core.class_diagram.*;

import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.function.Consumer;

class ClassDiagramCanvasPanelTest {

    private ClassDiagramCanvasPanel canvasPanel;
    private ClassDiagram mockDiagram;

    @BeforeAll
    static void initJavaFX() {
        new JFXPanel(); // Initialize JavaFX runtime
    }

    @BeforeEach
    void setUp() {
        canvasPanel = new ClassDiagramCanvasPanel();
        mockDiagram = mock(ClassDiagram.class);
        canvasPanel.setCurrentDiagram(mockDiagram);
    }

    @Test
    void testAddClassToCanvas() {
        ClassPanel mockClass = new ClassPanel("Test", false, 100, 100, canvasPanel);
        double x = 100;
        double y = 200;

        mockDiagram = new ClassDiagram("Test Diagram");
        canvasPanel.setCurrentDiagram(mockDiagram);

        canvasPanel.addClassToCanvas(mockClass, x, y);

//        verify(mockDiagram).addClass(mockClass);
        System.out.print(canvasPanel.getChildren());
        assertTrue(canvasPanel.getDiagram().getClasses().contains(mockClass));
    }

    @Test
    void testEnableClassPlacementMode() {
        canvasPanel.enableClassPlacementMode(false);
        assertEquals(Cursor.CROSSHAIR, canvasPanel.getCursor());
    }

    @ParameterizedTest
    @CsvSource({
            "100, 200, true",
            "300, 400, false"
    })
    void testCreateAndAddClassToCanvasAt(double x, double y, boolean isInterface) {
        when(mockDiagram.getClasses()).thenReturn(new ArrayList<>());

        canvasPanel.createAndAddClassToCanvasAt(x, y, isInterface);

        verify(mockDiagram).getClasses();
        assertEquals(1, canvasPanel.getChildren().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Class1", "Interface1"})
    void testUpdatePosition(String className) {
        double x = 150;
        double y = 250;

        ClassPanel mockClass = mock(ClassPanel.class);
        when(mockDiagram.getClass(className)).thenReturn(mockClass);

        canvasPanel.updatePosition(className, x, y);

        verify(mockClass).setPosition(x, y);
    }

    @Test
    void testHandleClassDragEndAvoidsOverlap() {
        StackPane mockContainer = mock(StackPane.class);
        when(mockContainer.getLayoutX()).thenReturn(100.0);
        when(mockContainer.getLayoutY()).thenReturn(100.0);

        canvasPanel.getChildren().add(mockContainer);

        assertDoesNotThrow(() -> {
            canvasPanel.handleClassDragEnd(mockContainer);
        });
    }

//    @TestFactory
//    DynamicTest testDynamicRelationshipCreation() {
//        return DynamicTest.dynamicTest("Dynamic Test for Relationship", () -> {
//            String startClass = "ClassA";
//            String endClass = "ClassB";
//            String relationshipType = "association";
//
//            ClassPanel startMock = mock(ClassPanel.class);
//            ClassPanel endMock = mock(ClassPanel.class);
//
//            when(mockDiagram.getClass(startClass)).thenReturn(startMock);
//            when(mockDiagram.getClass(endClass)).thenReturn(endMock);
//
//            canvasPanel.setRelationship(relationshipType, startClass, endClass);
//
//            verify(mockDiagram).addRelationship(any());
//        });
//    }

    @Test
    void testSetRelationshipHandlesNulls() {
        String relationshipType = "association";

        assertDoesNotThrow(() -> {
            canvasPanel.setRelationship(relationshipType, null, null);
        });
    }

//    @Test
//    void testContextMenuAddition() {
//        ContextMenu spyMenu = spy(new ContextMenu());
//        canvasPanel.addContextMenu(spyMenu);
//        verify(spyMenu).show(any(), anyDouble(), anyDouble());
//    }

}
