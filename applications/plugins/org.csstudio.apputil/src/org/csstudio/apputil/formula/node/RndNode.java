package org.csstudio.apputil.formula.node;

import org.csstudio.apputil.formula.Node;

/** One computational node.
 *  @author Kay Kasemir
 */
public class RndNode extends AbstractUnaryNode
{
    public RndNode(Node n)
    {
        super(n);
    }
    
    public double eval()
    {
        final double a = n.eval();
        return a*Math.random();
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(rnd(" + n + ")";
    }
}
