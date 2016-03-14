package de.desy.language.snl.diagram.ui.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import de.desy.language.snl.diagram.model.StateModel;
import de.desy.language.snl.diagram.model.ModelElement;
import de.desy.language.snl.diagram.model.StateSetModel;
import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.SNLDiagram;
import de.desy.language.snl.diagram.ui.commands.ShapeCreateCommand;
import de.desy.language.snl.diagram.ui.commands.ShapeSetConstraintCommand;

/**
 * EditPart for the a ShapesDiagram instance.
 * <p>
 * This edit part server as the main diagram container, the white area where
 * everything else is in. Also responsible for the container's layout (the way
 * the container rearranges is contents) and the container's capabilities (edit
 * policies).
 * </p>
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can
 * be notified of property changes in the corresponding model element.
 * </p>
 *
 */
class DiagramEditPart extends AbstractGraphicalEditPart implements
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
        // disallows the removal of this edit part from its parent
        installEditPolicy(EditPolicy.COMPONENT_ROLE,
                new RootComponentEditPolicy());
        // handles constraint changes (e.g. moving and/or resizing) of model
        // elements
        // and creation of new model elements
        installEditPolicy(EditPolicy.LAYOUT_ROLE,
                new ShapesXYLayoutEditPolicy());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        final Figure f = new FreeformLayer();
        f.setBorder(new MarginBorder(3));
        f.setLayoutManager(new FreeformLayout());

        // Create the static router for the connection layer
        final ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
        connLayer.setConnectionRouter(new ShortestPathConnectionRouter(f));

        return f;
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

    private SNLDiagram getCastedModel() {
        return (SNLDiagram) getModel();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     */
    @Override
    protected List<SNLModel> getModelChildren() {
        return getCastedModel().getChildren(); // return a list of shapes
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final String prop = evt.getPropertyName();
        // these properties are fired when Shapes are added into or removed from
        // the ShapeDiagram instance and must cause a call of refreshChildren()
        // to update the diagram's contents.
        if (SNLDiagram.CHILD_ADDED_PROP.equals(prop)
                || SNLDiagram.CHILD_REMOVED_PROP.equals(prop)) {
            refreshChildren();
        }
    }

    /**
     * EditPolicy for the Figure used by this edit part. Children of
     * XYLayoutEditPolicy can be used in Figures with XYLayout.
     *
     */
    private static class ShapesXYLayoutEditPolicy extends XYLayoutEditPolicy {

        /*
         * (non-Javadoc)
         *
         * @seeConstrainedLayoutEditPolicy#createChangeConstraintCommand(
         * ChangeBoundsRequest, EditPart, Object)
         */
        @Override
        protected Command createChangeConstraintCommand(
                final ChangeBoundsRequest request, final EditPart child,
                final Object constraint) {
            if (child instanceof ShapeEditPart
                    && constraint instanceof Rectangle) {
                // return a command that can move and/or resize a Shape
                return new ShapeSetConstraintCommand((SNLModel) child
                        .getModel(), request, (Rectangle) constraint);
            }
            return super.createChangeConstraintCommand(request, child,
                    constraint);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * ConstrainedLayoutEditPolicy#createChangeConstraintCommand(EditPart,
         * Object)
         */
        @Override
        protected Command createChangeConstraintCommand(final EditPart child,
                final Object constraint) {
            // not used in this example
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see LayoutEditPolicy#getCreateCommand(CreateRequest)
         */
        @Override
        protected Command getCreateCommand(final CreateRequest request) {
            final Object childClass = request.getNewObjectType();
            if (childClass == StateModel.class
                    || childClass == StateSetModel.class) {
                // return a command that can add a Shape to a ShapesDiagram
                return new ShapeCreateCommand(
                        (SNLModel) request.getNewObject(),
                        (SNLDiagram) getHost().getModel(),
                        (Rectangle) getConstraintFor(request));
            }
            return null;
        }

    }

}