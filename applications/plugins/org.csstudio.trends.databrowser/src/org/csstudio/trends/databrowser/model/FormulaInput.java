package org.csstudio.trends.databrowser.model;

import org.csstudio.util.formula.VariableNode;

/** One input to the formula
 *  @author Kay Kasemir
 */
public class FormulaInput
{
    /** The model item used as input. */
    private final IModelItem item;
    
    /** The variable assigned to the input. */
    private final VariableNode variable;

    /** Constructor
     *  @param item ModelItem that provides the input data
     *  @param variable Variable unter which the data appears in formula
     */
    public FormulaInput(IModelItem item, VariableNode variable)
    {
        this.item = item;
        this.variable = variable;
    }

    /** Constructor
     *  @param item ModelItem that provides the input data
     *  @param variable_name Variable unter which the data appears in formula
     */
    public FormulaInput(IModelItem item, String variable_name)
    {
        this(item, new VariableNode(variable_name));
    }

    /** Constructor
     *  @param item ModelItem that provides the input data
     */
    public FormulaInput(IModelItem item)
    {
        this(item, item.getName());
    }

    /** @return the item */
    public final IModelItem getModelItem()
    {   return item;    }

    /** @return the variable */
    public final VariableNode getVariable()
    {   return variable;    }
}
