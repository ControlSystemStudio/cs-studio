package de.desy.language.snl.diagram.ui.command;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Point;

import de.desy.language.snl.diagram.model.WhenConnection;

/**
 * The Command to create a new BendPoint for a {@link Connection}.
 *
 * @author Kai Meyer (C1 WPS)
 *
 */
public class CreateBendPointCommand extends AbstractBendPointCommand {

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
    public CreateBendPointCommand(final WhenConnection model, final Point location,
            final int index) {
        super(model, location, index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        final Point location = getLocation();
        getConnectionModel().addBendPoint(location, getIndex());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        getConnectionModel().removeBendPoint(getIndex());
    }

}
