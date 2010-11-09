package org.csstudio.utility.channel.actions;

import java.util.Collection;
import java.util.HashSet;

import org.csstudio.utility.channel.ICSSChannel;

public class ChannelModel {
	private ChannelModel parent;
	private Collection<ICSSChannel> child = new HashSet<ICSSChannel>();
	private int counter;

	public ChannelModel(Collection<ICSSChannel> child) {
		this.child = child;
	}

	public ChannelModel(int counter, ChannelModel parent) {
		this.parent = parent;
		this.counter= counter;
	}
	
	public ChannelModel getParent() {
		return parent;
	}

	public void setParent(ChannelModel parent) {
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