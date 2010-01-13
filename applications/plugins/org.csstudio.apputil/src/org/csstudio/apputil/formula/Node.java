package org.csstudio.apputil.formula;

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
     *  @return <code>true</code> if given node was found under this one.
     */
    public boolean hasSubnode(Node node);

    /** Check if this node has a sub-node with the name
     *  @return <code>true</code> if given node name was found under this one.
     */
    public boolean hasSubnode(String name);
}
