package org.csstudio.multichannelviewer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.csstudio.multichannelviewer.GlazedSortNameComparator;
import org.csstudio.utility.channel.CSSChannelUtils;
import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.swt.SWT;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEventListener;

public class CSSChannelGroup implements IChannelGroup {

	// channel list
	private ArrayList<ICSSChannel> rootList;
	private EventList<ICSSChannel> eventList;
	private SortedList<ICSSChannel> sortedList;

	private String groupName;

	public CSSChannelGroup(String groupName) {
		this.groupName = groupName;
		rootList = new ArrayList<ICSSChannel>();
		eventList = GlazedLists.eventList(rootList);
		sortedList = new SortedList<ICSSChannel>(eventList,
				new GlazedSortNameComparator(0, SWT.DOWN));
	}

	public CSSChannelGroup(String groupName, Collection<ICSSChannel> channels) {
		this.groupName = groupName;
		rootList = new ArrayList<ICSSChannel>(channels);
		eventList = GlazedLists.eventList(channels);
		sortedList = new SortedList<ICSSChannel>(eventList,
				new GlazedSortNameComparator(0, SWT.DOWN));
	}

	public CSSChannelGroup(String groupName, Collection<ICSSChannel> channels,
			Comparator<ICSSChannel> comparator) {
		this.groupName = groupName;
		rootList = new ArrayList<ICSSChannel>(channels);
		eventList = GlazedLists.eventList(channels);
		sortedList = new SortedList<ICSSChannel>(eventList, comparator);
	}

	/*
	 * set the sorter for the list of channels
	 * 
	 * @see
	 * org.csstudio.multichannelviewer.model.IChannelGroup#setCompatator(java
	 * .util.Comparator) TODO make this tread safe.
	 */
	public void setCompatator(Comparator<ICSSChannel> comparator) {
		sortedList.setComparator(comparator);
	}

	/**
	 * returns the comparator being used for the list
	 */
	public Comparator<? super ICSSChannel> getComparator(){
		return sortedList.getComparator();
	}
	
	@Override
	public Collection<ICSSChannel> getList() {
		if (sortedList == null)
			System.out.println("the sorted list is null");
		if (Collections.unmodifiableList(sortedList) == null)
			System.out.println("collections returns a null");
		return Collections.unmodifiableList(sortedList);
	}

	public ICSSChannel getElementAtIndex(int index) {
		return eventList.get(index);
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void addChannel(ICSSChannel channel) {
		eventList.getReadWriteLock().writeLock().lock();
		eventList.add(channel);
		eventList.getReadWriteLock().writeLock().unlock();
	}

	public void addChannels(Collection<ICSSChannel> channels) {
		eventList.getReadWriteLock().writeLock().lock();
		eventList.addAll(channels);
		eventList.getReadWriteLock().writeLock().unlock();
	}

	public void removeChannel(ICSSChannel channel) {
		eventList.getReadWriteLock().writeLock().lock();
		eventList.remove(channel);
		eventList.getReadWriteLock().writeLock().unlock();

	}

	public void removeChannels(Collection<ICSSChannel> channels) {
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
	public void addEventListListener(ListEventListener<ICSSChannel> listner) {
		sortedList.addListEventListener(listner);
	}

	/**
	 * Remove listener on the channel list
	 * 
	 * @param listner
	 */
	public void removeEventListListener(ListEventListener<ICSSChannel> listner) {
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
