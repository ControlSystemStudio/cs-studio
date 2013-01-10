package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.api.Channel;

import java.util.Collection;
import java.util.HashSet;


public class ChannelTreeModel {
	private ChannelTreeModel parent;
	private Collection<Channel> child = new HashSet<Channel>();
	private int counter;

	public ChannelTreeModel(Collection<Channel> child) {
		this.child = child;
	}

	public ChannelTreeModel(int counter, ChannelTreeModel parent) {
		this.parent = parent;
		this.counter= counter;
	}
	
	public ChannelTreeModel getParent() {
		return parent;
	}

	public void setParent(ChannelTreeModel parent) {
		this.parent = parent;
	}

	public Collection<Channel> getChild() {
		return child;
	}

	public void setChild(Collection<Channel> child) {
		this.child = child;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}