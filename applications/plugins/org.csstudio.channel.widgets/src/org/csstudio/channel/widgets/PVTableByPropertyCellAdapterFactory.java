/**
 * 
 */
package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * @author shroffk
 * 
 */
public class PVTableByPropertyCellAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof PVTableByPropertyCell) {
			PVTableByPropertyCell cell = ((PVTableByPropertyCell) adaptableObject);

			if (adapterType == Channel[].class) {
				// If it's a cell, get the cell channels
				if (cell.getCellChannels() != null)
					return cell.getCellChannels().toArray(new Channel[cell.getCellChannels().size()]);
				
				// If it's a column, get the column channels
				if (cell.getColumnChannels() != null)
					return cell.getColumnChannels().toArray(new Channel[cell.getColumnChannels().size()]);
				
				// If it's a row, get the row channels
				if (cell.getRowChannels() != null)
					return cell.getRowChannels().toArray(new Channel[cell.getRowChannels().size()]);
				
			} else if (adapterType == ChannelQuery[].class) {
				// If it's a cell, no channel query
				if (cell.getCellChannels() != null)
					return null;
				
				// If it's a row, get the row query
				if (cell.getRowQuery() != null)
					return new ChannelQuery[] {
						ChannelQuery.Builder.query(cell.getRowQuery()).result(cell.getRowChannels(), null).create()
					};
				
				// If it's a column, get the column query
				if (cell.getColumnQuery() != null)
					return new ChannelQuery[] {
						ChannelQuery.Builder.query(cell.getColumnQuery()).result(cell.getColumnChannels(), null).create()
				    };
			} else if (adapterType == ProcessVariable.class) {
				// Only return the pv for the cell
				if (cell.getCellChannels() != null && cell.getCellChannels().size() > 0)
					return new ProcessVariable(cell.getCellChannels().iterator().next().getName());
			} else if (adapterType == ProcessVariable[].class) {
				// Only return the pv for the cell
				if (cell.getCellChannels() != null && cell.getCellChannels().size() > 0)
					return new ProcessVariable[] {new ProcessVariable(cell.getCellChannels().iterator().next().getName())};
			} else if (adapterType == ConfigurableWidget.class) {
				return null;
			}
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { Channel[].class, ChannelQuery[].class, ProcessVariable.class, ConfigurableWidget.class };
	}

}
