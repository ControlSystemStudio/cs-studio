package de.desy.language.snl.diagram.ui.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;

import de.desy.language.snl.diagram.model.ModelElement;
import de.desy.language.snl.diagram.model.WhenConnection;
import de.desy.language.snl.diagram.ui.command.CreateBendPointCommand;
import de.desy.language.snl.diagram.ui.command.DeleteBendPointCommand;
import de.desy.language.snl.diagram.ui.command.MoveBendPointCommand;

/**
 * Edit part for Connection model elements.
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can
 * be notified of property changes in the corresponding model element.
 * </p>
 *
 */
class ConnectionEditPart extends AbstractConnectionEditPart implements
        PropertyChangeListener {

    /**
     * Upon activation, attach to the model element as a property change
     * listener.
     */
    @Override
    public void activate() {
        if (!isActive()) {
            super.activate();
            ((ModelElement) getModel()).addPropertyChangeListener(this);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    @Override
    protected void createEditPolicies() {
        // Selection handle edit policy.
        // Makes the connection show a feedback, when selected by the user.
        // installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
        // new ConnectionEndpointEditPolicy());
        // Allows the removal of the connection model element
        // installEditPolicy(EditPolicy.CONNECTION_ROLE,
        // new ConnectionEditPolicy() {
        // protected Command getDeleteCommand(GroupRequest request) {
        // return new ConnectionDeleteCommand(getCastedModel());
        // }
        // });
        installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
                new BendpointEditPolicy() {

                    @Override
                    protected Command getMoveBendpointCommand(
                            final BendpointRequest request) {
                        final int index = request.getIndex();
                        final Point location = request.getLocation();

                        getConnection().translateToRelative(location);

                        final MoveBendPointCommand moveBendPointCommand = new MoveBendPointCommand(
                                getCastedModel(), location, index);

                        return moveBendPointCommand;
                    }

                    @Override
                    protected Command getDeleteBendpointCommand(
                            final BendpointRequest request) {
                        final int index = request.getIndex();
                        final Point location = request.getLocation();

                        getConnection().translateToRelative(location);

                        final DeleteBendPointCommand deleteBendPointCommand = new DeleteBendPointCommand(
                                getCastedModel(), location, index);

                        return deleteBendPointCommand;
                    }

                    @Override
                    protected Command getCreateBendpointCommand(
                            final BendpointRequest request) {
                        final int index = request.getIndex();
                        final Point location = request.getLocation();

                        getConnection().translateToRelative(location);

                        final CreateBendPointCommand createBendPointCommand = new CreateBendPointCommand(
                                getCastedModel(), location, index);

                        return createBendPointCommand;
                    }
                });
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        final PolylineConnection connection = new PolylineConnection();
        // arrow at target endpoint
        connection.setTargetDecoration(new PolygonDecoration());
        // line drawing style
        connection.setLineStyle(getCastedModel().getLineStyle());

        getCastedModel().addPropertyChangeListener(this);

        final Label midLabel = new Label(getCastedModel().getWhenNode()
                .getSourceIdentifier());

        final ConnectionLocator locator = new MidConnectionRouteLocator(
                connection);
        locator.setRelativePosition(PositionConstants.SOUTH);
        connection.add(midLabel, locator);

        return connection;
    }

    private void refreshBendPoints() {
        final List<Point> modelConstraint = getCastedModel().getBendPoints();
        final List<Bendpoint> figureConstraint = new ArrayList<Bendpoint>();
        if (modelConstraint != null) {
            for (final Point current : modelConstraint) {
                final AbsoluteBendpoint abp = new AbsoluteBendpoint(current);
                figureConstraint.add(abp);
            }
            getConnectionFigure().setRoutingConstraint(figureConstraint);
        }
    }

    @Override
    protected void refreshVisuals() {
        super.refreshVisuals();
        refreshBendPoints();
    }

    /**
     * Upon deactivation, detach from the model element as a property change
     * listener.
     */
    @Override
    public void deactivate() {
        if (isActive()) {
            super.deactivate();
            ((ModelElement) getModel()).removePropertyChangeListener(this);
        }
    }

    private WhenConnection getCastedModel() {
        return (WhenConnection) getModel();
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        refreshBendPoints();
    }

}