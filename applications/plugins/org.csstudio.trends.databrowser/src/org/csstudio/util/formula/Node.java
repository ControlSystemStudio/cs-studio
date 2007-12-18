package org.csstudio.util.formula;

/** A node used to build a formula.
 *  @author Kay Kasemir
 */
public interface Node
{
    /** Evaluate the node, i.e. compute its value.
     *  @return The value of the node.
     *  @exception on error
     */
    public double eval();
    
    /** Check if this node has given node as a subnode
     *  @return <code>true</code> if given node was found unter this one.
     */
    public boolean hasSubnode(Node node);
}
