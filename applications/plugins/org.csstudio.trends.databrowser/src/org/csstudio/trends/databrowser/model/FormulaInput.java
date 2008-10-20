package org.csstudio.trends.databrowser.model;

import org.csstudio.apputil.formula.VariableNode;

/** One input to the formula
 *  @author Kay Kasemir
 */
public class FormulaInput
{
    /** The model item used as input. */
    private final IModelItem item;
    
    /** The variable assigned to the input. */
    private VariableNode variable;

    /** Constructor
     *  @param item ModelItem that provides the input data
     *  @param variable Variable unter which the data appears in formula
     */
    public FormulaInput(final IModelItem item, final VariableNode variable)
    {
        this.item = item;
        this.variable = variable;
    }

    /** Constructor
     *  @param item ModelItem that provides the input data
     *  @param variable_name Variable unter which the data appears in formula
     */
    public FormulaInput(final IModelItem item, final String variable_name)
    {
        this(item, new VariableNode(variable_name));
    }

    /** Constructor
     *  @param item ModelItem that provides the input data
     */
    public FormulaInput(final IModelItem item)
    {
        this(item, item.getName());
    }

    /** @return the item */
    final public IModelItem getModelItem()
    {   return item;    }

    /** @return the variable */
    final public VariableNode getVariable()
    {   return variable;    }

    /** Set a new variable name */
    final public void setVariable(final String new_name)
    {
        variable = new VariableNode(new_name);
    }
}
