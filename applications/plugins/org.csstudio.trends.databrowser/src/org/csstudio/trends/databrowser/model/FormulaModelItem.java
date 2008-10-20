package org.csstudio.trends.databrowser.model;

import java.util.ArrayList;

import org.csstudio.apputil.formula.Formula;
import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLHelper;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.swt.chart.TraceType;
import org.csstudio.trends.databrowser.Plugin;
import org.w3c.dom.Element;

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

    /** Severity used for samples that don't compute. */
    private static ISeverity invalid_severity;

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
        if (invalid_severity == null)
            invalid_severity = new ISeverity()
        {
            // This distinguishes it from ValueFactory.createInvalid...
            public boolean hasValue()
            {   return false;  }

            public boolean isInvalid()
            {   return true;   }

            public boolean isMajor()
            {   return false;  }

            public boolean isMinor()
            {   return false;  }

            public boolean isOK()
            {   return false;  }

            @Override
            public String toString()
            {   return Messages.INVALID; }
        };
    }
    
    /** Define the formula.
     *  @param formula_text The formula
     *  @param inputs The input variables or <code>null</code>.
     *  @throws Exception on parse error, including undefined variables.
     */
    public void setFormula(final String formula_text,
                           final FormulaInput inputs[])
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

    /** Check if this formula uses given PV as an input.
     *  @param pv_name Name of the PV (not the variable alias!) to check.
     *  @return <code>true</code> if used in at least one input.
     */
    public boolean usesInputPV(final String pv_name)
    {
        final FormulaInput[] inputs = input_variables.getInputs();
        for (FormulaInput input : inputs)
        {   // Is this input reading the pv, AND is it used in the formula?
            if (input.getModelItem().getName().equals(pv_name) &&
                formula.hasSubnode(input.getVariable()))
                    return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    public IModelSamples getSamples()
    {
        return samples;
    }

    /** Compute new samples from inputs and formula. */
    void compute()
    {
        final ISeverity ok_severity = ValueFactory.createOKSeverity();
        final String ok_status = ""; //$NON-NLS-1$
        final String invalid_status = Messages.NoNumericValue;
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
                    IValue value;
                    if (Double.isNaN(number)  ||  Double.isInfinite(number))
                        value = ValueFactory.createDoubleValue(
                                    time, invalid_severity, invalid_status,
                                    meta_data, IValue.Quality.Interpolated,
                                    new double[] { number });
                    else
                        value = ValueFactory.createDoubleValue(
                                    time, ok_severity, ok_status,
                                    meta_data, IValue.Quality.Interpolated,
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
                Plugin.getLogger().error("Formula '" + getName() + "'", ex);  //$NON-NLS-1$//$NON-NLS-2$
            }
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public String getXMLContent()
    {
        final StringBuilder b = new StringBuilder();
        b.append("        <" + TAG_FORMULA + ">\n");
        addCommonXMLConfig(b);
        
        final FormulaInput[] inputs = input_variables.getInputs();
        if (inputs != null)
        {
            for (FormulaInput input : inputs)
            {
                XMLHelper.indent(b, 3); b.append("<" + TAG_INPUT + ">\n");
                XMLHelper.XML(b, 4, TAG_PV, input.getModelItem().getName());
                XMLHelper.XML(b, 4, TAG_NAME, input.getVariable().getName());
                XMLHelper.indent(b, 3); b.append("</" + TAG_INPUT + ">\n");
            }
        }
        XMLHelper.XML(b, 3, TAG_FORMULA, formula.getFormula());
        b.append("        </" + TAG_FORMULA + ">\n");
        return b.toString();
    }
    
    /** Decode XML DOM element for "formula ...".
     *  @see #getXMLContent()
     */
    public static FormulaModelItem loadFromDOM(final Model model,
                    final Element pv) throws Exception
    {
        // Common PV stuff
        final String name = DOMHelper.getSubelementString(pv, TAG_NAME);
        final int axis_index = DOMHelper.getSubelementInt(pv, TAG_AXIS, 0);
        final int line_width = DOMHelper.getSubelementInt(pv, TAG_LINEWIDTH, 0);
        final double min = DOMHelper.getSubelementDouble(pv, TAG_MIN, 0.0);
        final double max = DOMHelper.getSubelementDouble(pv, TAG_MAX, 10.0);
        final boolean visible = DOMHelper.getSubelementBoolean(pv, TAG_VISIBLE, true);
        final boolean auto_scale = DOMHelper.getSubelementBoolean(pv, TAG_AUTOSCALE);
        final int rgb[] = loadColorFromDOM(pv);
        final boolean log_scale = DOMHelper.getSubelementBoolean(pv, TAG_LOG_SCALE);
        final TraceType trace_type = loadTraceTypeFromDOM(pv);
        final FormulaModelItem item =
            new FormulaModelItem(model, name, axis_index,
                      min, max, visible, auto_scale, rgb[0], rgb[1], rgb[2],
                      line_width, trace_type, log_scale);
        
        // Get the actual formula
        Element input = DOMHelper.findFirstElementNode(
                                            pv.getFirstChild(), TAG_INPUT);
        final ArrayList<FormulaInput> inputs = new ArrayList<FormulaInput>();
        while (input != null)
        {
            final String pv_name = DOMHelper.getSubelementString(input, TAG_PV);
            final String var_name = DOMHelper.getSubelementString(input, TAG_NAME);
            final IModelItem pv_item = model.findItem(pv_name);
            inputs.add(new FormulaInput(pv_item, var_name));
            input = DOMHelper.findNextElementNode(input, TAG_INPUT);
        }
        // Convert to array        
        final FormulaInput input_array[] = new FormulaInput[inputs.size()];
        inputs.toArray(input_array);
        final String formula = DOMHelper.getSubelementString(pv, TAG_FORMULA);
        item.setFormula(formula, input_array);
        
        return item;
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
