package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

/** One computational node.
 *  @author Kay Kasemir
 */
public class NotNode extends AbstractUnaryNode
{
    public NotNode(Node n)
    {
        super(n);
    }
    
    public double eval()
    {
        final double a = n.eval();
        return (a != 0) ? 0.0 : 1.0;
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "! (" + n + ")";
    }
}
