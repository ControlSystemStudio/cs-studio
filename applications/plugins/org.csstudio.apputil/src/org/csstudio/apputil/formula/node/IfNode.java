package org.csstudio.apputil.formula.node;

import org.csstudio.apputil.formula.Node;

/** One computational node.
 *  @author Kay Kasemir
 */
public class IfNode implements Node
{
    private final Node cond;
    private final Node yes;
    private final Node no;
    
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
    
    /** {@inheritDoc} */
    public boolean hasSubnode(Node node)
    {
        return cond == node  ||  yes == node  ||  no == node ||
               cond.hasSubnode(node) || yes.hasSubnode(node) ||
               no.hasSubnode(node);
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(" + cond + ") ? (" + yes + ") : (" + no + ")";
    }

}
