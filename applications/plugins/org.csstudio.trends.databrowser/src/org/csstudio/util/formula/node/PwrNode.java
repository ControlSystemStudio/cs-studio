package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class PwrNode implements Node
{
    private Node left;
    private Node right;
    
    public PwrNode(Node left, Node right)
    {
        this.left = left;
        this.right = right;
    }
    
    public double eval()
    {
        double a = left.eval();
        double b = right.eval();
        return Math.pow(a, b);
    }
    
    /** {@inheritDoc} */
    public boolean hasSubnode(Node node)
    {
        return left == node   ||   right == node;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(" + left + " ) ^ (" + right + ")";
    }
}
