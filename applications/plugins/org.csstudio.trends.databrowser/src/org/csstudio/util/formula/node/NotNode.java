package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class NotNode implements Node
{
    private Node n;
    
    public NotNode(Node n)
    {
        this.n = n;
    }
    
    public double eval()
    {
        double a = n.eval();
        return (a != 0) ? 0.0 : 1.0;
    }
    
    /** {@inheritDoc} */
    public boolean hasSubnode(Node node)
    {
        return n == node;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "! (" + n + ")";
    }
}
