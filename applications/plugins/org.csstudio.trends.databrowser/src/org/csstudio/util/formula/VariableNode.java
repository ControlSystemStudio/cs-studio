package org.csstudio.util.formula;

/** Named Variable.
 *  @author Kay Kasemir
 */
public class VariableNode implements Node
{
    /** Name of the variable. */
    final private String name;
    
    /** Current value of the variable. */
    private double value;
    
    /** Create Variable with given name. */
    public VariableNode(final String name)
    {
        this.name = name;
    }

    /** Create Variable with given name and value. */
    public VariableNode(String name, double value)
    {
        this.name = name;
        this.value = value;
    }

    /** @return Returns the name. */
    final public String getName()
    {
        return name;
    }

    /** Set to a new value. */
    public void setValue(double value)
    {
        this.value = value;
    }

    /** @return Returns the value. */
    public double getValue()
    {
        return value;
    }

    public double eval()
    {
        return value;
    }
    
    /** {@inheritDoc} */
    public boolean hasSubnode(Node node)
    {
        return false;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
}
