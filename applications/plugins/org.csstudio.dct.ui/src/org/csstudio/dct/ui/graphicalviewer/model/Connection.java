package org.csstudio.dct.ui.graphicalviewer.model;

import org.eclipse.draw2d.Graphics;

/**
 * Represent a connection between two different nodes.
 *
 * @author Sven Wende
 *
 */
public class Connection extends AbstractBase {
    private static final long serialVersionUID = 1;

    private int lineStyle = Graphics.LINE_SOLID;

    private AbstractNode sourceNode;

    private AbstractNode targetNode;

    private String caption;

    /**
     * Constructor.
     *
     * @param sourceNode
     *            source node
     * @param targetNode
     *            target node
     */
    public Connection(AbstractNode sourceNode, AbstractNode targetNode) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;

        // .. connect
        sourceNode.addConnection(this);
        targetNode.addConnection(this);
    }

    /**
     * Returns the line style (a constant of {@link Graphics}).
     *
     * @return the line style
     */
    public int getLineStyle() {
        return lineStyle;
    }

    /**
     * Returns the source node.
     *
     * @return the source node
     */
    public AbstractNode getSourceNode() {
        return sourceNode;
    }

    /**
     * Returns the target node.
     *
     * @return the target node
     */
    public AbstractNode getTargetNode() {
        return targetNode;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        try {
            Connection con = (Connection) obj;

            if ((con.getSourceNode() == sourceNode) && (con.getTargetNode() == targetNode)) {
                result = true;
            }
        } catch (ClassCastException cce) {
            result = false;
        }

        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption.
     *
     * @param details
     */
    public void setCaption(String details) {
        this.caption = details;
    }

}