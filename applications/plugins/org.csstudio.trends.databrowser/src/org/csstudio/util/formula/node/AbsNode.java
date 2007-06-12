package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class AbsNode extends AbstractUnaryNode
{
    public AbsNode(Node n)
    {
        super(n);
    }
    
    public double eval()
    {
        final double a = n.eval();
        return Math.abs(a);
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "abs(" + n + ")";
    }
}
