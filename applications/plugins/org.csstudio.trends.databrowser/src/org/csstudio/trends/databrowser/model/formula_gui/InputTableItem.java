package org.csstudio.trends.databrowser.model.formula_gui;

/** One item in the formula input table.
 *  @author Kay Kasemir
 */
public class InputTableItem
{
    final private String pv;
    private String variable_name;
    
    InputTableItem(String pv, String variable)
    {
        this.pv = pv;
        this.variable_name = variable;
    }
    
    String getPVName()
    {   return pv; }
    
    String getVariableName()
    {   return variable_name; }
    
    void setVariableName(String new_name)
    {   variable_name = new_name; }
}
