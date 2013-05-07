/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.model;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.csstudio.apputil.formula.Formula;
import org.csstudio.apputil.formula.VariableNode;
import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.trends.sscan.Messages;
import org.w3c.dom.Element;

/** A {@link Model} item that implements a formula.
 *  <p>
 *  The 'name' is mostly used internally to identify the
 *  formula when it's for example used as input to another formula.
 *  The 'display_name' could be another human-readable name that's
 *  used in the plot.
 *  The 'formula' is the actual formula (expression, input variables).
 *
 *  @author Kay Kasemir
 */
public class FormulaItem extends ModelItem
{
    /** Evaluate-able Formula
     *  <p>
     *  The formula as well as inputs and variables can be changed
     *  from the GUI thread via updateFormula().
     *  Concurrently, an update thread can invoke reevaluate().
     *  All access to <code>formula</code>, <code>inputs</code>,
     *  <code>variables</code> must therefore synchronize on <code>this</code>.
     */
    private Formula formula;

    /** Input elements to the formula
     *  @see #formula for synchronization
     */
    private FormulaInput inputs[];

    /** Variable nodes.
     *  Array elements correspond to entries in <code>inputs[]</code>
     *  @see #formula for synchronization
     */
    private VariableNode variables[];

    /** Samples of the formula, computed from inputs.
     *  Access must synchronize on samples (done inside PlotSampleArray) */
    //final private PlotSampleArray samples = new PlotSampleArray();

    /** Initialize formula
     *  @param name Name of the Formula item
     *  @param expression Expression to evaluate
     *  @param inputs Inputs to expression
     *  @throws Exception on error, including parse error in expression
     */
    public FormulaItem(final String name, final String expression,
            final FormulaInput inputs[]) throws Exception
    {
        super(name);
        updateFormula(expression, inputs);
        // Compute initial values
        compute();
    }

    /** @return Expression */
    public String getExpression()
    {
        return formula.getFormula();
    }

    /** @return inputs that are currently used in the formula */
    public FormulaInput[] getInputs()
    {
        return inputs;
    }

    /** Check if formula uses given input
     *  @param item ModelItem potentially used in formula
     *  @return <code>true</code> if used as input
     */
    synchronized public boolean usesInput(final ModelItem item)
    {
        for (FormulaInput input : inputs)
            if (input.getItem() == item)
                return true;
        return false;
    }

    /** Set input items, create VariableNodes for them, set the formula
     *  @param expression Formula expression
     *  @param inputs Inputs to formula
     *  @throws Exception on error in expression
     */
    public void updateFormula(final String expression,
            final FormulaInput inputs[]) throws Exception
    {
        // Prevent compute() from using inconsistent formula & inputs
        synchronized (this)
        {
            this.inputs = inputs;
            variables = new VariableNode[inputs.length];
            for (int i=0; i<variables.length; ++i)
                variables[i] = new VariableNode(inputs[i].getVariableName());
            this.formula = new Formula(expression, variables);
        }
        fireItemLookChanged();
    }

