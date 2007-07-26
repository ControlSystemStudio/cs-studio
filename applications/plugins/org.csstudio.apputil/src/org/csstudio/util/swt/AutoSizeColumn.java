package org.csstudio.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/** Helper for auto-sizing columns.
 *  @see AutoSizeControlListener
 *  @author Kay Kasemir
 */
public class AutoSizeColumn
{
	int min_size;
	int weight;

    private AutoSizeColumn(int min_size, int weight)
	{
		this.min_size = min_size;
		this.weight = weight;
	}
	
    /** Create a new auto-size column.
     *  <p>
     *  The 'data' of the column will be set to the auto-size info,
     *  so applications can no longer use the 'data' for other
     *  custom purposes!
     *  
     *  @param table The table to which this column belongs.
     *  @param header The column header.
     *  @param min_size The minumum column width in pixels.
     *  @param weight The 'weight' used in getting a share of additional
     *                screen space.
     *  @return Returns the table column.
     */
	public static TableColumn make(Table table, String header,
			int min_size, int weight)
	{
		TableColumn col = new TableColumn(table, SWT.LEFT);
		col.setText(header);
		col.setData(new AutoSizeColumn(min_size, weight));
		col.setMoveable(true);
		return col;
	}
}
