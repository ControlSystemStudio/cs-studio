package org.csstudio.apputil.formula.node;

import org.csstudio.apputil.formula.Node;

/** One computational node.
 *  @author Kay Kasemir
 */
public class AndNode extends AbstractBinaryNode
{
    public AndNode(Node left, Node right)
    {
        super(left, right);
    }
    
    public double eval()
    {
        return (left.eval() != 0.0   &&   right.eval() != 0.0) ? 1.0 : 0.0;
    }

   @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(" + left + " & " + right + ")";
    }

}
