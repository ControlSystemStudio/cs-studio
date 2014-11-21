/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;

/** TableColumnLayout that enforces a minimum column width
 *
 *  <p>Especially on Linux this avoids the "vanishing column"
 *  issue when users resize a column to nothing and then moan
 *  because it's gone.
 *
 *  @author Kay Kasemir
 */
public class MinSizeTableColumnLayout extends TableColumnLayout
{
    final private int min_width;

    public MinSizeTableColumnLayout()
    {
        this(10);
    }

    public MinSizeTableColumnLayout(int min_width)
    {
        this.min_width = min_width;
    }

    @Override
    protected void updateColumnData(final Widget column)
    {
        final TableColumn tColumn = (TableColumn) column;
        if (tColumn.getWidth() > min_width)
            // Delegate to default implementation
            super.updateColumnData(column);
        else
        {   // Enforce minimum width
            tColumn.setWidth(min_width);
            layout(tColumn.getParent().getParent(), true);
        }
     }

}
