package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class IfNode implements Node
{
    private Node cond;
    private Node yes;
    private Node no;
    
    public IfNode(Node cond, Node yes, Node no)
    {
        this.cond = cond;
        this.yes = yes;
        this.no = no;
    }
    
    public double eval()
    {
        return (cond.eval() != 0) ? yes.eval() : no.eval();
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(" + cond + ") ? (" + yes + ") : (" + no + ")";
    }

}
