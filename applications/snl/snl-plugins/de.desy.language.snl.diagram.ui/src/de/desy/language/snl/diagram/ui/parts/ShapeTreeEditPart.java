package de.desy.language.snl.diagram.ui.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.graphics.Image;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;

import de.desy.language.snl.diagram.model.ModelElement;
import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.ui.ShapesPlugin;

/**
 * TreeEditPart used for Shape instances (more specific for EllipticalShape and
 * RectangularShape instances). This is used in the Outline View of the
 * ShapesEditor.
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can
 * be notified of property changes in the corresponding model element.
 * </p>
 *
 */
class ShapeTreeEditPart extends AbstractTreeEditPart implements
        PropertyChangeListener {

    /**
     * Create a new instance of this edit part using the given model element.
     *
     * @param model
     *            a non-null Shapes instance
     */
    ShapeTreeEditPart(final SNLModel model) {
        super(model);
    }

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
     * @see org.eclipse.gef.editparts.AbstractTreeEditPart#createEditPolicies()
     */
    @Override
    protected void createEditPolicies() {
        // allow removal of the associated model element
        installEditPolicy(EditPolicy.COMPONENT_ROLE,
                new ShapeComponentEditPolicy());
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

    private SNLModel getCastedModel() {
        return (SNLModel) getModel();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
     */
    @Override
    protected Image getImage() {
        final String name = getCastedModel().getIconName();
        return ShapesPlugin.getImageDescriptor("icons/" + name).createImage();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getText()
     */
    @Override
    protected String getText() {
        return getCastedModel().toString();
    }

    /*
     * (non-Javadoc)
     *
     * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
     * PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        refreshVisuals(); // this will cause an invocation of getImage() and
        // getText(), see below
    }
}