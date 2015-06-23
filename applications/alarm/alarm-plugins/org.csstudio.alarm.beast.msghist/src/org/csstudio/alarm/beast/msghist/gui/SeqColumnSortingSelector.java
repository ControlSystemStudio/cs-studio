/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;

/** Listener to table column selections,
 *  sorting the table on message sequence number.
 *  @author Kay Kasemir
 */
public class SeqColumnSortingSelector extends SortingColumnSelector
{
    public SeqColumnSortingSelector(final TableViewer table_viewer,
            final TableColumn column)
    {
        super(table_viewer, column);
    }

    @Override
    protected void sort(boolean up)
    {
        table_viewer.setComparator(new MessageSeqComparator(up));
    }
}
