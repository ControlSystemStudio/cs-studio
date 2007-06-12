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
        return (left.eval() != 0.0   ||   right.eval() != 0.0) ? 1.0 : 0.0;
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
        return "(" + left + " | " + right + ")";
    }
}
