/**
 * 
 */
package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

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
			} else if (adapterType == ChannelQuery[].class) {
				return null;
			} else if (adapterType == ConfigurableWidget.class) {
				return null;
			}
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { Channel[].class, ChannelQuery[].class, ConfigurableWidget.class };
	}

}
