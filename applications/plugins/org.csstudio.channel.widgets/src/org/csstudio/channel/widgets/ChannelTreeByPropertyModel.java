package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class ChannelTreeByPropertyModel {

	// Reference to the widget is used to return the configurable
	// widget from the selection
	private ChannelTreeByPropertyWidget widget;
	
	List<Channel> allChannels;
	List<String> properties;
	private ChannelTreeByPropertyNode root;
	final String query;
	private final boolean showChannelNames;
	
	public ChannelTreeByPropertyModel(String query, Collection<Channel> allChannels, List<String> properties,
			ChannelTreeByPropertyWidget widget, boolean showChannelNames) {
		if (allChannels == null) {
			allChannels = Collections.emptyList();
		}
		
		// Filter the channels that would not show up as leaf because they don't
		// have a value for all properties
		this.allChannels = new ArrayList<Channel>(ChannelUtil.filterbyProperties(allChannels, properties));
		this.properties = properties;
		this.query = query;
		this.widget = widget;
		this.showChannelNames = showChannelNames;
		this.root = new ChannelTreeByPropertyNode(this, null, "");
	}
	
	public ChannelTreeByPropertyNode getRoot() {
		return root;
	}
	
	public ChannelTreeByPropertyWidget getWidget() {
		return widget;
	}
	
	class Node {
		// Channels represented by this node and down
		private List<Channel> nodeChannels;
		// 0 for root, 1 for children, 2 for grandchildren...
		private final int depth;
		// null for root, first property value for children, second property value for grandchildren,
		// channelName for leaf
		private final String displayName;
		// Next property value for root and descendents, names of channels for first to last node,
		// null for leaf
		private final List<String> childrenNames;
		
		
		private Node(Node parentNode, String displayName) {
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
				nodeChannels = allChannels;
			} else if (getPropertyName() == null) {
				// leaf node, no channel list
				nodeChannels = null;
			} else {
				// Filter the channels that match the property name
				nodeChannels = new ArrayList<Channel>();
				for (Channel channel : parentNode.nodeChannels) {
					if (this.displayName.equals(channel.getProperty(getPropertyName()).getValue())) {
						nodeChannels.add(channel);
					}
				}
			}
			
			if (depth < properties.size()) {
				// Children will be property values
				childrenNames = new ArrayList<String>(ChannelUtil.getPropValues(nodeChannels, properties.get(depth)));
				Collections.sort(childrenNames);
			} else if (nodeChannels != null) {
				// Children will be channels
				childrenNames = new ArrayList<String>(ChannelUtil.getChannelNames(nodeChannels));
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
			if (index >= properties.size())
				return null;
			
			return properties.get(index);
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		public List<String> getChildrenNames() {
			return childrenNames;
		}

		public Node getChild(int index) {
			return new Node(this, childrenNames.get(index));
		}
		
	}

	public boolean isShowChannelNames() {
		return showChannelNames;
	}
}
