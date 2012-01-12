/**
 * 
 */
package org.csstudio.channel.widgets;

import java.util.Collection;
import java.util.Iterator;

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
				if (cell.getChannels() != null && cell.getChannels().size() > 0)
					return cell.getChannels().toArray(new Channel[cell.getChannels().size()]);
				
			} else if (adapterType == ChannelQuery[].class) {
				if (cell.getQuery() != null)
					return new ChannelQuery[] {
						ChannelQuery.query(cell.getQuery()).result(cell.getChannels(), null).build()
					};
				
			} else if (adapterType == ProcessVariable[].class) {
				if (cell.getChannels() != null && cell.getChannels().size() > 0)
					return toPVArray(cell.getChannels());
				
			} else if (adapterType == ConfigurableWidget.class) {
				return null;
			}
		}
		return null;
	}
	
	// TODO: this should go in a utility class
	public ProcessVariable[] toPVArray(Collection<Channel> channels) {
		if (channels == null)
			return null;
		
		ProcessVariable[] result = new ProcessVariable[channels.size()];
		int i = 0;
		for (Channel channel : channels) {
			result[i] = new ProcessVariable(channel.getName());
			i++;
		}
		return result;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { Channel[].class, ChannelQuery[].class, ProcessVariable.class, ProcessVariable[].class, ConfigurableWidget.class };
	}

}
