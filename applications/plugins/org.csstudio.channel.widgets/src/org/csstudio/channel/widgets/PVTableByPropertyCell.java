package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;

import java.util.Collection;

import org.csstudio.utility.pvmanager.widgets.VTableDisplayCell;

public class PVTableByPropertyCell {
	
	private String rowQuery;
	private String columnQuery;
	private Collection<Channel> rowChannels;
	private Collection<Channel> columnChannels;
	private Collection<Channel> cellChannels;
	
	PVTableByPropertyCell(VTableDisplayCell cell, PVTableByPropertyWidget table) {
		// Set data for cell
		if (cell.getColumn() >= 1 && cell.getRow() >= 0) {
			cellChannels = table.getChannelsAt(cell.getRow(), cell.getColumn() - 1);
		}
		
		// Set data for column
		if (cell.getColumn() >= 1 && cell.getRow() == -1) {
			columnChannels = table.getChannelsInColumn(cell.getColumn() - 1);
		}
	}
	
	public Collection<Channel> getCellChannels() {
		return cellChannels;
	}
	
	public Collection<Channel> getColumnChannels() {
		return columnChannels;
	}
}
