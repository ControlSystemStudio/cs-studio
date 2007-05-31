package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class OrNode implements Node
{
    private Node left;
    private Node right;
    
    public OrNode(Node left, Node right)
    {
        this.left = left;
        this.right = right;
    }
    
    public double eval()
    {
        double a = left.eval();
        double b = right.eval();
        return (a != 0.0 || b != 0.0) ? 1.0 : 0.0;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(" + left + " | " + right + ")";
    }
}
