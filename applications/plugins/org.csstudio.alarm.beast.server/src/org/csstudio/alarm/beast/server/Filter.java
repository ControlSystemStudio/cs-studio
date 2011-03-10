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
import org.csstudio.data.values.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/** Filter that computes alarm enablement from expression.
 *  <p>
 *  Example:
 *  When configured with formula
 *    "2*PV1 > PV2",
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
public class Filter implements PVListener
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
		for (int i=0; i<pvs.length; ++i)
			pvs[i] = PVFactory.createPV(variables[i].getName());
	}

	/** Start control system subscriptions */
	public void start() throws Exception
	{
		for (PV	pv : pvs)
		{
			pv.addListener(this);
			pv.start();
		}
	}

	/** Stop control system subscriptions */
	public void stop()
	{
		for (PV	pv : pvs)
		{
			pv.removeListener(this);
			pv.stop();
		}
	}

	/** Locate variable for a PV
	 *  @param pv PV
	 *  @return VariableNode that uses that PV or <code>null</code>
	 */
	private VariableNode findVariable(final PV pv)
	{
	    // pvs[] and variables[] are linked:
	    // pvs[i] is the PV for variables[i].
	    // Using linear lookup assuming there aren't many vars,
	    // and a HashMap would mostly waste memory
	    for (int i = 0; i < pvs.length; i++)
        {
            if (pv == pvs[i])
                return variables[i];
        }
		return null;
	}

	/** Evaluate filter formula with current input values */
	private void evaluate()
	{
		final double value = formula.eval();
		listener.filterChanged(value);
	}

	/** @see PVListener */
    @Override
    public void pvDisconnected(final PV pv)
	{
	    // Ignore events from 'stop()' call
	    if (!pv.isRunning())
	        return;
		final VariableNode var = findVariable(pv);
		if (var == null)
		{
            Activator.getLogger().log(Level.WARNING, "Unknown Variable {0}", pv.getName());
			return;
		}
		var.setValue(Double.NaN);
		evaluate();
	}

	/** @see PVListener */
	@Override
    public void pvValueUpdate(final PV pv)
	{
		final VariableNode var = findVariable(pv);
		if (var == null)
		{
            Activator.getLogger().log(Level.WARNING, "Unknown Variable {0}", pv.getName());
			return;
		}

        final double value = ValueUtil.getDouble(pv.getValue());
        Activator.getLogger().log(Level.FINER, "Filter {0}: {1} = {2}",
                new Object[] { formula.getFormula(), pv.getName(), value });
        var.setValue(value);
		evaluate();
	}

	/** @return String representation for debugging */
    @Override
    public String toString()
    {
        return "Filter '" + formula.getFormula() + "'";
    }
}
