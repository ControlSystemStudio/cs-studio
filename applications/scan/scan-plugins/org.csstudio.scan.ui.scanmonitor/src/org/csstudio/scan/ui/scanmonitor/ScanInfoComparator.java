/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scanmonitor;

import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanState;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

/** Comparator for sorting rows in scan monitor
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanInfoComparator extends ViewerComparator
{
    private int direction = SWT.UP;
    private int column = 0;

    /** Set column on which to sort.
     *
     *  <p>When setting the same column again,
     *  change the sort direction of that column.
     *  @param column
     */
    public void setColumn(final int column)
    {
        if (this.column == column)
            direction = direction == SWT.DOWN ? SWT.UP : SWT.DOWN;
        else
            this.column = column;
    }

    /** @return SWT.UP or SWT.DOWN */
    public int getDirection()
    {
        return direction;
    }

    @Override
    public int compare(final Viewer viewer, final Object e1, final Object e2)
    {
        final ScanInfo one = (ScanInfo) e1,
                       other = (ScanInfo) e2;
        int cmp = 0;
        if (column == 1) // Time
            cmp = one.getCreated().compareTo(other.getCreated());
        else if (column == 2) // Name
            cmp = one.getName().compareTo(other.getName());
        else if (column == 3) // State
            cmp = Integer.compare(rankState(one.getState()), rankState(other.getState()));
        else if (column == 4) // Percent
            cmp = one.getCreated().compareTo(other.getCreated());
        else if (column == 5) // Runtime
            cmp = Long.compareUnsigned(one.getRuntimeMillisecs(), other.getRuntimeMillisecs());
        else if (column == 6) // Finish
        {
            if (one.getFinishTime() != null  &&  other.getFinishTime() != null)
                cmp = one.getFinishTime().compareTo(other.getFinishTime());
        }
        else if (column == 7) // Current Command
        {
            if (one.getCurrentCommand() != null  &&  other.getCurrentCommand() != null)
                cmp = one.getCurrentCommand().compareTo(other.getCurrentCommand());
        }
        else if (column == 8) // Error
            cmp = one.getError().orElse("").compareTo(other.getError().orElse(""));

        // Fall back to ID if none of the other columns,
        // or if that column has equal values
        if (cmp == 0)
            cmp = Long.compare(one.getId(), other.getId());
        return (direction == SWT.DOWN) ? cmp : -cmp;
    }

    /** Rank states
     *
     *  <p>Assumes that 'Running' is most interesting and 'Logged' is at the bottom
     *
     *  <p>Intermediate states are ranked somewhat arbitrarily
     *
     *  @param state ScanState
     *  @return
     */
    private int rankState(final ScanState state)
    {
        switch (state)
        {
        case Running: // Most important, happening right now
            return 6;
        case Paused:  // Very similar to a running state
            return 5;
        case Idle:    // About to run next
            return 4;
        case Failed:  // Of the not running ones, failure is important to know
            return 3;
        case Aborted: // Aborted on purpose
            return 2;
        case Finished:// Water down the bridge
            return 1;
        case Logged:
        default:
            return 0;
        }
    }
}
