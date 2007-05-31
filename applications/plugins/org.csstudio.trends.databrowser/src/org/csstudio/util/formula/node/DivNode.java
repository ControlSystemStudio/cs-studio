package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;


public class DivNode implements Node
{
    private Node left;
    private Node right;
    
    public DivNode(Node left, Node right)
    {
        this.left = left;
        this.right = right;
    }
    
    public double eval()
    {
        double a = left.eval();
        double b = right.eval();
        if (b == 0)
            return 0;
        return a / b;
    }
    
    @SuppressWarnings("nls")
    @Override
   public String toString()
    {
        return "(" + left + " / " + right + ")";
    }

}
