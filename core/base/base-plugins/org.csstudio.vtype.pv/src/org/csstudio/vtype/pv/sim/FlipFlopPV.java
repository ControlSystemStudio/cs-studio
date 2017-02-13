/*******************************************************************************
 * Copyright (c) 2016 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.sim;

import java.util.Arrays;
import java.util.List;

import org.csstudio.vtype.pv.PV;
import org.diirt.vtype.ValueFactory;

/** Simulated PV for flipflop
 *  @author Kay Kasemir, based on similar code in org.csstudio.utility.pv and diirt
 */
@SuppressWarnings("nls")
public class FlipFlopPV extends SimulatedPV
{
    private static final List<String> labels = Arrays.asList(Boolean.FALSE.toString(), Boolean.TRUE.toString());
    private int value = 0;

    public static PV forParameters(final String name, final List<Double> parameters) throws Exception
    {
        if (parameters.size() <= 0)
            return new FlipFlopPV(name, 1);
        else if (parameters.size() == 1)
            return new FlipFlopPV(name, parameters.get(0));
        throw new Exception("sim://flipflop needs no parameters or (update_seconds)");
    }

    public FlipFlopPV(final String name, final double update_seconds)
    {
        super(name);
        start(update_seconds);
    }

    @Override
    protected void update()
    {
        value = 1 - value;
        notifyListenersOfValue(ValueFactory.newVEnum(value, labels, ValueFactory.alarmNone(), ValueFactory.timeNow()));
    }
}
