
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
	private final Table table;
	private final Composite parent;
	private final TableColumn column;
	private final int usedWidth;

	public TableColumnResizeAdapter(final Composite parent, final Table table,
			final TableColumn column) {
		this(parent, table, column, 0);
	}

	public TableColumnResizeAdapter(final Composite parent, final Table table,
			final TableColumn column, final int usedWidth) {
		this.parent = parent;
		this.table = table;
		this.column = column;
		this.usedWidth = usedWidth;
	}

	@Override
	public void controlResized(final ControlEvent e) {
		final Rectangle area = this.parent.getClientArea();
		final Point size = this.table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		final ScrollBar vBar = this.table.getVerticalBar();
		int width = area.width - this.table.computeTrim(0, 0, 0, 0).width
				- vBar.getSize().x - this.usedWidth;
		if (size.y > area.height + this.table.getHeaderHeight()) {
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			final Point vBarSize = vBar.getSize();
			width -= vBarSize.x;
		}
		final Point oldSize = this.table.getSize();
		if (oldSize.x > area.width) {
			// table is getting smaller so make the columns
			// smaller first and then resize the table to
			// match the client area width
			this.column.setWidth(width);
			this.table.setSize(area.width, area.height);
		} else {
			// table is getting bigger so make the table
			// bigger first and then make the columns wider
			// to match the client area width
			this.table.setSize(area.width, area.height);
			this.column.setWidth(width);
		}
	}
}