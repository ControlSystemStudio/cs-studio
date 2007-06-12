package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class ExpNode extends AbstractUnaryNode
{
    public ExpNode(Node n)
    {
        super(n);
    }
    
    public double eval()
    {
        final double a = n.eval();
        return Math.exp(a);
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "exp(" + n + ")";
    }
}
