/**
 * 
 */
package org.csstudio.logbook.ui.extra;

import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;

/**
 * @author shroffk
 * 
 */
public class ColumnViewerWeightedLayout extends ControlAdapter {

    private final GridViewerColumn gridViewerColumn;
    private final GridTableViewer gridTableViewer;
    private final int weight;
    private int minWidth = 100;
    private Point oldSize = new Point(0, 0);

    public ColumnViewerWeightedLayout(GridTableViewer gridTableViewer,
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
	int newWidth = gridViewerColumn.getColumn().getWidth()
		+ (weight * (newSize.x - oldSize.x) / 100);
	gridViewerColumn.getColumn().setWidth(
		newWidth >= minWidth ? newWidth : minWidth);
	oldSize = newSize;
    }
}
