package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;


public class ConstantNode implements Node
{
    double value;
    
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
