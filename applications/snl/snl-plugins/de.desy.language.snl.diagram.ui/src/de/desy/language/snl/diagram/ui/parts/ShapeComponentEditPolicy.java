package de.desy.language.snl.diagram.ui.parts;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.SNLDiagram;
import de.desy.language.snl.diagram.ui.commands.ShapeDeleteCommand;

/**
 * This edit policy enables the removal of a Shapes instance from its container.
 *
 * @see ShapeEditPart#createEditPolicies()
 * @see ShapeTreeEditPart#createEditPolicies()
 */
class ShapeComponentEditPolicy extends ComponentEditPolicy {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(
     * org.eclipse.gef.requests.GroupRequest)
     */
    @Override
    protected Command createDeleteCommand(final GroupRequest deleteRequest) {
        final Object parent = getHost().getParent().getModel();
        final Object child = getHost().getModel();
        if (parent instanceof SNLDiagram && child instanceof SNLModel) {
            return new ShapeDeleteCommand((SNLDiagram) parent, (SNLModel) child);
        }
        return super.createDeleteCommand(deleteRequest);
    }
}