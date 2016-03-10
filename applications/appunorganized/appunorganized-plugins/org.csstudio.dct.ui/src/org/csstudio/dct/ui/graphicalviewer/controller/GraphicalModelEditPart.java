package org.csstudio.dct.ui.graphicalviewer.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.csstudio.dct.ui.graphicalviewer.model.DctGraphicalModel;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

/**
 * Controller for a {@link DctGraphicalModel}.
 *
 * @author Sven Wende
 * @version $Revision$
 */
public class GraphicalModelEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

    /**
     *{@inheritDoc}
     */
    @Override
    public void activate() {
        super.activate();
        getCastedModel().addPropertyChangeListener(this);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void deactivate() {
        getCastedModel().removePropertyChangeListener(this);
        super.deactivate();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        Figure f = new FreeformLayer();
        ToolbarLayout layout = new ToolbarLayout();
        layout.setStretchMinorAxis(false);
        layout.setSpacing(100);
        f.setLayoutManager(layout);
        f.setBorder(new MarginBorder(5));

        return f;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {

        installEditPolicy(EditPolicy.NODE_ROLE, null);
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
        installEditPolicy(EditPolicy.LAYOUT_ROLE,null);
//        installEditPolicy(EditPolicy.LAYOUT_ROLE, new XYLayoutEditPolicy() {
//
//            @Override
//            protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
//                return null;
//            }
//
//            @Override
//            protected Command getCreateCommand(CreateRequest request) {
//                return null;
//            }
//
//        });

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        return getCastedModel().getNodes();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void refreshVisuals() {
        ConnectionLayer cLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
        cLayer.setConnectionRouter(new FanRouter());
    }

    private DctGraphicalModel getCastedModel() {
        return (DctGraphicalModel) getModel();
    }
}
