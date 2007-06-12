package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class SqrtNode extends AbstractUnaryNode
{
    public SqrtNode(Node n)
    {
        super(n);
    }
    
    public double eval()
    {
        final double a = n.eval();
        if (a < 0)
            return 0;
        return Math.sqrt(a);
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "sqrt(" + n + ")";
    }
}
