package org.csstudio.dct.ui.graphicalviewer.model;

import org.csstudio.dct.model.IPrototype;

/**
 * Box node that represents {@link IPrototype}s in the graphical model.
 *
 * @author Sven Wende
 *
 */
public class PrototypeNode extends AbstractContainerNode<IPrototype> {
    /**
     * Standard constructor.
     *
     * @param prototype
     *            the dct prototype that is represented graphically
     */
    public PrototypeNode(IPrototype prototype) {
        super(prototype);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetCaption(IPrototype prototype) {
        return prototype.getName();
    }

}
