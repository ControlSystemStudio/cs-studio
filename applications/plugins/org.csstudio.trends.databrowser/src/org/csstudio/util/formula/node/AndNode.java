package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class AndNode extends AbstractBinaryNode
{
    public AndNode(Node left, Node right)
    {
        super(left, right);
    }
    
    public double eval()
    {
        return (left.eval() != 0.0   &&   right.eval() != 0.0) ? 1.0 : 0.0;
    }

   @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(" + left + " & " + right + ")";
    }

}
