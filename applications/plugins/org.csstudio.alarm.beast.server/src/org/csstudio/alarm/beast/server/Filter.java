/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.util.logging.Level;

import org.csstudio.apputil.formula.Formula;
import org.csstudio.apputil.formula.VariableNode;
import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListener;
import org.csstudio.vtype.pv.PVListenerAdapter;
import org.csstudio.vtype.pv.PVPool;
import org.epics.vtype.VType;

/** Filter that computes alarm enablement from expression.
 *  <p>
 *  Example:
 *  When configured with formula
 *  <pre>2*PV1 > PV2</pre>
 *  Filter will subscribe to PVs "PV1" and "PV2".
 *  For each value change in the input PVs, the formula is
 *  evaluated and the listener is notified of the result.
 *  <p>
 *  When subscribing to PVs, note that the filter uses the same
 *  mechanism as the alarm server, i.e. when the EPICS plug-in
 *  is configured to use 'alarm' subscriptions, the filter PVs
 *  will also only send updates when their alarm severity changes,
 *  NOT for all value changes.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Filter extends PVListenerAdapter
{
    /** Listener to notify when the filter computes a new value */
    final private FilterListener listener;

    /** Formula to evaluate */
    final private Formula formula;

    /** Variables used in the formula. May be [0], but never null */
    final private VariableNode[] variables;

    /** This array is linked to <code>variables</code>:
     *  Same size, and there's a PV for each VariableNode.
     */
    final private PV pvs[];

    private double previous_value = Double.NaN;

    /** Initialize
     *  @param filter_expression Formula that might contain PV names
     *  @throws Exception on error
     */
    public Filter(final String filter_expression,
            final FilterListener listener) throws Exception
    {
        this.listener = listener;
        formula = new Formula(filter_expression, true);
        final VariableNode vars[] = formula.getVariables();
        if (vars == null)
            variables = new VariableNode[0];
        else
            variables = vars;
        
        pvs = new PV[variables.length];
    }

    /** Start control system subscriptions */
    public void start() throws Exception
    {
        for (int i=0; i<pvs.length; ++i)
        {
            pvs[i] = PVPool.getPV(variables[i].getName());
            pvs[i].addListener(this);
        }
    }

    /** Stop control system subscriptions */
    public void stop()
    {
        for (int i=0; i<pvs.length; ++i)
        {
            pvs[i].removeListener(this);
            PVPool.releasePV(pvs[i]);
            pvs[i] = null;
        }
    }
    
    /** @param pv PV used by the formula
     *  @return Associated variable node
     */
    private VariableNode findVariableForPV(final PV pv)
    {
        for (int i=0; i<pvs.length; ++i) // Linear, assuming there are just a few PVs in one formula
            if (pvs[i] == pv)
                return variables[i];
        Activator.getLogger().log(Level.WARNING, "Got update for PV {0} that is not assigned to variable", pv.getName());
        return null;
    }

    /** @see PVListener */
    @Override
    public void valueChanged(final PV pv, final VType value)
    {
        final VariableNode variable = findVariableForPV(pv);
        if (variable == null)
            return;
        final double number = VTypeHelper.toDouble(value);
        Activator.getLogger().log(Level.FINER, "Filter {0}: {1} = {2}",
                new Object[] { formula.getFormula(), pv.getName(), number });
        variable.setValue(number);
        evaluate();
    }

    /** @see PVListener */
    @Override
    public void disconnected(final PV pv)
    {
        final VariableNode variable = findVariableForPV(pv);
        if (variable == null)
            return;
        Activator.getLogger().log(Level.WARNING, "PV " + pv.getName() + " (var. " + variable.getName() + ") disconnected");
        variable.setValue(Double.NaN);
        evaluate();
    }

    /** Evaluate filter formula with current input values */
    private void evaluate()
    {
        final double value = formula.eval();
        // Only update on _change_, not whenever inputs send an update
        synchronized (this)
        {
            if (previous_value == value)
                return;
            previous_value  = value;
        }
        listener.filterChanged(value);
    }

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        return "Filter '" + formula.getFormula() + "'";
    }
}
