package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;


public class AcosNode implements Node
{
    private Node n;
    
    public AcosNode(Node n)
    {
        this.n = n;
    }
    
    public double eval()
    {
        double a = n.eval();
        if (a < 0)
            return 0;
        return Math.acos(a);
    }
   
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "acos(" + n + ")";
    }
}