    /** Evaluate formula for each input sample
     *  <p>
     *  Iterates over the input samples in a manner of spreadsheet or
     *  staircase-interpolation: An input with a time stamp is valid
     *  until there's a sample with a greater time stamp.
     */
    private void compute()
    {
        final ArrayList<IValue> result = new ArrayList<IValue>();
        final String status = "ok";
        // Prevent changes to formula & inputs
        synchronized (this)
        {
            // 'Current' value for each input or null when no more
            // In computation loop, values is actually moved to the _next_
            // value
            final IValue values[] = new IValue[inputs.length];

            // 'Current' numeric min/val/max of values
            final double min[] = new double[inputs.length];
            final double val[] = new double[inputs.length];
            final double max[] = new double[inputs.length];

            // Determine first sample for each input
            boolean more_input = false;
            for (int i = 0; i < values.length; i++)
            {
                // Initially, none have any data
                min[i] = val[i] = max[i] = Double.NaN;
                // Is there an initial value for any input?
                values[i] = inputs[i].first();
                if (values[i] != null)
                    more_input = true;
            }

            // Compute result for each 'line in the spreadsheet'
            ITimestamp time;
            while (more_input)
            {   // Find oldest time stamp of all the inputs
                time = null;
                for (int i = 0; i < values.length; i++)
                {
                    if (values[i] == null)
                        continue;
                    final ITimestamp sample_time = values[i].getTime();
                    if (time == null  ||  sample_time.isLessThan(time))
                        time = sample_time;
                }
                if (time == null)
                {   // No input left with any data
                    more_input = false;
                    break;
                }

                // 'time' now defines the current spreadsheet line.
                // Set min/max/val to sample from each input for that time.
                // This might move values[i] resp. the inputs' iterators
                // to the 'next' sample
                boolean have_min_max = true;
                for (int i = 0; i < values.length; i++)
                {
                    if (values[i] == null) // No more data
                    {
                        min[i] = val[i] = max[i] = Double.NaN;
                        have_min_max = false;
                    }
                    else if (values[i].getTime().isLessOrEqual(time))
                    {   // Input is valid before-and-up-to 'time'
                        if (values[i] instanceof IMinMaxDoubleValue)
                        {
                            final IMinMaxDoubleValue mmv = (IMinMaxDoubleValue)values[i];
                            min[i] = mmv.getMinimum();
                            val[i] = mmv.getValue();
                            max[i] = mmv.getMaximum();
                        }
                        else
                        {
                            min[i] = max[i] = Double.NaN;
                            val[i] = ValueUtil.getDouble(values[i]);
                            // Use NaN for any non-number
                            if (Double.isInfinite(val[i]))
                                val[i] = Double.NaN;
                            have_min_max = false;
                        }
                        // Move to next input sample
                        values[i] = inputs[i].next();
                    }
                    else
                    {   // values[i].getTime() > time, so leave min/max/val[i]
                        // as is until 'time' catches up with the next input sample.
                        // Just update the have_min_max flag
                        if (Double.isNaN(min[i])  ||  Double.isNaN(max[i]))
                            have_min_max = false;
                    }
                }

                // Set variables[] from val to get res_val
                for (int i = 0; i < values.length; i++)
                    variables[i].setValue(val[i]);
                // Evaluate formula for these inputs
                final double res_val = formula.eval();
                final IValue value;

                if (have_min_max)
                {   // Set variables[] from min
                    for (int i = 0; i < values.length; i++)
                        variables[i].setValue(min[i]);
                    final double res_min = formula.eval();
                    // Set variables[] from max
                    for (int i = 0; i < values.length; i++)
                        variables[i].setValue(max[i]);
                    final double res_max = formula.eval();
                    value = ValueFactory.createMinMaxDoubleValue(time,
                            null, status, null,
                            IValue.Quality.Interpolated,
                            new double[] { res_val }, res_min, res_max);
                }
                else
                {   // No min/max.
                    value = ValueFactory.createDoubleValue(time,
                            null, status, null,
                            IValue.Quality.Interpolated,
                            new double[] { res_val });
                }
                result.add(value);
            }
        }
        // Convert numbers into PlotSamples
        //samples.set(Messages.Formula, result);
    }


    //TODO: return this getSamples instead of parent ... when working
    ///** {@inheritDoc} */
   // @Override
   // public PlotSamples getSamples()
   // {
   //     return samples;
   // }

    /** Write XML formatted PV configuration
     *  @param writer PrintWriter
     */
    @Override
    synchronized public void write(final PrintWriter writer)
    {
        XMLWriter.start(writer, 2, Model.TAG_FORMULA);
        writer.println();
        writeCommonConfig(writer);
        XMLWriter.XML(writer, 3, Model.TAG_FORMULA, formula.getFormula());
        for (FormulaInput input : inputs)
        {
            XMLWriter.start(writer, 3, Model.TAG_INPUT);
            writer.println();
            XMLWriter.XML(writer, 4, Model.TAG_PV, input.getItem().getName());
            XMLWriter.XML(writer, 4, Model.TAG_NAME, input.getVariableName());
            XMLWriter.end(writer, 3, Model.TAG_INPUT);
            writer.println();
        }
        XMLWriter.end(writer, 2, Model.TAG_FORMULA);
        writer.println();
    }

    /** Create FormulaItem from XML document
     *  @param model Model that's used to locate inputs to the formula
     *  @param node Node in DOM for this formula configuration
     *  @return FormulaItem
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public static FormulaItem fromDocument(final Model model, final Element node) throws Exception
    {
        final String name = DOMHelper.getSubelementString(node, Model.TAG_NAME);
        final String expression = DOMHelper.getSubelementString(node, Model.TAG_FORMULA);
        // Get inputs
        final ArrayList<FormulaInput> inputs = new ArrayList<FormulaInput>();
        Element input = DOMHelper.findFirstElementNode(node.getFirstChild(), Model.TAG_INPUT);
        while (input != null)
        {
            final String pv = DOMHelper.getSubelementString(input, Model.TAG_PV);
            final String var = DOMHelper.getSubelementString(input, Model.TAG_NAME);
            final ModelItem item = model.getItem(pv);
            if (item == null)
                throw new Exception("Formula " + name + " refers to unknown input " + pv);
            inputs.add(new FormulaInput(item, var));
            input = DOMHelper.findNextElementNode(input, Model.TAG_INPUT);
        }
        // Create model item, parse common properties
        final FormulaItem formula = new FormulaItem(name, expression, (FormulaInput[]) inputs.toArray(new FormulaInput[inputs.size()]));
        formula.configureFromDocument(model, node);
        return formula;
    }
}
