package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class RndNode extends AbstractUnaryNode
{
    public RndNode(Node n)
    {
        super(n);
    }
    
    public double eval()
    {
        final double a = n.eval();
        return a*Math.random();
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(rnd(" + n + ")";
    }
}
