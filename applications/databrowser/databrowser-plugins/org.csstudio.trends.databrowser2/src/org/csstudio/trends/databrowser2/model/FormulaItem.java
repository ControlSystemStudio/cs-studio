/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.apputil.formula.Formula;
import org.csstudio.apputil.formula.VariableNode;
import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.csstudio.archive.vtype.ArchiveVType;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.persistence.XMLPersistence;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VStatistics;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
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
@SuppressWarnings("nls")
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

    /** Flag to catch when a formula has been added or updated.
     *  This will cause a compute() to be called when re-evaluated
     */
    private boolean formulaChange;

    /** Samples of the formula, computed from inputs.
     *  Access must synchronize on samples (done inside PlotSampleArray) */
    private PlotSampleArray samples = new PlotSampleArray();

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
        this.formulaChange = false;
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
            this.formulaChange = true;
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
        final List<PlotSample> result = new ArrayList<PlotSample>();
        final Display display = ValueFactory.displayNone();
        // Prevent changes to formula & inputs
        synchronized (this)
        {
            // 'Current' value for each input or null when no more
            // In computation loop, values is actually moved to the _next_
            // value
            final VType values[] = new VType[inputs.length];

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
            Instant time;
            while (more_input)
            {   // Find oldest time stamp of all the inputs
                time = null;
                for (int i = 0; i < values.length; i++)
                {
                    if (values[i] == null)
                        continue;
                    final Instant sample_time = VTypeHelper.getTimestamp(values[i]);
                    if (time == null  ||  sample_time.compareTo(time) < 0)
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
                    else if (VTypeHelper.getTimestamp(values[i]).compareTo(time) <= 0)
                    {   // Input is valid before-and-up-to 'time'
                        if (values[i] instanceof VStatistics)
                        {
                            final VStatistics mmv = (VStatistics)values[i];
                            min[i] = mmv.getMin();
                            val[i] = mmv.getAverage();
                            max[i] = mmv.getMax();
                        }
                        else
                        {
                            min[i] = max[i] = Double.NaN;
                            val[i] = VTypeHelper.toDouble(values[i]);
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
                final VType value;

                if (have_min_max)
                {   // Set variables[] from min
                    for (int i = 0; i < values.length; i++)
                        variables[i].setValue(min[i]);
                    final double res_min = formula.eval();
                    // Set variables[] from max
                    for (int i = 0; i < values.length; i++)
                        variables[i].setValue(max[i]);
                    final double res_max = formula.eval();
                    value = new ArchiveVStatistics(time, AlarmSeverity.NONE, Messages.Formula,
                            display, res_val, res_min, res_max, 0.0, 1);
                }
                else
                {   // No min/max.
                    if (Double.isNaN(res_val))
                        value = new ArchiveVNumber(time, AlarmSeverity.INVALID, Messages.Formula,
                                    display, res_val);
                    else
                        value = new ArchiveVNumber(time, AlarmSeverity.NONE, ArchiveVType.STATUS_OK,
                                    display, res_val);
                }
                result.add(new PlotSample(Messages.Formula, value));
            }
        }
        // Update PlotSamples
        samples.set(result);
    }

    /** Re-evaluate the formula in case some of the input samples changed.
     *  @return <code>true</code> if it indeed re-evaluated,
     *          <code>false</code> if we assume there is no need to do anything.
     */
    public boolean reevaluate()
    {
        boolean anything_new = false;
        boolean formula_change = false;
        // Prevent changes to inputs array
        synchronized (this)
        {
            formula_change = this.formulaChange;
            // Set the flag for a formula change back to false if it was positive
            // as we have now registered it
            if (this.formulaChange)
                this.formulaChange = false;
            for (FormulaInput input : inputs)
                if (input.hasNewSamples())
                {
                    anything_new = true;
                    break;
                }
        }
        if (!anything_new & !formula_change)
            return false;
        // Formula and inputs could actually change right now,
        // but we're about to re-compute anyway, and we'll lock while
        // doing that
        compute();
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public PlotSamples getSamples()
    {
        return samples;
    }

    /** Write XML formatted PV configuration
     *  @param writer PrintWriter
     */
    @Override
    synchronized public void write(final PrintWriter writer)
    {
        XMLWriter.start(writer, 2, XMLPersistence.TAG_FORMULA);
        writer.println();
        writeCommonConfig(writer);
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_FORMULA, formula.getFormula());
        for (FormulaInput input : inputs)
        {
            XMLWriter.start(writer, 3, XMLPersistence.TAG_INPUT);
            writer.println();
            XMLWriter.XML(writer, 4, XMLPersistence.TAG_PV, input.getItem().getName());
            XMLWriter.XML(writer, 4, XMLPersistence.TAG_NAME, input.getVariableName());
            XMLWriter.end(writer, 3, XMLPersistence.TAG_INPUT);
            writer.println();
        }
        XMLWriter.end(writer, 2, XMLPersistence.TAG_FORMULA);
        writer.println();
    }

    /** Create FormulaItem from XML document
     *  @param model Model that's used to locate inputs to the formula
     *  @param node Node in DOM for this formula configuration
     *  @return FormulaItem
     *  @throws Exception on error
     */
    public static FormulaItem fromDocument(final Model model, final Element node) throws Exception
    {
        final String name = DOMHelper.getSubelementString(node, XMLPersistence.TAG_NAME);
        final String expression = DOMHelper.getSubelementString(node, XMLPersistence.TAG_FORMULA);
        // Get inputs
        final ArrayList<FormulaInput> inputs = new ArrayList<FormulaInput>();
        Element input = DOMHelper.findFirstElementNode(node.getFirstChild(), XMLPersistence.TAG_INPUT);
        while (input != null)
        {
            final String pv = DOMHelper.getSubelementString(input, XMLPersistence.TAG_PV);
            final String var = DOMHelper.getSubelementString(input, XMLPersistence.TAG_NAME);
            final ModelItem item = model.getItem(pv);
            if (item == null)
                throw new Exception("Formula " + name + " refers to unknown input " + pv);
            inputs.add(new FormulaInput(item, var));
            input = DOMHelper.findNextElementNode(input, XMLPersistence.TAG_INPUT);
        }
        // Create model item, parse common properties
        final FormulaItem formula = new FormulaItem(name, expression, (FormulaInput[]) inputs.toArray(new FormulaInput[inputs.size()]));
        formula.configureFromDocument(model, node);
        return formula;
    }
}
