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
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "exp(" + n + ")";
    }
}
