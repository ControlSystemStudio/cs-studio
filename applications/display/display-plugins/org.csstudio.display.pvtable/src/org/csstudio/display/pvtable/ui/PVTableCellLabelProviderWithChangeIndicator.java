/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.model.PVTableItem;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

/**
 * Cell label provider for PV Table that handles the tool tip and indicates a
 * changed item
 *
 * @author Kay Kasemir
 */
public class PVTableCellLabelProviderWithChangeIndicator extends StyledCellLabelProvider {
    final private Color changed_background;

    public PVTableCellLabelProviderWithChangeIndicator(final Color changed_background) {
        super(COLORS_ON_SELECTION);
        this.changed_background = changed_background;
    }

    @Override
    public String getToolTipText(final Object element) {
        return PVTableCellLabelProvider.getTableItemTooltip((PVTableItem) element);
    }

    @Override
    public void update(final ViewerCell cell) {
        final String text = cell.getText();
        final PVTableItem item = (PVTableItem) cell.getElement();
        if (item.isChanged())
            cell.setStyleRanges(new StyleRange[] { new StyleRange(0, text.length(), null, changed_background) });
        else
            cell.setStyleRanges(null);

        super.update(cell);
    }
}
