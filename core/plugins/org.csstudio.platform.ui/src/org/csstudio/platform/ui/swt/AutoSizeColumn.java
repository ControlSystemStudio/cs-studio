package org.csstudio.platform.ui.swt;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/** Helper for auto-sizing columns.
 *  @see AutoSizeControlListener
 *  @author Kay Kasemir
 *  @deprecated Use org.eclipse.jface.layout.TableColumnLayout, see http://javafact.com/2010/07/26/working-with-jface-tableviewer/
 */
@Deprecated
public class AutoSizeColumn
{
    /** Create a new auto-size table column.
     *  <p>
     *  The 'data' of the column will be set to the auto-size info,
     *  so applications can no longer use the 'data' for other
     *  custom purposes!
     *
     *  @param table_viewer The table viewer to which this column belongs.
     *  @param header The column header.
     *  @param min_size The minimum column width in pixels.
     *  @param weight The 'weight' used in getting a share of additional
     *                screen space.
     *                Suggested values are 100 for "normal",
     *                then higher or lower values to get more/less
     *                of the available space beyond the 'min_size'.
     *  @param center Center-align the column?
     *  @return Table viewer column.
     *  @since 2008-06-24
     */
    public static TableViewerColumn make(final TableViewer table_viewer,
            final String header, final int min_size, final int weight,
            final boolean center)
    {
        final TableViewerColumn view_col =
            new TableViewerColumn(table_viewer, center ? SWT.CENTER : SWT.LEFT);
        final TableColumn col = view_col.getColumn();
        col.setText(header);
        col.setMoveable(true);
        col.setWidth(min_size);
        col.setData(new ColumnWeightData(weight, min_size, true));
        return view_col;
    }

    /** @see #make(TableViewer, String, int, int, boolean)
     *  @since 2008-06-24
     */
    public static TableViewerColumn make(final TableViewer table_viewer,
            final String header, final int min_size, final int weight)
    {
        return make(table_viewer, header, min_size, weight, false);
    }

    /** Create a new auto-size column.
     *  @see #make(TableViewer, String, int, int, boolean)
     */
	public static TableColumn make(Table table, String header,
			int min_size, int weight, boolean center)
	{
		TableColumn col = new TableColumn(table, SWT.LEFT);
		col.setText(header);
        col.setMoveable(true);
        col.setWidth(min_size);
        col.setData(new ColumnWeightData(weight, min_size, true));
        if (center)
            col.setAlignment(SWT.CENTER);
		return col;
	}

    /** Create a new auto-size column.
     *  @see #make(TableViewer, String, int, int, boolean)
     */
    public static TableColumn make(Table table, String header,
            int min_size, int weight)
    {
        return make(table, header, min_size, weight, false);
    }
}
