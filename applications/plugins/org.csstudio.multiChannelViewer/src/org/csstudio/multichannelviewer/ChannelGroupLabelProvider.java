package org.csstudio.multichannelviewer;

import static gov.bnl.channelfinder.api.ChannelUtil.getPropertyNames;
import static gov.bnl.channelfinder.api.ChannelUtil.getTagNames;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.Property;
import gov.bnl.channelfinder.api.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.csstudio.multichannelviewer.model.IChannelGroup;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ChannelGroupLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	private IChannelGroup channelGroup;
	Collection<String> allProperties;
	Collection<String> allTags;

	public ChannelGroupLabelProvider(IChannelGroup channelGroup) {
		this.channelGroup = channelGroup;	
		this.allProperties = getAllPropertyNames(this.channelGroup.getList());
		this.allTags = getAllTagNames(this.channelGroup.getList());
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Channel channel = (Channel) element;
		if (columnIndex == 0) {
			return channel.getName();
		} else if (columnIndex == 1) {
			return channel.getOwner();
		} else if (columnIndex < (allProperties.size() + 2)) {
			ArrayList<String> propertyNames = new ArrayList<String>(
					allProperties);
			String propertyName = propertyNames.get(columnIndex - 2);

			for (Property property : channel.getProperties()) {
				if (property.getName().equals(propertyName)) {
					return property.getValue();
				}
			}
		} else if ((columnIndex >= (allProperties.size() + 2))
				&& (columnIndex < (allProperties.size() + allTags.size() + 2))) {
			ArrayList<String> tagNames = new ArrayList<String>(allTags);
			String tagName = tagNames.get(columnIndex
					- (allProperties.size() + 2));
			for (Tag tag : channel.getTags()) {
				if (tag.getName().equals(tagName)) {
					return "tagged";
				}
			}
			return "";

		}
		return "";
	}

	private Collection<String> getAllTagNames(
			Collection<Channel> channelItems) {
		Collection<String> tagNames = new HashSet<String>();
		for (Channel channel : channelItems) {
			tagNames.addAll(getTagNames(channel));
		}
		return tagNames;

	}

	private Collection<String> getAllPropertyNames(
			Collection<Channel> channelItems) {
		Collection<String> propertyNames = new HashSet<String>();
		for (Channel channel : channelItems) {
			propertyNames.addAll(getPropertyNames(channel));
		}
		return propertyNames;
	}
}
