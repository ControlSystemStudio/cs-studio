package org.csstudio.dct.ui.graphicalviewer.model;

/**
 * A node visitor. When applied to a {@link DctGraphicalModel} a callback method
 * is called for each existing node in the model.
 *
 * @author Sven Wende
 *
 */
public interface INodeVisitor {
    /**
     * Callback method which is called for every node in the model.
     *
     * @param node the current node
     */
    void visit(AbstractNode node);
}
