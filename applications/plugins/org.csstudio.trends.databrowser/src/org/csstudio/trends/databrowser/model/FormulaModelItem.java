package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.ArchiveValues;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.model.IArchiveDataSource;
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
    private FormulaInputs input_variables = new FormulaInputs();
    private Formula formula;
    
    /** Constructor */
    FormulaModelItem(Model model, String pv_name,
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
    
    /** Add an input item.
     *  @param item Model item that provides samples for formula.
     *  @param name Name of the variable to use in the formula.
     */
    public void addInput(IModelItem item, String name)
    {
        input_variables.addInput(item, name);
    }

    /** Define the formula.
     *  @param formula_text
     *  @throws Exception on parse error, including undefined variables.
     */
    public void setFormula(String formula_text) throws Exception
    {
        formula = new Formula(formula_text, input_variables.getVariables());
    }

    public void addArchiveSamples(ArchiveValues samples)
    {
        throw new Error("FormulaModelItem.addArchiveSamples?"); //$NON-NLS-1$
    }
    
    @Override
    public void addArchiveDataSource(IArchiveDataSource archive)
    {
        // NOP, since we don't use archived data
    }

    /** {@inheridDoc} */
    public IModelSamples getSamples()
    {
        // Compute new samples from inputs and formula
        // TODO this is expensive.
        // Check if the input samples actually changed,
        // only recompute when needed?
        ModelSampleArray samples = new ModelSampleArray();
        
        input_variables.startIteration();
        final INumericMetaData meta_data = input_variables.getMetaData();
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
                samples.add(new ModelSample(value, getName()));
                // Prepare next row of the spreadsheet iterator
                time = input_variables.next();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Plugin.logException("Formula '" + getName() + "'", ex);  //$NON-NLS-1$//$NON-NLS-2$
        }
        return samples;
    }
}
