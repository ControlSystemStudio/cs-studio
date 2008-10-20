package org.csstudio.apputil.formula.node;

import org.csstudio.apputil.formula.Node;

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

    @Override
    public String toString()
    {
        return Double.toString(value);
    }
}
