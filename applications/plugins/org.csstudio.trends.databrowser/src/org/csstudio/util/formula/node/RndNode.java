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
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(rnd(" + n + ")";
    }
}
