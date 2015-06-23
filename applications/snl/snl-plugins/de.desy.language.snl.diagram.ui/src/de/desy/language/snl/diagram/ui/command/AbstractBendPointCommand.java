package de.desy.language.snl.diagram.ui.command;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import de.desy.language.snl.diagram.model.WhenConnection;

/**
 * Abstract superclass for all BendPointCommands.
 *
 * @author Kai Meyer, Sebastian Middeke (C1 WPS)
 *
 */
public class AbstractBendPointCommand extends Command {

    /**
     * The model of the connection.
     */
    private final WhenConnection _model;
    /**
     * The location of the BendPoint.
     */
    private final Point _location;
    /**
     * The index of the BendPoint
     */
    private final int _index;

    /**
     * Constructor.
     *
     * @param model
     *            The model of the connection
     * @param location
     *            The location of the BendPoint
     * @param index
     *            The index of the BendPoint
     * @requires model != null
     * @requires location != null
     * @requires index >= 0
     */
    public AbstractBendPointCommand(final WhenConnection model, final Point location,
            final int index) {
        assert model != null : "model != null";
        assert location != null : "location != null";
        assert index >= 0 : "index >= 0";

        _model = model;
        _location = location;
        _index = index;
    }

    public Point getLocation() {
        return _location;
    }

    public int getIndex() {
        return _index;
    }

    public WhenConnection getConnectionModel() {
        return _model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void redo() {
        execute();
    }

}
