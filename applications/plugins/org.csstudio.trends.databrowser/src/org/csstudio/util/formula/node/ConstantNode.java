package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

/** One computational node.
 *  @author Kay Kasemir
 */
public class ConstantNode implements Node
{
    final double value;
    
    public ConstantNode(double value)
    {
        this.value = value;
    }
    
    public double eval()
    {
        return value;
    }
    
    /** {@inheritDoc} */
    public boolean hasSubnode(Node node)
    {
        return false;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return Double.toString(value);
    }
}
