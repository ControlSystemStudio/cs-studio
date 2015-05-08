package org.csstudio.dct.ui.graphicalviewer.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IElement;

/**
 * Base class for box nodes. Box nodes may have {@link Connection}s to other
 * nodes and theý can contain other nodes.
 *
 * @author Sven Wende
 *
 * @param <F>
 */
public abstract class AbstractContainerNode<F extends IElement> extends AbstractNode<F> {
    private List<AbstractNode> nodes;

    /**
     * Constructor.
     *
     * @param element
     *            the corresponding element in the original dct model
     */
    public AbstractContainerNode(F element) {
        super(element);
        nodes = new ArrayList<AbstractNode>();
    }

    /**
     * Adds a node.
     *
     * @param node
     */
    public void addNode(AbstractNode node) {
        nodes.add(node);
    }

    /**
     * Returns all sub nodes.
     *
     * @return all sub ndes
     */
    public List<AbstractNode> getNodes() {
        return nodes;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visit(this);

        for (AbstractNode node : nodes) {
            node.accept(visitor);
        }
    }
}
