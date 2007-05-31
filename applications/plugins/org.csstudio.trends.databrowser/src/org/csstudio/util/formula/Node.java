package org.csstudio.util.formula;

/** A node used to build a formula.
 *  @author Kay Kasemir
 */
public interface Node
{
    /** Evaluate the node, i.e. compute its value.
     *  @return The value of the node.
     */
    public double eval();    
}
