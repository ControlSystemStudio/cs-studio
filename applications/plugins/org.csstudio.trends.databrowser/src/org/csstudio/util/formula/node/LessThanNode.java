package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class LessThanNode implements Node
{
    private Node left;
    private Node right;
    
    public LessThanNode(Node left, Node right)
    {
        this.left = left;
        this.right = right;
    }
    
    public double eval()
    {
        double a = left.eval();
        double b = right.eval();
        return (a < b) ? 1.0 : 0.0;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(" + left + " < " + right + ")";
    }
}
