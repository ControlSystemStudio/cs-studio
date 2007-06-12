package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;


public class AbsNode implements Node
{
    private Node n;
    
    public AbsNode(Node n)
    {
        this.n = n;
    }
    
    public double eval()
    {
        double a = n.eval();
        return Math.abs(a);
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
        return "abs(" + n + ")";
    }
}
