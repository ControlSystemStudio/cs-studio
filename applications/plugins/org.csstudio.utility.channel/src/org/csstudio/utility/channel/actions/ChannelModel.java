package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.model.XmlChannel;

import java.util.ArrayList;

public class ChannelModel {
	private ChannelModel parent;
	private ArrayList<XmlChannel> child = new ArrayList<XmlChannel>();
	private int counter;

	public ChannelModel(ArrayList<XmlChannel> child) {
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

	public ArrayList<XmlChannel> getChild() {
		return child;
	}

	public void setChild(ArrayList<XmlChannel> child) {
		this.child = child;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}