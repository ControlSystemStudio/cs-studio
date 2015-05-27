package de.desy.language.snl.diagram.ui.command;

import org.eclipse.draw2d.geometry.Point;

import de.desy.language.snl.diagram.model.WhenConnection;

/**
 *The Command to delete a BendPoint.
 *
 * @author Kai Meyer, Sebastian Middeke (C1 WPS)
 *
 */
public class DeleteBendPointCommand extends AbstractBendPointCommand {

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
    public DeleteBendPointCommand(final WhenConnection model, final Point location,
            final int index) {
        super(model, location, index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        getConnectionModel().removeBendPoint(getIndex());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        getConnectionModel().addBendPoint(getLocation(), getIndex());
    }

}
