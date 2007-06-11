package org.csstudio.trends.databrowser.model.formula_gui;

import org.csstudio.trends.databrowser.model.IModelItem;

/** One item in the formula input table.
 *  @author Kay Kasemir
 */
public class InputTableItem
{
    private IModelItem pv;
    private String variable_name;
    
    InputTableItem(IModelItem pv)
    {
        this.pv = pv;
        this.variable_name = pv.getName();
    }
    
    String getPVName()
    {   return pv.getName(); }
    
    String getVariableName()
    {   return variable_name; }
    
    void setVariableName(String new_name)
    {   variable_name = new_name; }
}
