/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import org.csstudio.alarm.beast.AlarmTreePV;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** Provider that links model (current alarms) to table viewer.
 *  @author Kay Kasemir
 */
public class AlarmTableContentProvider implements IStructuredContentProvider
{
    private AlarmTreePV[] alarms;
    
    /** Remember the new input.
     *  Should be the result of call to TableViewer.setInput(AlarmTreePV[])
     *  in GUI.
     */
    public void inputChanged(Viewer viewer, Object old_input, Object new_input)
    {
        alarms = (AlarmTreePV[]) new_input;
    }

    /** {@inheritDoc} */
    public Object[] getElements(Object inputElement)
    {
        return alarms;
    }

    /** {@inheritDoc} */
    public void dispose()
    {
        // Nothing to dispose
    }
 }
