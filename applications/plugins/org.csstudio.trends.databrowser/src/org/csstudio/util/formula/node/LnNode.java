package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class LnNode implements Node
{
    private Node n;
    
    public LnNode(Node n)
    {
        this.n = n;
    }
    
    public double eval()
    {
        double a = n.eval();
        return Math.log(a);
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "ln(" + n + ")";
    }
}
