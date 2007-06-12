package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

/** Abstract base for binary nodes.
 *  @author Kay Kasemir
 */
abstract class AbstractBinaryNode implements Node
{
    protected final Node left;
    protected final Node right;
    
    public AbstractBinaryNode(Node left, Node right)
    {
        this.left = left;
        this.right = right;
    }
    
    /** {@inheritDoc} */
    final public boolean hasSubnode(Node node)
    {
        return left == node          || right == node ||
               left.hasSubnode(node) || right.hasSubnode(node);
    }
}
