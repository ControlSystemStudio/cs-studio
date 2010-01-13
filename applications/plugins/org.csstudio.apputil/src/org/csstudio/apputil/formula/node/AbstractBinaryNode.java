package org.csstudio.apputil.formula.node;

import org.csstudio.apputil.formula.Node;

/** Abstract base for binary nodes.
 *  @author Kay Kasemir
 */
abstract class AbstractBinaryNode implements Node
{
    protected final Node left;
    protected final Node right;
    
    public AbstractBinaryNode(final Node left, final Node right)
    {
        this.left = left;
        this.right = right;
    }
    
    /** {@inheritDoc} */
    final public boolean hasSubnode(final Node node)
    {
        return left == node          || right == node ||
               left.hasSubnode(node) || right.hasSubnode(node);
    }

    /** {@inheritDoc} */
    final public boolean hasSubnode(final String name)
    {
        return left.hasSubnode(name) || right.hasSubnode(name);
    }
}
