package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

/** One computational node.
 *  @author Kay Kasemir
 */
public class PwrNode extends AbstractBinaryNode
{
    public PwrNode(Node left, Node right)
    {
        super(left, right);
    }
    
    public double eval()
    {
        final double a = left.eval();
        final double b = right.eval();
        return Math.pow(a, b);
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(" + left + " ) ^ (" + right + ")";
    }
}
