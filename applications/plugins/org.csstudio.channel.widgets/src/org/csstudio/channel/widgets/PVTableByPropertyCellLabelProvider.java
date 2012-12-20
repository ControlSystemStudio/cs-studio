package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.csstudio.utility.pvmanager.widgets.VTableCellLabelProvider;
import org.csstudio.utility.pvmanager.widgets.VTableContentProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

public class PVTableByPropertyCellLabelProvider extends VTableCellLabelProvider {
	private final List<List<Collection<Channel>>> channels;

	public PVTableByPropertyCellLabelProvider(List<List<Collection<Channel>>> channels) {
		this.channels = channels;
	}
	
	@Override
	public void update(ViewerCell cell) {
		super.update(cell);
		if (cell.getColumnIndex() > 0) {
			Collection<Channel> cellChannels = channels.get(cell.getColumnIndex() - 1).get(((VTableContentProvider.VTableRow) cell.getElement()).getRow());
			if (cellChannels.isEmpty()) {
				cell.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
				cell.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
			} else if (cellChannels.size() > 1) {
				cell.setBackground(SWTResourceManager.getColor(187,225,246));				
			}
		}
	}
	
	@Override
	public String getToolTipText(Object element, int row, int column) {
		if (column > 0) {
			Collection<Channel> cellChannels = channels.get(column - 1).get(row);
			
			// No channels in the cell, no tooltip
			// 1 channel in the cell, return the name
			// n channels in the cell, list in alphabetical order
			if (cellChannels.isEmpty()) {
				return null;
			} else if (cellChannels.size() == 1) {
				return cellChannels.iterator().next().getName();
			} else {
				List<String> channelNames = new ArrayList<String>();
				for (Channel channel : cellChannels) {
					channelNames.add(channel.getName());
				}
				Collections.sort(channelNames);
				StringBuffer buffer = new StringBuffer();
				for (String channelName : channelNames) {
					if (buffer.length() != 0)
						buffer.append("\n");
					buffer.append(channelName);
				}
				return buffer.toString();
			}
		} else {
			return null;
		}
	}
	
}
