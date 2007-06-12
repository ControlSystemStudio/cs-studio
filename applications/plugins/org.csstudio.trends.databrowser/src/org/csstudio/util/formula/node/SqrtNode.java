package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;


public class SqrtNode implements Node
{
    private Node n;
    
    public SqrtNode(Node n)
    {
        this.n = n;
    }
    
    public double eval()
    {
        double a = n.eval();
        if (a < 0)
            return 0;
        return Math.sqrt(a);
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
        return "sqrt(" + n + ")";
    }
}
