package org.csstudio.utility.channel.actions;

import java.util.Collection;
import java.util.HashSet;

import org.csstudio.utility.channel.ICSSChannel;

public class ChannelTreeModel {
	private ChannelTreeModel parent;
	private Collection<ICSSChannel> child = new HashSet<ICSSChannel>();
	private int counter;

	public ChannelTreeModel(Collection<ICSSChannel> child) {
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

	public Collection<ICSSChannel> getChild() {
		return child;
	}

	public void setChild(Collection<ICSSChannel> child) {
		this.child = child;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}