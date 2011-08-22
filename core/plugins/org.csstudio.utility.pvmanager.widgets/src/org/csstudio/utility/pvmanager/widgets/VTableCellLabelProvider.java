package org.csstudio.utility.pvmanager.widgets;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

public class VTableCellLabelProvider extends CellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Object value = ((VTableContentProvider.VTableRow) cell.getElement()).getValue(cell.getColumnIndex());
		cell.setText(value.toString());
		
	}
}
