package org.csstudio.dct.ui.graphicalviewer.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.dct.ui.graphicalviewer.model.Connection;
import org.csstudio.dct.ui.graphicalviewer.view.ConnectionFigure;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.swt.graphics.RGB;

/**
 * Controller for a {@link Connection} between records.
 *
 * @version $Revision$
 */
public class ConnectionEditPart extends AbstractConnectionEditPart implements PropertyChangeListener {

    /**
     *{@inheritDoc}
     */
    @Override
    public void activate() {

        if (!isActive()) {
            super.activate();
            // register property change listener
            ((Connection) getModel()).addPropertyChangeListener(this);

            // show figure
            getFigure().setVisible(true);
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void deactivate() {
        if (isActive()) {
            super.deactivate();
            // remove property change listener
            ((Connection) getModel()).removePropertyChangeListener(this);

            // hide figure
            getFigure().setVisible(false);
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        refreshVisuals();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        boolean moveable = false;

        if (moveable) {
            // Selection handle edit policy.
            // Makes the connection show a feedback, when selected by the user.
            installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
        }

    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        ConnectionFigure figure = new ConnectionFigure();
        figure.setTargetDecoration(new PolygonDecoration());
        figure.setLineStyle(getCastedModel().getLineStyle());
        figure.setLineWidth(1);

        RGB rgb = getCastedModel().getCaption().startsWith("datalink") ? new RGB(128, 128, 255) : new RGB(128,0,128);
        figure.setForegroundColor(CustomMediaFactory.getInstance().getColor(rgb));

         ConnectionLocator locator = new ConnectionLocator(figure,
         ConnectionLocator.MIDDLE);
         locator.setGap(10);
         locator.setRelativePosition(3);
//         figure.add(new Label(getCastedModel().getCaption()), locator);

        return figure;
    }

    protected Connection getCastedModel() {
        return (Connection) getModel();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void refreshVisuals() {
    }

}