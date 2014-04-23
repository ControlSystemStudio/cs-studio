package org.csstudio.logbook.ui.extra;

import org.eclipse.nebula.jface.gridviewer.GridTreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;

public class TreeColumnViewerLayout extends ControlAdapter{

    private final GridViewerColumn gridViewerColumn;
    private final GridTreeViewer gridTreeViewer;
    private final int weight;
    private int minWidth = 100;

    public TreeColumnViewerLayout(GridTreeViewer gridTreeViewer,
	    GridViewerColumn gridViewerColumn, int weight, int minWidth) {

	this.gridViewerColumn = gridViewerColumn;
	this.gridTreeViewer = gridTreeViewer;
	this.weight = weight;
	this.minWidth = minWidth;
	this.gridTreeViewer.getGrid().addControlListener(this);
    }

    @Override
    public void controlResized(ControlEvent e) {
	// TODO Auto-generated method stub
	super.controlResized(e);
	Point newSize = gridTreeViewer.getGrid().getSize();
	int newWidth = (newSize.x * weight) / 100;
	gridViewerColumn.getColumn().setWidth(
		newWidth >= minWidth ? newWidth : minWidth);
    }
}
