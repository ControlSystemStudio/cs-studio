package org.csstudio.multichannelviewer.model;

import gov.bnl.channelfinder.api.Channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.csstudio.multichannelviewer.GlazedSortNameComparator;
import org.csstudio.utility.channel.CSSChannelUtils;
import org.eclipse.swt.SWT;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEventListener;

public class CSSChannelGroup implements IChannelGroup {

	// channel list
	private ArrayList<Channel> rootList;
	private EventList<Channel> eventList;
	private SortedList<Channel> sortedList;

	private String groupName;

	public CSSChannelGroup(String groupName) {
		this.groupName = groupName;
		rootList = new ArrayList<Channel>();
		eventList = GlazedLists.eventList(rootList);
		sortedList = new SortedList<Channel>(eventList,
				new GlazedSortNameComparator(0, SWT.DOWN));
	}

	public CSSChannelGroup(String groupName, Collection<Channel> channels) {
		this.groupName = groupName;
		rootList = new ArrayList<Channel>(channels);
		eventList = GlazedLists.eventList(channels);
		sortedList = new SortedList<Channel>(eventList,
				new GlazedSortNameComparator(0, SWT.DOWN));
	}

	public CSSChannelGroup(String groupName, Collection<Channel> channels,
			Comparator<Channel> comparator) {
		this.groupName = groupName;
		rootList = new ArrayList<Channel>(channels);
		eventList = GlazedLists.eventList(channels);
		sortedList = new SortedList<Channel>(eventList, comparator);
	}

	/*
	 * set the sorter for the list of channels
	 * 
	 * @see
	 * org.csstudio.multichannelviewer.model.IChannelGroup#setCompatator(java
	 * .util.Comparator) TODO make this tread safe.
	 */
	public void setCompatator(Comparator<Channel> comparator) {
		sortedList.setComparator(comparator);
	}

	/**
	 * returns the comparator being used for the list
	 */
	public Comparator<? super Channel> getComparator(){
		return sortedList.getComparator();
	}
	
	@Override
	public Collection<Channel> getList() {
		if (sortedList == null)
			System.out.println("the sorted list is null");
		if (Collections.unmodifiableList(sortedList) == null)
			System.out.println("collections returns a null");
		return Collections.unmodifiableList(sortedList);
	}

	public Channel getElementAtIndex(int index) {
		return eventList.get(index);
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void addChannel(Channel channel) {
		eventList.getReadWriteLock().writeLock().lock();
		eventList.add(channel);
		eventList.getReadWriteLock().writeLock().unlock();
	}

	public void addChannels(Collection<Channel> channels) {
		eventList.getReadWriteLock().writeLock().lock();
		eventList.addAll(channels);
		eventList.getReadWriteLock().writeLock().unlock();
	}

	public void removeChannel(Channel channel) {
		eventList.getReadWriteLock().writeLock().lock();
		eventList.remove(channel);
		eventList.getReadWriteLock().writeLock().unlock();

	}

	public void removeChannels(Collection<Channel> channels) {
		eventList.getReadWriteLock().writeLock().lock();
		eventList.removeAll(channels);
		eventList.getReadWriteLock().writeLock().unlock();

	}

	/**
	 * Add a listener to the list of channels represented by this model, capture
	 * events generated when channels are added or removed or resorted
	 * 
	 * @param listner
	 */
	public void addEventListListener(ListEventListener<Channel> listner) {
		sortedList.addListEventListener(listner);
	}

	/**
	 * Remove listener on the channel list
	 * 
	 * @param listner
	 */
	public void removeEventListListener(ListEventListener<Channel> listner) {
		sortedList.removeListEventListener(listner);
	}

	/**
	 * 
	 * @return
	 */
	public int getUniqueColCount() {
		return 2
				+ CSSChannelUtils.getCSSChannelPropertyNames(sortedList).size()
				+ CSSChannelUtils.getCSSChannelTagNames(sortedList).size();
	}

}
