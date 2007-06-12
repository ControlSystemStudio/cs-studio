package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class MulNode extends AbstractBinaryNode
{
    public MulNode(Node left, Node right)
    {
        super(left, right);
    }
    
    public double eval()
    {
        final double a = left.eval();
        final double b = right.eval();
        return a * b;
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(" + left + " * " + right + ")";
    }

}
