package org.csstudio.sds.ui.internal.editparts;

import org.csstudio.sds.model.ConnectionElement;
import org.csstudio.sds.ui.internal.commands.ConnectionDeleteCommand;
import org.csstudio.sds.util.AntialiasingUtil;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**
 * The EditPart implementation for connections.
 * 
 * @author Sven Wende
 * @version $Revision$
 */
public final class ConnectionEditPart extends AbstractConnectionEditPart {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			// show figure
			getFigure().setVisible(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			// hide figure
			getFigure().setVisible(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		// determine connection behaviour
		boolean moveable = true;
		boolean deletable = true;

		if (moveable) {
			// Selection handle edit policy.
			// Makes the connection show a feedback, when selected by the user.
			installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
					new ConnectionEndpointEditPolicy());
		}

		if (deletable) {
			// Allows the removal of the connection widget model
			installEditPolicy(EditPolicy.CONNECTION_ROLE,
					new ConnectionEditPolicy() {
						@Override
						protected Command getDeleteCommand(
								final GroupRequest request) {
							Command cmd = new ConnectionDeleteCommand(
									getCastedModel());
							return cmd;
						}
					});
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		PolylineConnection f = new PolylineConnection() {

			@Override
			public void paint(final Graphics graphics) {
				AntialiasingUtil.getInstance().enableAntialiasing(graphics);
				super.paint(graphics);
			}

		};
		f.setTargetDecoration(new PolygonDecoration());
		f.setLineStyle(getCastedModel().getLineStyle());
		f.setLineWidth(2);
		f.setSourceAnchor(new ChopboxAnchor(new RectangleFigure()));
		f.setTargetAnchor(new ChopboxAnchor(new RectangleFigure()));

		return f;
	}

	/**
	 * Gets a casted reference of the model.
	 * 
	 * @return the casted reference
	 */
	protected ConnectionElement getCastedModel() {
		return (ConnectionElement) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		PolylineConnection f = (PolylineConnection) getFigure();
		f.setTargetDecoration(new PolygonDecoration());
		f.setLineStyle(getCastedModel().getLineStyle());
	}
}
