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
			String propertyName = table.getColumnProperty();
			String propertyValue = null;
			if (table.getColumnPropertyValues() != null) {
				propertyValue = table.getColumnPropertyValues().get(cell.getColumn() - 1);
			}
			if (propertyName != null && propertyValue != null) {
				columnQuery = table.getChannelQuery() + " " + propertyName
						+ "=" + propertyValue;
			}
		}
		
		// Set data for row
		if (cell.getRow() >= 0 ) {
			rowChannels = table.getChannelsInRow(cell.getRow());
			String propertyName = table.getRowProperty();
			String propertyValue = null;
			if (table.getRowPropertyValues() != null) {
				propertyValue = table.getRowPropertyValues().get(cell.getRow());
			}
			if (propertyName != null && propertyValue != null) {
				rowQuery = table.getChannelQuery() + " " + propertyName
						+ "=" + propertyValue;
			}
		}
	}
	
	public Collection<Channel> getCellChannels() {
		return cellChannels;
	}
	
	public Collection<Channel> getColumnChannels() {
		return columnChannels;
	}
	
	public Collection<Channel> getRowChannels() {
		return rowChannels;
	}
	
	public String getRowQuery() {
		return rowQuery;
	}
	
	public String getColumnQuery() {
		return columnQuery;
	}
}
