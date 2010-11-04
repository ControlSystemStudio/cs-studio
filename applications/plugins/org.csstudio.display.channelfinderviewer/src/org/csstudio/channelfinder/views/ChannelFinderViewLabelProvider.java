package org.csstudio.channelfinder.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import gov.bnl.channelfinder.model.XmlChannel;
import gov.bnl.channelfinder.model.XmlChannels;
import gov.bnl.channelfinder.model.XmlProperty;
import gov.bnl.channelfinder.model.XmlTag;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ChannelFinderViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	private XmlChannels xmlchannels;
	private Collection<String> allProperties;
	private Collection<String> allTags;

	public ChannelFinderViewLabelProvider(XmlChannels channels) {
		// TODO Auto-generated constructor stub
		this.xmlchannels = channels;
	}

	public ChannelFinderViewLabelProvider(Collection<String> allProperties,
			Collection<String> allTags) {
		// TODO Auto-generated constructor stub
		this.allProperties = allProperties;
		this.allTags = allTags;
	}

	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int index) {
		// TODO Auto-generated method stub
		XmlChannel channel = (XmlChannel) element;
		if (index == 0) {
			return channel.getName();
		} else if (index == 1) {
			return channel.getOwner();
		} else if (index < (allProperties.size() + 2)) {
			ArrayList<String> propertyNames = new ArrayList<String>(
					allProperties);
			String propertyName = propertyNames.get(index - 2);
			for (Iterator<XmlProperty> itr = channel.getXmlProperties()
					.iterator(); itr.hasNext();) {
				XmlProperty item = itr.next();
				if (item.getName().equals(propertyName)) {
					return item.getValue();
				}
			}
		} else if ((index >= (allProperties.size() + 2))
				&& (index < (allProperties.size() + allTags.size() + 2))) {
			ArrayList<String> tagNames = new ArrayList<String>(allTags);
			String tagName = tagNames.get(index - (allProperties.size() + 2));
			for (Iterator<XmlTag> itr = channel.getXmlTags().iterator(); itr
					.hasNext();) {
				if(itr.next().getName().equals(tagName)){
					return "tagged";
				}
			}
			return null;

		}
		// number of additional columns = # of total properties + # of total

		return null;
	}
}
