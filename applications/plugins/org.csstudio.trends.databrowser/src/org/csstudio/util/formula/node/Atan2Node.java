package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class Atan2Node implements Node
{
    final private Node left;
    final private Node right;
    
    public Atan2Node(Node left, Node right)
    {
        this.left = left;
        this.right = right;
    }
    
    public double eval()
    {
        final double a = left.eval();
        double b = right.eval();
        return Math.atan2(a, b);
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "atan2(" + left + " , " + right + ")";
    }
}
