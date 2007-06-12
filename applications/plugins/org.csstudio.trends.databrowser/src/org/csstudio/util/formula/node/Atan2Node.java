package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class Atan2Node extends AbstractBinaryNode
{
    public Atan2Node(Node left, Node right)
    {
        super(left, right);
    }
    
    public double eval()
    {
        final double a = left.eval();
        final double b = right.eval();
        return Math.atan2(a, b);
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "atan2(" + left + " , " + right + ")";
    }
}
