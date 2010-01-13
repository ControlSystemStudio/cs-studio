package org.csstudio.apputil.formula.node;

import org.csstudio.apputil.formula.Node;

/** Abstract base for unary nodes.
 *  @author Kay Kasemir
 */
abstract class AbstractUnaryNode implements Node
{
    protected final Node n;
    
    public AbstractUnaryNode(final Node n)
    {
        this.n = n;
    }
    
    /** {@inheritDoc} */
    final public boolean hasSubnode(final Node node)
    {
        return n == node  ||  n.hasSubnode(node);
    }

    /** {@inheritDoc} */
    final public boolean hasSubnode(final String name)
    {
        return n.hasSubnode(name);
    }
}
