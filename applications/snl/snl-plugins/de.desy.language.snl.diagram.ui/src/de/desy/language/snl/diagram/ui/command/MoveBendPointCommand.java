package de.desy.language.snl.diagram.ui.command;

import org.eclipse.draw2d.geometry.Point;

import de.desy.language.snl.diagram.model.WhenConnection;

/**
 * The command to move a BendPoint.
 *
 * @author Kai Meyer, Sebastian Middeke (C1 WPS)
 *
 */
public class MoveBendPointCommand extends AbstractBendPointCommand {

    /**
     * The old location of the BendPoint to move.
     */
    private Point _oldLocation;

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
    public MoveBendPointCommand(final WhenConnection model, final Point location, final int index) {
        super(model, location, index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        final Point point = getConnectionModel().getBendPoints().get(getIndex());
        _oldLocation = point.getCopy();
        getConnectionModel().moveBendPoint(getIndex(), getLocation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        getConnectionModel().moveBendPoint(getIndex(), _oldLocation);
    }

}
