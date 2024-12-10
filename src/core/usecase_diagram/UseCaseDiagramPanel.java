package core.usecase_diagram;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Bounds;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import ui.ActorShape;
import ui.UndoableDiagramPanel;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * A panel for displaying and interacting with Use Case diagrams.
 * Supports adding Actors, Use Cases, and Relationships.
 * Provides undo/redo functionality via Command pattern.
 */


public class UseCaseDiagramPanel extends Pane implements UndoableDiagramPanel {
    private static final Logger LOGGER = Logger.getLogger(UseCaseDiagramPanel.class.getName());

    /**
     * Enum to represent the various interaction modes of the panel.
     */
    enum Mode {
        DEFAULT,
        ADD_ACTOR,
        ADD_USE_CASE,
        DRAG,
        EDIT_TEXT,
        DELETE,
        CREATE_RELATIONSHIP
    }

    // List of components and relationships in the diagram
    public List<DiagramComponent> components = new ArrayList<>();
    public List<UseCaseRelationship> relationships = new ArrayList<>();

    // Temporary variables for interaction
    private Line tempLine;
    private final String name;
    private DiagramComponent startComponent;

    // Interaction modes
    boolean relationshipCreationMode = false;
    boolean addActorMode = false;
    boolean addUseCaseMode = false;
    static boolean dragMode = false;
    private boolean editTextMode = false;
    boolean deleteMode = false;

    // Image for representing actors
    Image actorImage;

    // Current interaction mode
    Mode currentMode = Mode.DEFAULT;

    // Undo/redo stacks for command-based actions
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    /**
     * Constructor for the UseCaseDiagramPanel.
     * Initializes the diagram panel with a given name and attempts to load an actor image for representation.
     *
     * @param name The name of the Use Case Diagram.
     */
    public UseCaseDiagramPanel(String name) {
        super();
        this.name=name;
        loadActorImage();



    }

    /**
     * Adds an actor to the diagram at the specified coordinates.
     * Uses either a loaded image or a fallback circle shape for the actor representation.
     *
     * @param x The x-coordinate where the actor should be added.
     * @param y The y-coordinate where the actor should be added.
     * @return The created ActorComponent.
     */
    public ActorComponent addActor(double x, double y) {
        StackPane actorContainer = new StackPane();
        if (actorImage != null) {

            ActorShape actorShape = new ActorShape("");
            actorContainer.getChildren().add(actorShape);
            resetAllModes();

        } else {
            Circle fallbackActor = new Circle(30, Color.LIGHTGRAY);
            fallbackActor.setStroke(Color.BLACK);
            Text actorText = new Text("Actor");
            actorContainer.getChildren().addAll(fallbackActor, actorText);
        }



        actorContainer.setLayoutX(x);
        actorContainer.setLayoutY(y);

        long actorCount = components.stream()
                .filter(component -> component instanceof ActorComponent)
                .count();

        // Assign unique name based on count

        // Even if it's an image, we treat it as a shape for uniformity
        Shape shape = new Circle(30); // Dummy shape for references
        Text text = new Text("Actor " + (actorCount + 1));

        ActorComponent actorComponent = new ActorComponent(actorContainer, shape, text);
        components.add(actorComponent);
        getChildren().add(actorContainer);

        LOGGER.info("Actor added at (" + x + ", " + y + ")");
        return actorComponent;
    }

    /**
     * Removes an actor component from the diagram.
     * Deletes the actor and any associated relationships from the panel.
     *
     * @param actorComponent The ActorComponent to remove.
     */
    public void removeActor(ActorComponent actorComponent) {
        components.remove(actorComponent);
        getChildren().remove(actorComponent.container);
    }



