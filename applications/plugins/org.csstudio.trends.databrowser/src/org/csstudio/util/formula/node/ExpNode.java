package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;


public class ExpNode implements Node
{
    private Node n;
    
    public ExpNode(Node n)
    {
        this.n = n;
    }
    
    public double eval()
    {
        double a = n.eval();
        return Math.exp(a);
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
        return "exp(" + n + ")";
    }
}
