package org.csstudio.trends.databrowser.model;

import java.util.ArrayList;

import org.csstudio.archive.crawl.SpreadsheetIterator;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.util.formula.VariableNode;

/** Handler for all the formula input.
 * 
 *  It maps them to variables, and allows spreadsheet-like
 *  iteration over their values.
 *  
 *  @author Kay Kasemir
 */
class FormulaInputs
{
    /** The model items used as inputs. */
    private ArrayList<IModelItem> items = new ArrayList<IModelItem>();

    /** The variables assigned to the inputs */
    private ArrayList<VariableNode> variables = new ArrayList<VariableNode>();
    
    /** Lockstep iterator over all item's samples. */
    private SpreadsheetIterator sheet = null;

    /** Add model item as variable with given name.
     *  @param item The model item
     *  @param name Name for the variable
     */
    void addInput(IModelItem item, String name)
    {
        items.add(item);
        variables.add(new VariableNode(name));
    }
    
    /** @return All variable nodes as used by formula */
    VariableNode [] getVariables()
    {
        VariableNode var_array[] = new VariableNode[variables.size()];
        for (int i=0; i<var_array.length; ++i)
            var_array[i] = variables.get(i);
        return var_array;
    }
    
    /** Start the spreadsheet-type iteration over all the input's samples */
    void startIteration()
    {
        ModelSampleIterator item_iters[] = new ModelSampleIterator[items.size()];
        for (int i=0; i<items.size(); ++i)
            item_iters[i] = new ModelSampleIterator(items.get(i).getSamples());
        sheet = new SpreadsheetIterator(item_iters);
    }
    
    /** Set the variables to the next set of input values.
     *  @return Timestamp for that set of values, or
     *          <code>null</code> if iteration is at end.
     */
    @SuppressWarnings("nls")
    ITimestamp next() throws Exception
    {
        if (!sheet.hasNext())
            return null;
        // SpreadsheetIterator requires getTime(), then next()
        ITimestamp time = sheet.getTime();
        IValue values[] = sheet.next();
        // Check cosistency with number of variables
        if (values.length != variables.size())
            throw new Exception("Got " + values.length
                        + " values for " + variables.size() + " Variables?");
        // Update the variables to current input data
        for (int i=0; i<values.length; ++i)
        {
            final double value = ValueUtil.getDouble(values[i]);
            variables.get(i).setValue(value);
        }
        return time;
    }
}

