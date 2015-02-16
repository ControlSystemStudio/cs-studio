/**
 * 
 */
package org.csstudio.logbook.ui.extra;

import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;

/**
 * @author shroffk
 * 
 */
public class TableColumnViewerLayout extends ControlAdapter {

    private final GridViewerColumn gridViewerColumn;
    private final GridTableViewer gridTableViewer;
    private final int weight;
    private int minWidth = 100;

    public TableColumnViewerLayout(GridTableViewer gridTableViewer,
	    GridViewerColumn gridViewerColumn, int weight, int minWidth) {

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
	Point newSize = gridTableViewer.getGrid().getSize();
	int newWidth = (newSize.x * weight) / 100;
	gridViewerColumn.getColumn().setWidth(
		newWidth >= minWidth ? newWidth : minWidth);
    }
}
