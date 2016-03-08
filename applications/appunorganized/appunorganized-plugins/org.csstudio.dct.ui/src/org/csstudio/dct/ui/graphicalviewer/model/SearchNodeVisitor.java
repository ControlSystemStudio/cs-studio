package org.csstudio.dct.ui.graphicalviewer.model;

import java.util.UUID;

import org.csstudio.dct.model.IElement;

/**
 * Node visitor that searches for a node that represents an {@link IElement}
 * with a certain id.
 *
 * @author Sven Wende
 *
 */
public class SearchNodeVisitor implements INodeVisitor {
    private UUID id;
    private AbstractNode result;

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(AbstractNode node) {
        if (id.equals(node.getElement().getId())) {
            result = node;
        }
    }

    /**
     * Traverses the {@link DctGraphicalModel} to find a node that represents an
     * {@link IElement} from the original DCT model.
     *
     * @param id
     *            the id of the element in the original dct model
     * @param model
     *            the graphical model
     *
     * @return null or the representing node
     */
    public AbstractNode find(UUID id, DctGraphicalModel model) {
        this.id = id;
        this.result = null;
        model.accept(this);

        return result;
    }

}
