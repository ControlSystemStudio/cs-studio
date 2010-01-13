package org.csstudio.apputil.ui.formula;

/** Input to a formula.
 *  Has an input name, for example a PV name or name of some
 *  other data source,
 *  and a Variable name under which that input is used
 *  in the formula.
 *  
 *  @author Kay Kasemir
 */
public class InputItem
{
    private final String input_name;
    private String variable_name;
    
    public InputItem(final String input, final String name)
    {
        this.input_name = input;
        this.variable_name = name;
    }

    public String getInputName()
    {
        return input_name;
    }

    public String getVariableName()
    {
        return variable_name;
    }
    
    public void setVariableName(final String variable_name)
    {
        this.variable_name = variable_name;
    }
}
