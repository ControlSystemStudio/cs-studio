package de.desy.language.snl.diagram.ui.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.commands.Command;

import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.SNLDiagram;

/**
 * A command to add a Shape to a ShapeDiagram. The command can be undone or
 * redone.
 */
public class ShapeCreateCommand extends Command {

    /** The new shape. */
    private final SNLModel newShape;
    /** ShapeDiagram to add to. */
    private final SNLDiagram parent;
    /** The bounds of the new Shape. */
    private final Rectangle bounds;

    /**
     * Create a command that will add a new Shape to a ShapesDiagram.
     *
     * @param newShape
     *            the new Shape that is to be added
     * @param parent
     *            the ShapesDiagram that will hold the new element
     * @param bounds
     *            the bounds of the new shape; the size can be (-1, -1) if not
     *            known
     * @throws IllegalArgumentException
     *             if any parameter is null, or the request does not provide a
     *             new Shape instance
     */
    public ShapeCreateCommand(final SNLModel newShape, final SNLDiagram parent,
            final Rectangle bounds) {
        this.newShape = newShape;
        this.parent = parent;
        this.bounds = bounds;
        setLabel("shape creation");
    }

    /**
     * Can execute if all the necessary information has been provided.
     *
     * @see org.eclipse.gef.commands.Command#canExecute()
     */
    @Override
    public boolean canExecute() {
        return newShape != null && parent != null && bounds != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.commands.Command#execute()
     */
    @Override
    public void execute() {
        newShape.setLocation(bounds.getLocation());
        final Dimension size = bounds.getSize();
        if (size.width > 0 && size.height > 0)
            newShape.setSize(size);
        redo();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.commands.Command#redo()
     */
    @Override
    public void redo() {
        parent.addChild(newShape);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.commands.Command#undo()
     */
    @Override
    public void undo() {
        parent.removeChild(newShape);
    }

}