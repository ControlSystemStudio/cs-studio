package org.csstudio.utility.pvmanager.widgets;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;

public class VTableToolTipSupport extends ColumnViewerToolTipSupport{
	
	private ColumnViewer viewer;
	
	protected VTableToolTipSupport(ColumnViewer viewer, int style,
			boolean manualActivation) {
		super(viewer, style, manualActivation);
		this.viewer = viewer;
	}
	
	@Override
	protected boolean shouldCreateToolTip(Event event) {
		// Since the tooltip support for table is retarted, and gives
		// the CellLabelProvide the ROW instead of, like, the -->CELL<--
		// we set the set the cell in the VTableCellLabelProvider.
		
		// Naturally, half the methods that I would need to determine what
		// cell label provider the column has are not available publicly,
		// so I just use the viewer direcly and hope for the best.
		ViewerCell cell = viewer.getCell(new Point(event.x, event.y));
		((VTableCellLabelProvider) viewer.getLabelProvider()).setCurrentCell(cell);

		return super.shouldCreateToolTip(event);
	}
	public static void enableFor(ColumnViewer viewer, int style) {
		new VTableToolTipSupport(viewer, style, false);
	}

}
