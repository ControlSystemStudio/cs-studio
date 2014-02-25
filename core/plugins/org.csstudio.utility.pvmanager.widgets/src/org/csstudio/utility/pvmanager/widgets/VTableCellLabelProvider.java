package org.csstudio.utility.pvmanager.widgets;


import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.epics.util.time.Timestamp;
import org.epics.util.time.TimestampFormat;
import org.epics.vtype.ValueUtil;

public class VTableCellLabelProvider extends CellLabelProvider {
	
	private ViewerCell currentCell;

	@Override
	public void update(ViewerCell cell) {
		Object value = ((VTableContentProvider.VTableRow) cell.getElement()).getValue(cell.getColumnIndex());
	 	if (value == null || (value instanceof Double && ((Double) value).isNaN())) {
	 		cell.setText(""); 
	 	} else if (value instanceof Timestamp){
	 		cell.setText(ValueUtil.getDefaultTimestampFormat().format(value));
	 	} else {
	 		cell.setText(value.toString());
	 	}
 	}
	
	public void setCurrentCell(ViewerCell currentCell) {
		this.currentCell = currentCell;
	}
	
	public ViewerCell getCurrentCell() {
		return currentCell;
	}
	
	@Override
	public String getToolTipText(Object element) {
		return getToolTipText(element, ((VTableContentProvider.VTableRow) getCurrentCell().getElement()).getRow(),
				getCurrentCell().getColumnIndex());
	}
	
	public String getToolTipText(Object element, int row, int column) {
		return super.getToolTipText(element);
	}

}
