/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.eclipse.jface.viewers.IStructuredSelection;

/** Helper for locating alarm model items in a selection
 *  @author Kay Kasemir
 */
public class SelectionHelper
{
    /** @param selection Selection that might contain alarm tree PVs
     *  @return All selected alarm tree PVs or <code>null</code>
     */
    public static AlarmTreePV[] getAlarmPVs(final IStructuredSelection selection)
    {
        final List<AlarmTreePV> pvs = new ArrayList<AlarmTreePV>();
        final Object[] sel = selection.toArray();
        for (Object obj : sel)
            if (obj instanceof AlarmTreePV)
                pvs.add((AlarmTreePV) obj);
        if (pvs.size() <= 0)
            return null;
        return pvs.toArray(new AlarmTreePV[pvs.size()]);
    }

    /** @param selection Selection that might contain alarm tree PVs
     *  @return All selected alarm tree PVs, <code>null</code>, or single object
     *          if only one is found in selection
     */
    public static Object getAlarmTreePVsForDragging(final IStructuredSelection selection)
    {
        final AlarmTreePV[] pvs = getAlarmPVs(selection);
        if (pvs != null  &&  pvs.length == 1)
            return pvs[0];
        return pvs;
    }
}
