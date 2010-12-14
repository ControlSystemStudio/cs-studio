package org.csstudio.multichannelviewer.model;

import java.util.Collection;
import java.util.Comparator;

import org.csstudio.utility.channel.ICSSChannel;

import ca.odell.glazedlists.event.ListEventListener;

public interface IChannelGroup {	
	
	public Collection<ICSSChannel> getList();
	
	public void setGroupName(String groupName);

	public String getGroupName();
	
	public void addChannel(ICSSChannel channel);
	
	public void addChannels(Collection<ICSSChannel> channels);
	
	public void removeChannel(ICSSChannel channel);
	
	public void removeChannels(Collection<ICSSChannel> channels);
	
	public void setCompatator(Comparator<ICSSChannel> comparator);
	
	/**
	 * A method to acquire the element at a particular index on the EVENTLIST
	 * @param index
	 * @return
	 */
	public ICSSChannel getElementAtIndex(int index);
	/**
	 * Add a listener to the list of channels represented by this model, capture
	 * events generated when channels are added or removed or resorted
	 * 
	 * @param listner
	 */
	public void addEventListListener(ListEventListener<ICSSChannel> listner);

	/**
	 * Remove listener on the channel list
	 * @param listner
	 */
	public void removeEventListListener(ListEventListener<ICSSChannel> listner);

	public int getUniqueColCount();
}
