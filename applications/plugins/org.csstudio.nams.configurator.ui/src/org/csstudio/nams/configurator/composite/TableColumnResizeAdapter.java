package org.csstudio.nams.configurator.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Setzt die Spaltenbreite einer Tabelle nach dem Style SWT.FILL
 * 
 * @author eugrei
 * 
 */
public class TableColumnResizeAdapter extends ControlAdapter {
	private Table table;
	private final Composite parent;
	private final TableColumn column;
	private final int usedWidth;

	public TableColumnResizeAdapter(Composite parent, Table table,
			TableColumn column) {
		this(parent, table, column, 0);
	}

	public TableColumnResizeAdapter(Composite parent, Table table,
			TableColumn column, int usedWidth) {
		this.parent = parent;
		this.table = table;
		this.column = column;
		this.usedWidth = usedWidth;
	}

	@Override
	public void controlResized(ControlEvent e) {
		Rectangle area = parent.getClientArea();
		Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		ScrollBar vBar = table.getVerticalBar();
		int width = area.width - table.computeTrim(0, 0, 0, 0).width
				- vBar.getSize().x - usedWidth;
		if (size.y > area.height + table.getHeaderHeight()) {
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			Point vBarSize = vBar.getSize();
			width -= vBarSize.x;
		}
		Point oldSize = table.getSize();
		if (oldSize.x > area.width) {
			// table is getting smaller so make the columns
			// smaller first and then resize the table to
			// match the client area width
			column.setWidth(width);
			table.setSize(area.width, area.height);
		} else {
			// table is getting bigger so make the table
			// bigger first and then make the columns wider
			// to match the client area width
			table.setSize(area.width, area.height);
			column.setWidth(width);
		}
	}
}