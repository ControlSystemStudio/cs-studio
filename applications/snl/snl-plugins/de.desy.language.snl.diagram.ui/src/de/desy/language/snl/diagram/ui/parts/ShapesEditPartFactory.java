package de.desy.language.snl.diagram.ui.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.desy.language.snl.diagram.model.WhenConnection;
import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.SNLDiagram;

/**
 * Factory that maps model elements to edit parts.
 *
 */
public class ShapesEditPartFactory implements EditPartFactory {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart,
     * java.lang.Object)
     */
    @Override
    public EditPart createEditPart(final EditPart context,
            final Object modelElement) {
        // get EditPart for model element
        final EditPart part = getPartForElement(modelElement);
        // store model element in EditPart
        part.setModel(modelElement);
        return part;
    }

    /**
     * Maps an object to an EditPart.
     *
     * @throws RuntimeException
     *             if no match was found (programming error)
     */
    private EditPart getPartForElement(final Object modelElement) {
        if (modelElement instanceof SNLDiagram) {
            return new DiagramEditPart();
        }
        if (modelElement instanceof SNLModel) {
            return new ShapeEditPart();
        }
        if (modelElement instanceof WhenConnection) {
            return new ConnectionEditPart();
        }
        throw new RuntimeException("Can't create part for model element: "
                + ((modelElement != null) ? modelElement.getClass().getName()
                        : "null"));
    }

}