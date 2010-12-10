package org.csstudio.channelfinder.views;

import static gov.bnl.channelfinder.api.ChannelUtil.getPropertyNames;
import static gov.bnl.channelfinder.api.ChannelUtil.getTagNames;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.Property;
import gov.bnl.channelfinder.api.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ChannelFinderViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	private Collection<ICSSChannel> channels;
	Collection<String> allProperties;
	Collection<String> allTags;

	public ChannelFinderViewLabelProvider(Collection<ICSSChannel> channels) {
		// TODO Auto-generated constructor stub
		this.channels = channels;
		this.allProperties = getAllPropertyNames(channels);
		this.allTags = getAllTagNames(channels);
	}

	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int index) {

		Channel channel = ((ICSSChannel) element).getChannel();
		if (index == 0) {
			return channel.getName();
		} else if (index == 1) {
			return channel.getOwner();
		} else if (index < (allProperties.size() + 2)) {
			ArrayList<String> propertyNames = new ArrayList<String>(
					allProperties);
			String propertyName = propertyNames.get(index - 2);

			for (Property property : channel.getProperties()) {
				if (property.getName().equals(propertyName)) {
					return property.getValue();
				}
			}
		} else if ((index >= (allProperties.size() + 2))
				&& (index < (allProperties.size() + allTags.size() + 2))) {
			ArrayList<String> tagNames = new ArrayList<String>(allTags);
			String tagName = tagNames.get(index - (allProperties.size() + 2));
			for (Tag tag : channel.getTags()) {
				if (tag.getName().equals(tagName)) {
					return "tagged";
				}
			}
			return null;

		}
		// number of additional columns = # of total properties + # of total

		return null;
	}

	private Collection<String> getAllTagNames(
			Collection<ICSSChannel> channelItems) {
		Collection<String> tagNames = new HashSet<String>();
		for (ICSSChannel channelItem : channelItems) {
			tagNames.addAll(getTagNames(channelItem.getChannel()));
		}
		return tagNames;

	}

	private Collection<String> getAllPropertyNames(
			Collection<ICSSChannel> channelItems) {
		Collection<String> propertyNames = new HashSet<String>();
		for (ICSSChannel channelItem : channelItems) {
			propertyNames.addAll(getPropertyNames(channelItem.getChannel()));
		}
		return propertyNames;
	}
}
