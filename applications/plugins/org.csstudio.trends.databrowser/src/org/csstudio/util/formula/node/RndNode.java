package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;


public class RndNode implements Node
{
	final private Node n;
	
    public RndNode(Node n)
    {
    	this.n = n;
    }
    
    public double eval()
    {
        double a = n.eval();
        return a*Math.random();
    }
    
    /** {@inheritDoc} */
    public boolean hasSubnode(Node node)
    {
        return n == node;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(rnd(" + n + ")";
    }
}
