package de.desy.language.snl.diagram.ui.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.SNLDiagram;

/**
 * Factory that maps model elements to TreeEditParts. TreeEditParts are used in
 * the outline view of the ShapesEditor.
 */
public class ShapesTreeEditPartFactory implements EditPartFactory {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart,
     * java.lang.Object)
     */
    @Override
    public EditPart createEditPart(final EditPart context, final Object model) {
        if (model instanceof SNLModel) {
            return new ShapeTreeEditPart((SNLModel) model);
        }
        if (model instanceof SNLDiagram) {
            return new DiagramTreeEditPart((SNLDiagram) model);
        }
        return null; // will not show an entry for the corresponding model
                        // instance
    }

}
