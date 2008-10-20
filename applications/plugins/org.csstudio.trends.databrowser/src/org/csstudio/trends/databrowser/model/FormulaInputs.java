package org.csstudio.trends.databrowser.model;

import org.csstudio.apputil.formula.VariableNode;
import org.csstudio.archive.crawl.SpreadsheetIterator;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.ValueUtil;

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
    final private FormulaInput inputs[];
    
    /** Lockstep iterator over all item's samples. */
    private SpreadsheetIterator sheet = null;

    /** Constructor
     *  @param inputs The inputs
     */
    FormulaInputs(FormulaInput inputs[])
    {
        this.inputs = inputs;
    }

    /** @return Number of inputs, also handling 0 when there are none. */
    private int getNumInputs()
    {
        if (inputs == null)
            return 0;
        return inputs.length;
    }
    
    /** @return The inputs */
    FormulaInput [] getInputs()
    {
        return inputs;
    }
    
    /** @return All variable nodes as used by formula */
    VariableNode [] getVariables()
    {
        final int n = getNumInputs();
        VariableNode var_array[] = new VariableNode[n];
        for (int i=0; i<n; ++i)
            var_array[i] = inputs[i].getVariable();
        return var_array;
    }
    
    /** Start the spreadsheet-type iteration over all the input's samples */
    void startIteration()
    {
        final int n = getNumInputs();
        if (n <= 0)
        {
            sheet = null;
            return;
        }
        final ModelSampleIterator item_iters[] = new ModelSampleIterator[n];
        for (int i=0; i<n; ++i)
            item_iters[i] =
                new ModelSampleIterator(inputs[i].getModelItem().getSamples());
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
        final int n = getNumInputs();
        for (int i=0; i<n; ++i)
        {
            final IModelSamples samples =
                inputs[i].getModelItem().getSamples();
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
        if (sheet == null  ||  !sheet.hasNext())
            return null;
        // SpreadsheetIterator requires getTime(), then next()
        final ITimestamp time = sheet.getTime();
        final IValue values[] = sheet.next();
        // Check consistency with number of variables
        final int n = getNumInputs();
        if (values.length != n)
            throw new Exception("Got " + values.length
                        + " values for " + n + " inputs");
        // Update the variables to current input data
        for (int i=0; i<values.length; ++i)
        {
            final VariableNode variable = inputs[i].getVariable();
            final IValue value = values[i];
            if (value != null)
                variable.setValue(ValueUtil.getDouble(value));
            else
                variable.setValue(Double.NaN);
        }
        return time;
    }
}

