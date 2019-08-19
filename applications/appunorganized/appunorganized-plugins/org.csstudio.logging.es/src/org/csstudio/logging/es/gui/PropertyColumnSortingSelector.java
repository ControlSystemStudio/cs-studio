/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es.gui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Listener to table column selections, sorting the table on the selected col.
 *
 * @author Kay Kasemir
 */
public class PropertyColumnSortingSelector extends SortingColumnSelector
{
    final private String property;

    /**
     * Constructor
     *
     * @param table_viewer
     *            Table Viewer
     * @param column
     *            Column to sort
     * @param property
     *            Property handled by column
     */
    public PropertyColumnSortingSelector(final TableViewer table_viewer,
            final TableColumn column, final String property)
    {
        super(table_viewer, column);
        this.property = property;
    }

    /** {@inheritDoc} */
    @Override
    protected void sort(boolean up)
    {
        this.table_viewer.setComparator(
                new MessagePropertyComparator(this.property, up));
    }
}
