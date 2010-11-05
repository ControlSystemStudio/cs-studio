package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.Property;
import gov.bnl.channelfinder.api.Tag;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ChannelFinderViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	private Collection<Channel> channels;
	private Collection<String> allProperties;
	private Collection<String> allTags;

	public ChannelFinderViewLabelProvider(Collection<Channel> channels) {
		// TODO Auto-generated constructor stub
		this.channels = channels;
	}

	public ChannelFinderViewLabelProvider(Collection<String> allProperties,
			Collection<String> allTags) {
		// TODO Auto-generated constructor stub
		this.allProperties = allProperties;
		this.allTags = allTags;
	}

	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int index) {
		Channel channel = (Channel) element;
		if (index == 0) {
			return channel.getName();
		} else if (index == 1) {
			return channel.getOwner();
		} else if (index < (allProperties.size() + 2)) {
			ArrayList<String> propertyNames = new ArrayList<String>(
					allProperties);
			String propertyName = propertyNames.get(index - 2);
			
			for (Property property : channel.getProperties()) {
				if(property.getName().equals(propertyName)){
					return property.getValue();
				}
			}
		} else if ((index >= (allProperties.size() + 2))
				&& (index < (allProperties.size() + allTags.size() + 2))) {
			ArrayList<String> tagNames = new ArrayList<String>(allTags);
			String tagName = tagNames.get(index - (allProperties.size() + 2));			
			for (Tag tag : channel.getTags()) {
				if(tag.getName().equals(tagName)){
					return "tagged";
				}
			}
			return null;

		}
		// number of additional columns = # of total properties + # of total

		return null;
	}
}
