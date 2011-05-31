/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globaltable;

import org.csstudio.alarm.beast.ui.globalclientmodel.GlobalAlarm;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/** Base for implementing a comparator for sorting a table
 *  that sorts global alarm columns up/down.
 *  Derived class must implement the actual comparison.
 *  @author Kay Kasemir
 */
abstract public class GlobalAlarmComparator extends ViewerComparator
{
    protected boolean up;

    /** @param up Sort up or down? */
    public void setDirection(final boolean up)
    {
        this.up = up;
    }

    @Override
    final public int compare(final Viewer viewer, final Object e1, final Object e2)
    {
        final GlobalAlarm a = (GlobalAlarm) e2;
        final GlobalAlarm b = (GlobalAlarm) e1;
        final int cmp = compare(a, b);
        return up ? cmp : -cmp;
    }

    /** Compare alarms
     *  @param a
     *  @param b
     *  @return Comparison code
     */
    abstract public int compare(GlobalAlarm a, GlobalAlarm b);
}
