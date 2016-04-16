package de.desy.language.snl.diagram.ui.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import de.desy.language.snl.diagram.model.ModelElement;
import de.desy.language.snl.diagram.model.SNLDiagram;

/**
 * TreeEditPart for a ShapesDiagram instance. This is used in the Outline View
 * of the ShapesEditor.
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can
 * be notified of property changes in the corresponding model element.
 * </p>
 *
 */
class DiagramTreeEditPart extends AbstractTreeEditPart implements
        PropertyChangeListener {

    /**
     * Create a new instance of this edit part using the given model element.
     *
     * @param model
     *            a non-null ShapesDiagram instance
     */
    DiagramTreeEditPart(final SNLDiagram model) {
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
     * @see
     * org.eclipse.gef.examples.shapes.parts.ShapeTreeEditPart#createEditPolicies
     * ()
     */
    @Override
    protected void createEditPolicies() {
        // If this editpart is the root content of the viewer, then disallow
        // removal
        if (getParent() instanceof RootEditPart) {
            installEditPolicy(EditPolicy.COMPONENT_ROLE,
                    new RootComponentEditPolicy());
        }
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

    /**
     * Convenience method that returns the EditPart corresponding to a given
     * child.
     *
     * @param child
     *            a model element instance
     * @return the corresponding EditPart or null
     */
    private EditPart getEditPartForChild(final Object child) {
        return (EditPart) getViewer().getEditPartRegistry().get(child);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected List getModelChildren() {
        return getCastedModel().getChildren(); // a list of shapes
    }

    /*
     * (non-Javadoc)
     *
     * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
     * PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final String prop = evt.getPropertyName();
        if (SNLDiagram.CHILD_ADDED_PROP.equals(prop)) {
            // add a child to this edit part
            // causes an additional entry to appear in the tree of the outline
            // view
            addChild(createChild(evt.getNewValue()), -1);
        } else if (SNLDiagram.CHILD_REMOVED_PROP.equals(prop)) {
            // remove a child from this edit part
            // causes the corresponding edit part to disappear from the tree in
            // the outline view
            removeChild(getEditPartForChild(evt.getNewValue()));
        } else {
            refreshVisuals();
        }
    }
}
