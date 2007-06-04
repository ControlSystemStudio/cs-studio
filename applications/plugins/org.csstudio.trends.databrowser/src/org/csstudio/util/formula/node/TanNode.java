package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;


public class TanNode implements Node
{
    private Node n;
    
    public TanNode(Node n)
    {
        this.n = n;
    }
    
    public double eval()
    {
        double a = n.eval();
        if (a < 0)
            return 0;
        return Math.tan(a);
    }
   
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "tan(" + n + ")";
    }
}
