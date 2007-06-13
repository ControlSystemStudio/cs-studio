package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

/** One computational node.
 *  @author Kay Kasemir
 */
public class OrNode extends AbstractBinaryNode
{
    public OrNode(Node left, Node right)
    {
        super(left, right);
    }
    
    public double eval()
    {
        return (left.eval() != 0.0   ||   right.eval() != 0.0) ? 1.0 : 0.0;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(" + left + " | " + right + ")";
    }
}