    /**
     * Adds a use case to the diagram at the specified coordinates with the given text label.
     * Creates an ellipse to represent the use case.
     *
     * @param x The x-coordinate where the use case should be added.
     * @param y The y-coordinate where the use case should be added.
     * @param text The label of the use case.
     * @return The created UseCaseComponent.
     */
    public UseCaseComponent addUseCase(double x, double y, String text) {
        Ellipse useCase = new Ellipse(100, 40);
        useCase.setFill(Color.WHITE);
        useCase.setStroke(Color.BLACK);

        Text useCaseText = new Text(text);

        StackPane useCaseContainer = new StackPane(useCase, useCaseText);
        useCaseContainer.setLayoutX(x);
        useCaseContainer.setLayoutY(y);

        UseCaseComponent useCaseComponent = new UseCaseComponent(useCaseContainer, useCase, useCaseText);
        components.add(useCaseComponent);
        getChildren().add(useCaseContainer);
        resetAllModes();

        LOGGER.info("Use Case '" + text + "' added at (" + x + ", " + y + ")");
        return useCaseComponent;
    }



    /**
     * Removes a use case component from the diagram.
     * Deletes the use case and any associated relationships from the panel.
     *
     * @param useCaseComponent The UseCaseComponent to remove.
     */
    public void removeUseCase(UseCaseComponent useCaseComponent) {
        components.remove(useCaseComponent);
        getChildren().remove(useCaseComponent.container);
    }



    /** HELPER METHODS **/



    public void setEditTextMode(boolean mode) {
        if (mode) {
            resetAllModes();
            currentMode = Mode.EDIT_TEXT;
            editTextMode = true;
            setCursor(Cursor.HAND);
            // Use the default mouse pressed unless specifically overridden
        } else {
            resetAllModes();
        }
    }
    public boolean isEditTextMode() {
        return editTextMode;
    }


