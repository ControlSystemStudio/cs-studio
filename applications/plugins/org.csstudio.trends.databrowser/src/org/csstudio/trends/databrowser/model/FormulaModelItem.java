package org.csstudio.trends.databrowser.model;

import java.util.ArrayList;

import org.csstudio.archive.ArchiveValues;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.IValue.Quality;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.TraceType;
import org.csstudio.util.formula.Formula;
import org.csstudio.util.formula.VariableNode;

/** Model item based on a formula.
 *  <p>
 *  Uses several other Model Items as inputs to variables,
 *  and computes results via a formula.
 *  
 *  @author Kay Kasemir
 */
public class FormulaModelItem extends AbstractModelItem
{
    class Input
    {
        private IModelItem item;
        private VariableNode variable;
        
        Input(IModelItem item, String name)
        {
            this.item = item;
            variable = new VariableNode(name);
        }
        
        IModelItem getModelItem()
        {   return item;  }
        
        VariableNode getVariableNode()
        {   return variable;  }
    }
    private ArrayList<Input> inputs = new ArrayList<Input>();
    private VariableNode variables[] = null;
    private Formula formula;
    
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
        inputs.add(new Input(item, name));
        updateVariableArray();
    }
    
    /** Define the formula.
     *  @param formula_text
     *  @throws Exception on parse error, including undefined variables.
     */
    public void setFormula(String formula_text) throws Exception
    {
        formula = new Formula(formula_text, variables);
        have_new_samples = true;
    }

    /** Update <code>variables</code> to reflect <code>inputs</code>. */
    private void updateVariableArray()
    {
        variables = new VariableNode[inputs.size()];
        for (int i=0; i<variables.length; ++i)
            variables[i] = inputs.get(i).getVariableNode();
        have_new_samples = true;
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

    public IModelSamples getSamples()
    {
        // Compute new samples from inputs and formula

        final INumericMetaData meta_data =
            ValueFactory.createNumericMetaData(0, 10, 0, 0, 0, 0, 1, "");
        
        // TODO handle more than one input (staircase-interpol.)
        ModelSampleArray result;
        IModelSamples samples = inputs.get(0).getModelItem().getSamples();
        synchronized (samples)
        {
            result = new ModelSampleArray(samples.size());
            for (int i=0; i<samples.size(); ++i)
            {
                final ModelSample sample = samples.get(i);
                final ITimestamp time = sample.getSample().getTime();
                variables[0].setValue(sample.getY());
                double number = formula.eval();
                
                System.out.format("%g -> %g\n", sample.getY(), number);
                
                IValue value = ValueFactory.createDoubleValue(time,
                                ValueFactory.createOKSeverity(),
                                "",
                                meta_data,
                                IValue.Quality.Interpolated,
                                new double[] { number });
                // TODO add time/value to result
                result.add(new ModelSample(value, getName()));
            }
        }
        return result;
    }
}
