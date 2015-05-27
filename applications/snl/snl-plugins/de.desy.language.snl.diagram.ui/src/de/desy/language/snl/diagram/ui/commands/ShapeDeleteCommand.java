package de.desy.language.snl.diagram.ui.commands;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;

import de.desy.language.snl.diagram.model.WhenConnection;
import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.SNLDiagram;

/**
 * A command to remove a shape from its parent. The command can be undone or
 * redone.
 */
public class ShapeDeleteCommand extends Command {
    /** Shape to remove. */
    private final SNLModel child;

    /** ShapeDiagram to remove from. */
    private final SNLDiagram parent;
    /** Holds a copy of the outgoing connections of child. */
    private List<WhenConnection> sourceConnections;
    /** Holds a copy of the incoming connections of child. */
    private List<WhenConnection> targetConnections;
    /** True, if child was removed from its parent. */
    private boolean wasRemoved;

    /**
     * Create a command that will remove the shape from its parent.
     *
     * @param parent
     *            the ShapesDiagram containing the child
     * @param child
     *            the Shape to remove
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public ShapeDeleteCommand(final SNLDiagram parent, final SNLModel child) {
        if (parent == null || child == null) {
            throw new IllegalArgumentException();
        }
        setLabel("shape deletion");
        this.parent = parent;
        this.child = child;
    }

    /**
     * Reconnects a List of Connections with their previous endpoints.
     *
     * @param connections
     *            a non-null List of connections
     */
    private void addConnections(final List<WhenConnection> connections) {
        for (final Iterator<WhenConnection> iter = connections.iterator(); iter.hasNext();) {
            iter.next().reconnect();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.commands.Command#canUndo()
     */
    @Override
    public boolean canUndo() {
        return wasRemoved;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.commands.Command#execute()
     */
    @Override
    public void execute() {
        // store a copy of incoming & outgoing connections before proceeding
        sourceConnections = child.getSourceConnections();
        targetConnections = child.getTargetConnections();
        redo();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.commands.Command#redo()
     */
    @Override
    public void redo() {
        // remove the child and disconnect its connections
        wasRemoved = parent.removeChild(child);
        if (wasRemoved) {
            removeConnections(sourceConnections);
            removeConnections(targetConnections);
        }
    }

    /**
     * Disconnects a List of Connections from their endpoints.
     *
     * @param connections
     *            a non-null List of connections
     */
    private void removeConnections(final List<WhenConnection> connections) {
        for (final Iterator<WhenConnection> iter = connections.iterator(); iter.hasNext();) {
            iter.next().disconnect();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.commands.Command#undo()
     */
    @Override
    public void undo() {
        // add the child and reconnect its connections
        if (parent.addChild(child)) {
            addConnections(sourceConnections);
            addConnections(targetConnections);
        }
    }
}