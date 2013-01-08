/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import static org.epics.pvmanager.vtype.ExpressionLanguage.vType;
import static org.epics.util.time.TimeDuration.ofSeconds;

import java.util.logging.Level;

import org.csstudio.apputil.formula.Formula;
import org.csstudio.apputil.formula.VariableNode;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
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
public class Filter
{
    /** Timeout for PV connections */
    public static final int TIMEOUT_SECS = 30;

    /** Listener to notify when the filter computes a new value */
    final private FilterListener listener;

    /** Formula to evaluate */
    final private Formula formula;

    /** Variables used in the formula. May be [0], but never null */
    final private VariableNode[] variables;

    /** This array is linked to <code>variables</code>:
     *  Same size, and there's a PV for each VariableNode.
     */
    final private PVReader<VType> pvs[];

    private double previous_value = Double.NaN;

    /** Initialize
     *  @param filter_expression Formula that might contain PV names
     *  @throws Exception on error
     */
    @SuppressWarnings("unchecked")
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
        
        pvs = (PVReader<VType>[]) new PVReader[variables.length];
    }

    /** Start control system subscriptions */
    public void start() throws Exception
    {
        // Note:
        // Could use
        //   PVManager.read(listOf(latestValueOf(vTypes(pv_names)))).listeners(pvlistener).timeout(ofSeconds(TIMEOUT_SECS)).maxRate(ofSeconds(0.5));
        // to read all variables as a list, but then no idea which individual PV is causing an error
        // in case of disconnects.
        // Could also try the PVManager's formula support, but already had a formula package...
        for (int i=0; i<pvs.length; ++i)
        {
        	final VariableNode variable = variables[i];
            final PVReaderListener<VType> pvlistener = new PVReaderListener<VType>()
            {
                @Override
                public void pvChanged(final PVReaderEvent<VType> event)
                {
                    final PVReader<VType> pv = event.getPvReader();
                    final Exception error = pv.lastException();
                    if (error != null)
                    {
                        Activator.getLogger().log(Level.WARNING, "Error from PV " + pv.getName() + " (var. " + variable.getName() + ")", error);
                        variable.setValue(Double.NaN);
                    }
                    else
                    {
                        final double value = VTypeHelper.toDouble(pv.getValue());
                        Activator.getLogger().log(Level.FINER, "Filter {0}: {1} = {2}",
                                new Object[] { formula.getFormula(), pv.getName(), value });
                        variable.setValue(value);
                    }
                    evaluate();
                }
            };
            pvs[i] = PVManager.read(vType(variables[i].getName())).readListener(pvlistener).timeout(ofSeconds(TIMEOUT_SECS)).maxRate(ofSeconds(0.5));
        }
    }

    /** Stop control system subscriptions */
    public void stop()
    {
        for (PVReader<VType> pv : pvs)
            pv.close();
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
