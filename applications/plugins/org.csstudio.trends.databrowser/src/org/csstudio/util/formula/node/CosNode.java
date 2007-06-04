package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;


public class CosNode implements Node
{
    private Node n;
    
    public CosNode(Node n)
    {
        this.n = n;
    }
    
    public double eval()
    {
        double a = n.eval();
        if (a < 0)
            return 0;
        return Math.cos(a);
    }
   
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "cos(" + n + ")";
    }
}
