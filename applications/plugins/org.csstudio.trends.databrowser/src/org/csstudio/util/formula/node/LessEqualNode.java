package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class LessEqualNode extends AbstractBinaryNode
{
    public LessEqualNode(Node left, Node right)
    {
        super(left, right);
    }
    
    public double eval()
    {
        final double a = left.eval();
        final double b = right.eval();
        return (a <= b) ? 1.0 : 0.0;
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(" + left + " <= " + right + ")";
    }
}
