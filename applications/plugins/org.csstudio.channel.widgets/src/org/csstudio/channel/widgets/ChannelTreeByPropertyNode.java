package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.text.AbstractDocument.LeafElement;

public class ChannelTreeByPropertyNode {
	
	// The model that contains the node
	private ChannelTreeByPropertyModel model;
	
	// Channels represented by this node and down
	private List<Channel> nodeChannels;
	// 0 for root, 1 for children, 2 for grandchildren...
	private final int depth;
	// null for root, first property value for children, second property value
	// for grandchildren,
	// channelName for leaf
	private final String displayName;
	// Next property value for root and descendents, names of channels for first
	// to last node,
	// null for leaf
	private final List<String> childrenNames;

	ChannelTreeByPropertyNode(ChannelTreeByPropertyModel model, ChannelTreeByPropertyNode parentNode, String displayName) {
		this.model = model;
		
		// Calculate depth
		if (parentNode == null) {
			depth = 0;
		} else {
			depth = parentNode.depth + 1;
		}

		this.displayName = displayName;

		// Construct the Channel list
		if (parentNode == null) {
			// Node is root, get all channels
			nodeChannels = model.allChannels;
		} else if (getPropertyName() == null) {
			// leaf node, channels that match the name
			nodeChannels = new ArrayList<Channel>();
			for (Channel channel : parentNode.nodeChannels) {
				if (this.displayName.equals(channel.getName())) {
					nodeChannels.add(channel);
				}
			}
		} else {
			// Filter the channels that match the property name
			nodeChannels = new ArrayList<Channel>();
			for (Channel channel : parentNode.nodeChannels) {
				if (this.displayName.equals(channel.getProperty(
						getPropertyName()).getValue())) {
					nodeChannels.add(channel);
				}
			}
		}

		if (depth < model.properties.size()) {
			// Children will be property values
			childrenNames = new ArrayList<String>(ChannelUtil.getPropValues(
					nodeChannels, model.properties.get(depth)));
			Collections.sort(childrenNames);
		} else if (depth == model.properties.size()) {
			// Children will be channels
			childrenNames = new ArrayList<String>(
					ChannelUtil.getChannelNames(nodeChannels));
			Collections.sort(childrenNames);
		} else {
			childrenNames = null;
		}
	}

	/**
	 * The property name at this level
	 * 
	 * @return property name or null if leaf node
	 */
	public String getPropertyName() {
		// Root node does not have any property associated with it
		if (depth == 0)
			return null;

		int index = depth - 1;
		// We are at the channel level
		if (index >= model.properties.size())
			return null;

		return model.properties.get(index);
	}

	public String getDisplayName() {
		return displayName;
	}

	public List<String> getChildrenNames() {
		return childrenNames;
	}

	public ChannelTreeByPropertyNode getChild(int index) {
		return new ChannelTreeByPropertyNode(model, this, childrenNames.get(index));
	}
	
	public List<Channel> getNodeChannels() {
		return Collections.unmodifiableList(nodeChannels);
	}

}