    public void setDeleteMode(boolean mode) {
        if (mode) {
            resetAllModes();
            currentMode = Mode.DELETE;
            deleteMode = true;
            setCursor(Cursor.HAND);
            // Default event handlers apply here; onMousePressedDefault will handle deletion
        } else {
            resetAllModes();
        }
    }
    /**
     * Attempts to load the actor.png image.
     * Logs a warning and uses a fallback shape if loading fails.
     */
    private void loadActorImage() {
        try {
            InputStream is = getClass().getResourceAsStream("actor.png");
            if (is != null) {
                actorImage = new Image(is);
                LOGGER.info("Actor image loaded successfully.");
            } else {
                LOGGER.warning("Actor image not found, fallback to circle representation.");
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to load actor image: " + e.getMessage() + ". Using fallback.");
        }
    }


    /**
     * Enables or disables the mode for adding actors to the diagram.
     * Sets the appropriate mouse cursor and handlers for interaction.
     *
     * @param mode True to enable adding actors; False to disable.
     */
    public void setAddActorMode(boolean mode) {
        if (mode) {
            resetAllModes();
            currentMode = Mode.ADD_ACTOR;
            addActorMode = true;
            setCursor(Cursor.CROSSHAIR);
            setOnMousePressed(this::onMousePressedToAddActor);
            setOnMouseDragged(null);
            setOnMouseReleased(null);
        } else {
            resetAllModes();
        }
    }

    /**
     * Enables or disables the mode for adding use cases to the diagram.
     * Sets the appropriate mouse cursor and handlers for interaction.
     *
     * @param mode True to enable adding use cases; False to disable.
     */
    public void setAddUseCaseMode(boolean mode) {
        if (mode) {
            resetAllModes();
            currentMode = Mode.ADD_USE_CASE;
            addUseCaseMode = true;
            setCursor(Cursor.CROSSHAIR);
            setOnMousePressed(this::onMousePressedToAddUseCase);
            setOnMouseDragged(null);
            setOnMouseReleased(null);
        } else {
            resetAllModes();
        }
    }

    /**
     * Enables or disables the mode for dragging components in the diagram.
     * Sets the appropriate mouse cursor and handlers for interaction.
     *
     * @param mode True to enable drag mode; False to disable.
     */
    public void setDragMode(boolean mode) {
        if (mode) {
            resetAllModes();
            currentMode = Mode.DRAG;
            dragMode = true;
            setCursor(Cursor.MOVE);
            setOnMousePressed(this::onMousePressedDefault);
            setOnMouseDragged(null);
            setOnMouseReleased(null);
        } else {
            resetAllModes();
        }
    }


    public void initiateRelationshipCreation() {
        resetAllModes();
        relationshipCreationMode = true;
        setCursor(Cursor.CROSSHAIR);
        setOnMousePressed(this::onMousePressedForRelationship);
        setOnMouseDragged(null);
        setOnMouseReleased(null);
    }



    /**
     * Enables or disables the mode for creating relationships between components in the diagram.
     * Configures the appropriate mouse cursor and event handlers.
     *
     * @param mode True to enable relationship creation mode; False to disable.
     */
    public void setRelationshipCreationMode(boolean mode) {
        if (mode) {
            resetAllModes();
            currentMode = Mode.CREATE_RELATIONSHIP;
            relationshipCreationMode = true;
            setCursor(Cursor.CROSSHAIR);
            setOnMousePressed(this::onMousePressedForRelationship);
            setOnMouseDragged(null);
            setOnMouseReleased(null);
        } else {
            resetAllModes();
        }
    }



    /**
     * Resets all interaction modes to the default mode.
     * Clears any specific mouse event handlers and restores the default cursor.
     */
    public void resetAllModes() {
        currentMode = Mode.DEFAULT;
        addActorMode = false;
        addUseCaseMode = false;
        dragMode = false;
        relationshipCreationMode = false;
        editTextMode = false;
        deleteMode = false;
        setCursor(Cursor.DEFAULT);
        setOnMousePressed(this::onMousePressedDefault);
        setOnMouseDragged(null);
        setOnMouseReleased(null);
    }

    /**
     * Prompts the user for text input using a dialog.
     *
     * @return The entered text, or null if the dialog was canceled.
     */
    String promptForText() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Use Case Name");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter text:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }



    private RelationshipOptions promptForRelationshipOptions() {
        // Create the custom dialog.
        Dialog<RelationshipOptions> dialog = new Dialog<>();
        dialog.setTitle("Enter Relationship Details");
        dialog.setHeaderText(null);

        // Set the button types.
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Create the layout.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add components.
        TextField textField = new TextField();
        textField.setPromptText("Relationship Label");

        CheckBox extendsCheckBox = new CheckBox("<<extends>>");
        CheckBox includesCheckBox = new CheckBox("<<includes>>");

        grid.add(new Label("Label:"), 0, 0);
        grid.add(textField, 1, 0);
        grid.add(extendsCheckBox, 0, 1);
        grid.add(includesCheckBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a RelationshipOptions object when the OK button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                String label = textField.getText().trim();
                boolean isExtend = extendsCheckBox.isSelected();
                boolean isInclude = includesCheckBox.isSelected();
                return new RelationshipOptions(label, isExtend, isInclude);
            }
            return null;
        });

        Optional<RelationshipOptions> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public String getName() {
        return name;
    }



/** HELPER METHODS ------------------------------------------------------------------------------------------------**/

    public void addRectangle(double x, double y) {
        // Prompt the user for dimensions
        TextInputDialog widthDialog = new TextInputDialog("100");
        widthDialog.setTitle("Rectangle Dimensions");
        widthDialog.setHeaderText("Enter Rectangle Width");
        widthDialog.setContentText("Width:");

        Optional<String> widthResult = widthDialog.showAndWait();
        if (widthResult.isEmpty()) {
            LOGGER.info("Rectangle creation canceled.");
            return;
        }

        double width;
        try {
            width = Double.parseDouble(widthResult.get().trim());
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid width entered. Using default value of 100.");
            width = 100;
        }

        TextInputDialog heightDialog = new TextInputDialog("50");
        heightDialog.setTitle("Rectangle Dimensions");
        heightDialog.setHeaderText("Enter Rectangle Height");
        heightDialog.setContentText("Height:");

        Optional<String> heightResult = heightDialog.showAndWait();
        if (heightResult.isEmpty()) {
            LOGGER.info("Rectangle creation canceled.");
            return;
        }

        double height;
        try {
            height = Double.parseDouble(heightResult.get().trim());
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid height entered. Using default value of 50.");
            height = 50;
        }

        // Create and add the rectangle
        Rectangle rectangle = new Rectangle(width, height, Color.LIGHTGRAY);
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.TRANSPARENT);

