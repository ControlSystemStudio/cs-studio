package org.csstudio.channel.widgets;

import java.util.List;

import org.csstudio.utility.pvmanager.widgets.VTableCellLabelProvider;

public class PVTableByPropertyCellLabelProvider extends VTableCellLabelProvider {
	private final List<List<String>> channels;

	public PVTableByPropertyCellLabelProvider(List<List<String>> channels) {
		this.channels = channels;
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
