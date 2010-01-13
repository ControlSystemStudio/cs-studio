package org.csstudio.apputil.formula.node;

import org.csstudio.apputil.formula.Node;

/** One computational node.
 *  @author Kay Kasemir
 */
public class ConstantNode implements Node
{
    final double value;
    
    public ConstantNode(final double value)
    {
        this.value = value;
    }
    
    public double eval()
    {
        return value;
    }
    
    /** {@inheritDoc} */
    public boolean hasSubnode(final Node node)
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean hasSubnode(final String node)
    {
        return false;
    }
    
    @Override
    public String toString()
    {
        return Double.toString(value);
    }
}