        StackPane rectangleContainer = new StackPane(rectangle);
        rectangleContainer.setLayoutX(x);
        rectangleContainer.setLayoutY(y);

        Text text = new Text("");
        rectangleContainer.getChildren().add(text);

        DiagramComponent rectangleComponent = new DiagramComponent(rectangleContainer, rectangle, text);
        components.add(rectangleComponent);
        getChildren().add(rectangleContainer);

        LOGGER.info("Rectangle container added at (" + x + ", " + y + ") with width " + width + " and height " + height);
    }



/**Mouse Listeners**/


    /**
     * Handles mouse press events when adding a use case.
     * Prompts the user for the use case name and adds it to the diagram at the mouse location.
     *
     * @param mouseEvent The MouseEvent triggering the action.
     */

    private void onMousePressedToAddUseCase(MouseEvent mouseEvent) {
        String useCaseText = promptForText();
        if (useCaseText != null && !useCaseText.trim().isEmpty()) {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();


            AddUseCaseCommand command = new AddUseCaseCommand(this, x, y, useCaseText.trim());
            command.execute();
            undoStack.push(command);
            redoStack.clear();
        } else {
            LOGGER.info("Use Case creation canceled or empty text.");
        }
    }


    /**
     * Handles mouse press events when adding an actor.
     * Adds an actor to the diagram at the mouse location.
     *
     * @param mouseEvent The MouseEvent triggering the action.
     */
    private void onMousePressedToAddActor(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();


        AddActorCommand command = new AddActorCommand(this, x, y);
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }


    /**
     * Handles mouse press events when creating a relationship.
     * Sets the starting component and prepares a temporary line for the relationship.
     *
     * @param mouseEvent The MouseEvent triggering the action.
     */
    private void onMousePressedForRelationship(MouseEvent mouseEvent) {
        if (relationshipCreationMode) {
            double localX = mouseEvent.getSceneX();
            double localY = mouseEvent.getSceneY();

            DiagramComponent component = getComponentAt(localX, localY);


            if (component != null) {
                startComponent = component;
                Point2D startCenter = startComponent.getCenter();
                tempLine = new Line(startCenter.getX(), startCenter.getY(), startCenter.getX(), startCenter.getY());
                tempLine.setStroke(Color.BLACK);
                tempLine.setStrokeWidth(2);
                tempLine.getStrokeDashArray().addAll(5.0, 5.0);
                getChildren().add(tempLine);

                setOnMouseDragged(this::onMouseDraggedForRelationship);
                setOnMouseReleased(this::onMouseReleasedForRelationship);
            } else {
                LOGGER.info("No component selected to start relationship.");
            }
        }
    }


    /**
     * Handles mouse drag events during relationship creation.
     * Updates the temporary line to follow the mouse cursor.
     *
     * @param mouseEvent The MouseEvent triggering the action.
     */
    private void onMouseDraggedForRelationship(MouseEvent mouseEvent) {
        if (tempLine != null) {
            double localX = mouseEvent.getX();
            double localY = mouseEvent.getY();
            tempLine.setEndX(localX);
            tempLine.setEndY(localY);
        }

    }

    /**
     * Handles mouse release events during relationship creation.
     * Finalizes the relationship if a valid end component is selected, otherwise cancels the operation.
     *
     * @param mouseEvent The MouseEvent triggering the action.
     */
    private void onMouseReleasedForRelationship(MouseEvent mouseEvent) {
        if (tempLine != null && startComponent != null) {
            double sceneX = mouseEvent.getSceneX();
            double sceneY = mouseEvent.getSceneY();

            DiagramComponent endComponent = getComponentAt(sceneX, sceneY);
            if (endComponent != null && endComponent != startComponent) {
                // Use the custom dialog instead of simple text input.
                RelationshipOptions options = promptForRelationshipOptions();

                if (options != null) {
                    String relationshipText = options.label;

                    // Handle the checkboxes.
                    if (options.isExtend && options.isInclude) {
                        // If both are selected, decide on priority or handle accordingly
                        // For simplicity, we'll prioritize 'extends'
                        relationshipText = "<<extends>>";
                        options.isInclude = false; // Ensure only one is applied
                    } else if (options.isExtend) {
                        relationshipText = "<<extends>>";
                    } else if (options.isInclude) {
                        relationshipText = "<<includes>>";
                    }

                    AddRelationshipCommand command = new AddRelationshipCommand(this, startComponent, endComponent, relationshipText, options.isExtend, options.isInclude);
                    command.execute();
                    undoStack.push(command);
                    redoStack.clear();
                } else {
                    LOGGER.info("Relationship creation canceled.");
                }
            } else {
                LOGGER.info("Invalid end component for relationship.");
            }

            getChildren().remove(tempLine);
            tempLine = null;
            startComponent = null;
            setOnMouseDragged(null);
            setOnMouseReleased(null);
            setRelationshipCreationMode(false);
            // resetAllModes();

        }
    }


    /**
     * Handles default mouse press events based on the current interaction mode.
     * Supports deletion or no-op for other modes.
     *
     * @param mouseEvent The MouseEvent triggering the action.
     */
    private void onMousePressedDefault(MouseEvent mouseEvent) {
        // Now we rely on currentMode checks rather than multiple booleans
        if (currentMode == Mode.DELETE) {
            double x = mouseEvent.getSceneX();
            double y = mouseEvent.getSceneY();
            DiagramComponent component = getComponentAt(x, y);

            if (component != null) {
                if (component instanceof ActorComponent) {
                    removeActor((ActorComponent) component);
                } else if (component instanceof UseCaseComponent) {
                    removeUseCase((UseCaseComponent) component);
                }

                // Remove connected relationships
                relationships.removeIf(relationship -> {
                    if (relationship.from == component || relationship.to == component) {
                        getChildren().remove(relationship.line);
                        if (relationship.label != null) {
                            getChildren().remove(relationship.label);
                        }
                        return true;
                    }
                    return false;
                });

                LOGGER.info("Deleted component and associated relationships.");
            } else {
                LOGGER.info("No component found at the clicked position.");
            }

            // After deleting, return to default mode:
            resetAllModes();
        } else {
            // If in DEFAULT or any other non-conflicting mode, do default behavior
            // For now, the default mode does nothing special on press
        }
    }


    private DiagramComponent getComponentAt(double sceneX, double sceneY) {
        for (DiagramComponent component : components) {
            if (component.containsSceneCoords(sceneX, sceneY)) {
                return component;
            }
        }
        return null;
    }

