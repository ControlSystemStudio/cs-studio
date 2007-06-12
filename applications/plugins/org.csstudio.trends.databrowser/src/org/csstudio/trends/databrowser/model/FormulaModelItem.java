package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.swt.chart.TraceType;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.util.formula.Formula;

/** Model item based on a formula.
 *  <p>
 *  Uses several other Model Items as inputs to variables,
 *  and computes results via a formula.
 *  
 *  @author Kay Kasemir
 */
public class FormulaModelItem extends AbstractModelItem
{
    /** The (changing) samples of this item,
     *  but keeping the same ModelSampleArray instance
     *  to hold them, since that's what the plot has received.
     */
    private final ModelSampleArray samples = new ModelSampleArray();

    /** The input items and their associated variables. */
    private FormulaInputs input_variables = new FormulaInputs(null);
    
    /** The formula.
     *  Its variables refer to (a subset) of the input_variables.
     */
    private Formula formula;

    /** Constructor */
    public FormulaModelItem(Model model, String pv_name,
                            int axis_index, double min, double max,
                            boolean visible,
                            boolean auto_scale,
                            int red, int green, int blue,
                            int line_width,
                            TraceType trace_type,
                            boolean log_scale)
    {
        super(model, pv_name, axis_index, min, max, visible, auto_scale,
              red, green, blue, line_width, trace_type, log_scale);
    }
    
    /** Define the formula.
     *  @param formula_text The formula
     *  @param inputs The input variables or <code>null</code>.
     *  @throws Exception on parse error, including undefined variables.
     */
    public void setFormula(String formula_text, FormulaInput inputs[])
        throws Exception
    {
        input_variables = new FormulaInputs(inputs);
        formula = new Formula(formula_text, input_variables.getVariables());
    }
    
    /** @return The formula. */
    public String getFormula()
    {
        return formula == null ? "" : formula.getFormula(); //$NON-NLS-1$
    }

    /** @return The inputs */
    public FormulaInput [] getInputs()
    {
        return input_variables.getInputs();
    }

    /** {@inheridDoc} */
    public IModelSamples getSamples()
    {
        // TODO re-compute when inputs change.
        // TODO throttle?
        compute();
        return samples;
    }

    /** Compute new samples from inputs and formula. */
    void compute()
    {
        input_variables.startIteration();
        final INumericMetaData meta_data = input_variables.getMetaData();
        synchronized (samples)
        {
            samples.clear();
            try
            {
                ITimestamp time = input_variables.next();
                while (time != null)
                {        
                    final double number = formula.eval();
                    IValue value = ValueFactory.createDoubleValue(
                                    time,
                                    ValueFactory.createOKSeverity(),
                                    "",  //$NON-NLS-1$
                                    meta_data,
                                    IValue.Quality.Interpolated,
                                    new double[] { number });
                    // Add a sample with that value and source = formula
                    samples.add(new ModelSample(value, formula.getFormula()));
                    // Prepare next row of the spreadsheet iterator
                    time = input_variables.next();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Plugin.logException("Formula '" + getName() + "'", ex);  //$NON-NLS-1$//$NON-NLS-2$
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getXMLContent()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** Format as string */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        final StringBuffer b = new StringBuffer();
        b.append("FormulaModelItem: " + formula.getFormula());
        final FormulaInput[] inputs = input_variables.getInputs();
        for (FormulaInput input : inputs)
        {
            b.append("\nInput '" + input.getModelItem().getName()
                     + "' = Variable '" + input.getVariable().getName() + "'");
        }
        return b.toString();
    }
}
