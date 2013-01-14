package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.Collection;
import java.util.Collections;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.utility.pvmanager.widgets.VTableDisplayCell;

public class PVTableByPropertyCell implements ChannelQueryAdaptable {
	
	private String query;
	private Collection<Channel> channels;
	private boolean column;
	private boolean row;
	private boolean cell;
	
	PVTableByPropertyCell(VTableDisplayCell cell, PVTableByPropertyWidget table) {
		// Set data for cell
		if (cell.getColumn() >= 1 && cell.getRow() >= 0) {
			channels = table.getChannelsAt(cell.getRow(), cell.getColumn() - 1);
			this.cell = true;
		}
		
		// Set data for column
		if (cell.getColumn() >= 1 && cell.getRow() == -1) {
			channels = table.getChannelsInColumn(cell.getColumn() - 1);
			String propertyName = table.getColumnProperty();
			String propertyValue = null;
			if (table.getColumnPropertyValues() != null) {
				propertyValue = table.getColumnPropertyValues().get(cell.getColumn() - 1);
			}
			if (propertyName != null && propertyValue != null) {
				query = table.getChannelQuery().getQuery() + " " + propertyName
						+ "=" + propertyValue;
			}
			column = true;
		}
		
		// Set data for row
		if (cell.getColumn() == 0 && cell.getRow() >= 0) {
			channels = table.getChannelsInRow(cell.getRow());
			String propertyName = table.getRowProperty();
			String propertyValue = null;
			if (table.getRowPropertyValues() != null) {
				propertyValue = table.getRowPropertyValues().get(cell.getRow());
			}
			if (propertyName != null && propertyValue != null) {
				query = table.getChannelQuery().getQuery() + " " + propertyName
						+ "=" + propertyValue;
			}
			row = true;
		}
	}
	
	public Collection<Channel> getChannels() {
		return channels;
	}
	
	public String getQuery() {
		return query;
	}
	
	public boolean isColumn() {
		return column;
	}
	
	public boolean isRow() {
		return row;
	}
	
	public boolean isCell() {
		return cell;
	}

	@Override
	public Collection<Channel> toChannels() {
		return getChannels();
	}

	@Override
	public Collection<ProcessVariable> toProcesVariables() {
		return AdaptableUtilities.toProcessVariables(toChannels());
	}

	@Override
	public Collection<ChannelQuery> toChannelQueries() {
		if (query == null)
			return null;
		return Collections.singletonList(ChannelQuery.query(getQuery()).result(getChannels(), null).build());
	}
}