/*Mouse Listeners -------------------------------------------------------------------**/

/*Relationships**/





    /**
     * Represents the options selected in the relationship dialog.
     */
    private static class RelationshipOptions {
        String label;
        boolean isExtend;
        boolean isInclude;

        /**
         * Constructor for RelationshipOptions.
         *
         * @param label The label for the relationship.
         * @param isExtend True if the relationship is of type <<extends>>.
         * @param isInclude True if the relationship is of type <<includes>>.
         */
        RelationshipOptions(String label, boolean isExtend, boolean isInclude) {
            this.label = label;
            this.isExtend = isExtend;
            this.isInclude = isInclude;
        }
    }


    /**
     * Adds a relationship line between two components.
     * If relationshipText is "<<include>>" or "<<exclude>>", a dotted line with an arrow is drawn.
     *
     * @param from The starting component of the relationship.
     * @param to The ending component of the relationship.
     * @param relationshipText The label for the relationship.
     * @param isExtend True if the relationship is of type <<extends>>.
     * @param isInclude True if the relationship is of type <<includes>>.
     * @return The created UseCaseRelationship.
     */
    public UseCaseRelationship addRelationship(DiagramComponent from, DiagramComponent to, String relationshipText, boolean isExtend, boolean isInclude) {
        Line relationshipLine = new Line();
        relationshipLine.setStroke(Color.BLACK);
        relationshipLine.setStrokeWidth(2);

        // Binding start/end to components' centers
        relationshipLine.startXProperty().bind(from.centerInPaneXBinding());
        relationshipLine.startYProperty().bind(from.centerInPaneYBinding());
        relationshipLine.endXProperty().bind(to.centerInPaneXBinding());
        relationshipLine.endYProperty().bind(to.centerInPaneYBinding());

        // If it's an extension or inclusion, make the line dotted
        if (isExtend || isInclude) {
            relationshipLine.getStrokeDashArray().addAll(10.0, 10.0);
        }

        getChildren().addFirst(relationshipLine);

        Text label = null;
        if (relationshipText != null && !relationshipText.trim().isEmpty()) {
            label = new Text(relationshipText);
            label.setFill(Color.BLUE);
            label.setStyle("-fx-font-weight: bold;");
            // Position the label at the midpoint of the line
            label.layoutXProperty().bind(relationshipLine.startXProperty().add(relationshipLine.endXProperty()).divide(2));
            label.layoutYProperty().bind(relationshipLine.startYProperty().add(relationshipLine.endYProperty()).divide(2).subtract(5));
            getChildren().add(label);
        }


        UseCaseRelationship relationship = new UseCaseRelationship(from, to, relationshipLine, label);
        relationships.add(relationship);
        resetAllModes();

        return relationship;
    }


    /**
     * Removes a relationship from the diagram.
     * Deletes the relationship line and associated label from the panel.
     *
     * @param relationship The UseCaseRelationship to remove.
     */
    public void removeRelationship(UseCaseRelationship relationship) {
        // Remove the relationship from the internal list.
        relationships.remove(relationship);

        // Remove the line (if any). Whether it's dashed or solid,
        // removing it from the children removes it from the diagram.
        if (relationship.line != null) {
            getChildren().remove(relationship.line);
        }

        // Remove the label if it exists.
        if (relationship.label != null) {
            getChildren().remove(relationship.label);
        }
        resetAllModes();
    }

    /** Relationships --------------------------------------------------------------------- **/




    /**
     * Executes the most recent command in the undo stack, reverting its effects.
     * The command is moved to the redo stack for potential re-execution.
     */
    @Override
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            LOGGER.info("Undo executed.");
        } else {
            LOGGER.info("Nothing to undo.");
        }
    }



    /**
     * Re-executes the most recent command in the redo stack.
     * The command is moved back to the undo stack after execution.
     */
    @Override
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
            LOGGER.info("Redo executed.");
        } else {
            LOGGER.info("Nothing to redo.");
        }
    }

    /**
     * A generic diagram component with dragging support.
     */
    public static class DiagramComponent {
        public final Shape shape;
        public Text text;
        public final StackPane container;
        protected UseCaseDiagramPanel parentPanel;



        public DiagramComponent(StackPane container, Shape shape, Text text) {
            this.container = container;
            this.shape = shape;
            this.text = text;
            container.setOnMousePressed(this::componentOnMousePressed);
            container.setOnMouseDragged(this::componentOnMouseDragged);
            container.setOnMouseReleased(this::componentOnMouseReleased);
            // Parent might not be available at construction time, so we set it later
//            container.sceneProperty().addListener((_, _, newScene) -> {
//                if (newScene != null) {
//                    parentPanel = (UseCaseDiagramPanel) container.getParent();
//                }
//            });
//            container.setOnMouseClicked(this::componentOnMouseClicked);

        }private void componentOnMouseClicked(MouseEvent event) {
            if (parentPanel != null) {
                if (parentPanel.currentMode == Mode.EDIT_TEXT) {
                    if (this instanceof UseCaseDiagramPanel.ActorComponent) {
                        ((UseCaseDiagramPanel.ActorComponent) this).editText();
                        event.consume();
                        // After finishing editing, revert to default mode
                        parentPanel.resetAllModes();
                    }
                } else if (parentPanel.currentMode == Mode.DRAG) {
                    // Ignore if dragging is active or handle accordingly
                }
                // Other modes can be checked similarly as needed
            }
        }

        private double dragOffsetX;
        private double dragOffsetY;

        private void componentOnMousePressed(MouseEvent event) {
            if (dragMode) {
                double initialX = container.getLayoutX();
                double initialY = container.getLayoutY();
                dragOffsetX = event.getSceneX() - initialX;
                dragOffsetY = event.getSceneY() - initialY;
                container.toFront();
                event.consume(); // Consume event to prevent interference
            } else if (parentPanel != null && parentPanel.isEditTextMode()) {
                // Ignore drag events in edit mode if text editing is active
            }
        }

        private void componentOnMouseDragged(MouseEvent event) {
            if (dragMode) {
                double newX = event.getSceneX() - dragOffsetX;
                double newY = event.getSceneY() - dragOffsetY;
                container.setLayoutX(newX);
                container.setLayoutY(newY);
                event.consume(); // Consume event to prevent interference
            }



        }

        private void componentOnMouseReleased(MouseEvent event) {
            if (dragMode) {
                event.consume(); // Consume event to prevent interference
            }
        }

        public boolean containsSceneCoords(double sceneX, double sceneY) {
            Bounds boundsInScene = container.localToScene(container.getBoundsInLocal());
            return boundsInScene.contains(sceneX, sceneY);
        }


        /**
         * Returns the center of this component in scene coordinates.
         */
        public Point2D getCenterInScene() {
            Bounds boundsInScene = container.localToScene(container.getBoundsInLocal());
            double centerX = boundsInScene.getMinX() - (boundsInScene.getWidth())*2 ;
            double centerY = boundsInScene.getMinY() ;//- boundsInScene.getHeight() ;
            return new Point2D(centerX, centerY);
        }

        public DoubleBinding centerInPaneXBinding() {
            return container.layoutXProperty().add(container.getBoundsInLocal().getWidth() / 2);
        }

        public DoubleBinding centerInPaneYBinding() {
            return container.layoutYProperty().add(container.getBoundsInLocal().getHeight() / 2);
        }
        public Point2D getCenter() {
            Bounds boundsInLocal = container.getBoundsInLocal();
            double centerX = container.getLayoutX() + boundsInLocal.getWidth() / 2;
            double centerY = container.getLayoutY() + boundsInLocal.getHeight() / 2;
            return new Point2D(centerX, centerY);
        }
        public String getText() {
            return text.getText();
        }

        public void setText(Text text2) {
            this.text =  text2;
        }

    }


    /**
     * Actor Component class representing an actor in the diagram.
     */
    public class ActorComponent extends DiagramComponent {
        public ActorComponent(StackPane container, Shape shape, Text text) {
            super(container, shape, text);

        }

        public void editText() {
            TextInputDialog dialog = new TextInputDialog(this.text.getText());
            dialog.setTitle("Edit Actor Text");
            dialog.setHeaderText(null);
            dialog.setContentText("Enter new text:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newText -> {
                if (!newText.trim().isEmpty()) {
                    this.text.setText(newText.trim());
                    LOGGER.info("Actor text updated to: " + newText);

                    // Refresh the layout to ensure the text update is visible
                    container.getChildren().remove(this.text);
                    container.getChildren().add(this.text);
                    StackPane.setAlignment(this.text, Pos.BOTTOM_CENTER); // Align below the actor shape
                    this.text.setTranslateX(-20); // Adjust vertical po// Re-add updated text
                    container.requestLayout(); // Trigger layout refresh
                } else {
                    LOGGER.warning("Empty text entered. Actor text not updated.");
                }
            });
            resetAllModes();
        }
        }

    public class UseCaseComponent extends DiagramComponent {
        public UseCaseComponent(StackPane container, Ellipse useCase, Text text) {
            super(container, useCase, text);
        }
    }




}