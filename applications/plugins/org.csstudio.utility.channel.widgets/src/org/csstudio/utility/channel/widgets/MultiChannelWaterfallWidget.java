package org.csstudio.utility.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.api.ChannelUtil;
import gov.bnl.channelfinder.api.Property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.csstudio.utility.pvmanager.widgets.WaterfallWidget;
import org.eclipse.swt.widgets.Composite;

public class MultiChannelWaterfallWidget extends WaterfallWidget {

	public MultiChannelWaterfallWidget(Composite parent, int style) {
		super(parent, style);
	}
	
	private String inputText;
	private String sortProperty;
	
	public String getSortProperty() {
		return sortProperty;
	}
	
	public void setSortProperty(String sortProperty) {
		this.sortProperty = sortProperty;
	}
	
	@Override
	public String getInputText() {
		return inputText;
	}
	
	@Override
	public void setInputText(String inputText) {
		if (this.inputText != null && this.inputText.equals(inputText)) {
			return;
		}
		
		this.inputText = inputText;
		
		List<String> channelNames = null;
		try {
			// Should be done in a background task
			Collection<Channel> channels = ChannelFinderClient.getInstance().findChannelsByTag(inputText);
			if (channels != null && !channels.isEmpty()) {
				// Sort if you can
				try {
					List<Channel> sortedChannels = new ArrayList<Channel>(channels);
					Collections.sort(sortedChannels, new Comparator<Channel>() {
						@Override
						public int compare(Channel o1, Channel o2) {
							return findProperty(o1).compareTo(findProperty(o2));
						}
						
						public Double findProperty(Channel channel) {
							for (Property property : channel.getProperties()) {
								if (property.getName().equals(sortProperty)) {
									return Double.parseDouble(property.getValue());
								}
							}
							return null;
						}
					});
					channels = sortedChannels;
				} catch (Exception e) {
					// Leave unsorted
				}
				
				channelNames = new ArrayList<String>();
				for (Channel channel : channels) {
					channelNames.add(channel.getName());
				}
			}
		} catch (Exception e) {
		}
		
		if (channelNames != null && !channelNames.isEmpty()) {
			setScalarPVNames(channelNames);
		} else {
			setWaveformPVName(inputText);
		}
		
	}

}
