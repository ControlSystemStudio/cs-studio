package org.csstudio.trends.databrowser.model;

import java.util.ArrayList;

import org.csstudio.archive.crawl.SpreadsheetIterator;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.util.formula.VariableNode;

/** Handler for formula inputs.
 * 
 *  It maps each input to a variable, and allows spreadsheet-like
 *  iteration over the inputs.
 *  
 *  @author Kay Kasemir
 */
class FormulaInputs
{
    /** The model items used as inputs. */
    private ArrayList<FormulaInput> inputs = new ArrayList<FormulaInput>();

    /** Lockstep iterator over all item's samples. */
    private SpreadsheetIterator sheet = null;

    /** @return number of inputs
     *  @see #getInput(int)
     */
    int getNumInputs()
    {   return inputs.size();    }
    
    /** @return One of the input items
      *  @see #getNumInputs()
      */
    FormulaInput getInput(int index)
    {   return inputs.get(index);    }
    
    /** Add model item as variable with given name.
     *  @param item The model item
     *  @param name Name for the variable
     */
    void addInput(IModelItem item, String name)
    {
        inputs.add(new FormulaInput(item, new VariableNode(name)));
    }
    
    /** @return All variable nodes as used by formula */
    VariableNode [] getVariables()
    {
        VariableNode var_array[] = new VariableNode[inputs.size()];
        for (int i=0; i<var_array.length; ++i)
            var_array[i] = inputs.get(i).getVariable();
        return var_array;
    }
    
    /** Start the spreadsheet-type iteration over all the input's samples */
    void startIteration()
    {
        ModelSampleIterator item_iters[] = new ModelSampleIterator[inputs.size()];
        for (int i=0; i<inputs.size(); ++i)
            item_iters[i] =
                new ModelSampleIterator(inputs.get(i).getModelItem().getSamples());
        sheet = new SpreadsheetIterator(item_iters);
    }

    /** @return Some best guess for the meta data. */
    INumericMetaData getMetaData()
    {
        // Get the biggest range and precision from the
        // first sample of each input item
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        int prec = 1;
        for (int i=0; i<inputs.size(); ++i)
        {
            final IModelSamples samples =
                inputs.get(i).getModelItem().getSamples();
            synchronized (samples)
            {
                if (samples.size() < 0)
                    continue;
                IMetaData meta = samples.get(0).getSample().getMetaData();
                if (! (meta instanceof INumericMetaData))
                    continue;
                INumericMetaData num_meta = (INumericMetaData) meta;
                if (min > num_meta.getDisplayLow())
                    min = num_meta.getDisplayLow();
                if (max < num_meta.getDisplayHigh())
                    max = num_meta.getDisplayHigh();
                if (prec < num_meta.getPrecision())
                    prec = num_meta.getPrecision();
            }
        }
        if (min >= max)
        {
            min = -10.0;
            max = +10.0;
        }
        return ValueFactory.createNumericMetaData(min, max,
                                        0, 0, 0, 0, prec, ""); //$NON-NLS-1$
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
        // Check consistency with number of variables
        if (values.length != inputs.size())
            throw new Exception("Got " + values.length
                        + " values for " + inputs.size() + " inpu?s");
        // Update the variables to current input data
        for (int i=0; i<values.length; ++i)
        {
            final VariableNode variable = inputs.get(i).getVariable();
            final IValue value = values[i];
            if (value != null)
                variable.setValue(ValueUtil.getDouble(value));
            else
                variable.setValue(Double.NaN);
        }
        return time;
    }
}

