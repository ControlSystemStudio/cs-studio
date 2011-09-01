package org.csstudio.channel.widgets;

import java.util.List;

import org.csstudio.utility.pvmanager.widgets.VTableCellLabelProvider;
import org.csstudio.utility.pvmanager.widgets.VTableContentProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.wb.swt.SWTResourceManager;

public class PVTableByPropertyCellLabelProvider extends VTableCellLabelProvider {
	private final List<List<String>> channels;

	public PVTableByPropertyCellLabelProvider(List<List<String>> channels) {
		this.channels = channels;
	}
	
	@Override
	public void update(ViewerCell cell) {
		super.update(cell);
		if (cell.getColumnIndex() > 0) {
			String channelName = channels.get(cell.getColumnIndex() - 1).get(((VTableContentProvider.VTableRow) cell.getElement()).getRow());
			if (channelName == null) {
				cell.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
				cell.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
			}
		}
	}
	
	@Override
	public String getToolTipText(Object element, int row, int column) {
		if (column > 0) {
			return channels.get(column - 1).get(row);
		} else {
			return null;
		}
	}
	
}
