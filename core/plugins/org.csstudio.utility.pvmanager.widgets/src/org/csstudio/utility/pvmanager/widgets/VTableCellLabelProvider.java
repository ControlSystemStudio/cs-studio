package org.csstudio.utility.pvmanager.widgets;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

public class VTableCellLabelProvider extends CellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Object value = ((VTableContentProvider.VTableRow) cell.getElement()).getValue(cell.getColumnIndex());
		if (value == null || (value instanceof Double && ((Double) value).isNaN())) {
			cell.setText("");
		} else {
			cell.setText(value.toString());
		}
		
	}
}
