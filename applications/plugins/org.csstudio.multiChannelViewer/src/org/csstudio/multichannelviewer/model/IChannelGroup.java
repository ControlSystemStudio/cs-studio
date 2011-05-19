package org.csstudio.multichannelviewer.model;

import gov.bnl.channelfinder.api.Channel;

import java.util.Collection;
import java.util.Comparator;


import ca.odell.glazedlists.event.ListEventListener;

public interface IChannelGroup {	
	
	public Collection<Channel> getList();
	
	public void setGroupName(String groupName);

	public String getGroupName();
	
	public void addChannel(Channel channel);
	
	public void addChannels(Collection<Channel> channels);
	
	public void removeChannel(Channel channel);
	
	public void removeChannels(Collection<Channel> channels);
	
	public void setCompatator(Comparator<Channel> comparator);
	
	/**
	 * A method to acquire the element at a particular index on the EVENTLIST
	 * @param index
	 * @return
	 */
	public Channel getElementAtIndex(int index);
	/**
	 * Add a listener to the list of channels represented by this model, capture
	 * events generated when channels are added or removed or resorted
	 * 
	 * @param listner
	 */
	public void addEventListListener(ListEventListener<Channel> listner);

	/**
	 * Remove listener on the channel list
	 * @param listner
	 */
	public void removeEventListListener(ListEventListener<Channel> listner);

	public int getUniqueColCount();
}
