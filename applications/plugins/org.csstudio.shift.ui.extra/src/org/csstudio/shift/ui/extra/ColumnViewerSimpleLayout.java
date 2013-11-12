/**
 * 
 */
package org.csstudio.shift.ui.extra;

import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;

public class ColumnViewerSimpleLayout extends ControlAdapter {

    private final GridViewerColumn gridViewerColumn;
    private final GridTableViewer gridTableViewer;
    private final int weight;
    private int minWidth = 100;

    public ColumnViewerSimpleLayout(final GridTableViewer gridTableViewer, final GridViewerColumn gridViewerColumn, final int weight, final int minWidth) {

		this.gridViewerColumn = gridViewerColumn;
		this.gridTableViewer = gridTableViewer;
		this.weight = weight;
		this.minWidth = minWidth;
		this.gridTableViewer.getGrid().addControlListener(this);
    }

    @Override
    public void controlResized(ControlEvent e) {
		// TODO Auto-generated method stub
		super.controlResized(e);
		final Point newSize = gridTableViewer.getGrid().getSize();
		final int newWidth = (newSize.x * weight) / 100;
		gridViewerColumn.getColumn().setWidth(newWidth >= minWidth ? newWidth : minWidth);
    }
}
