package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.api.ChannelUtil;
import gov.bnl.channelfinder.api.Property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.csstudio.utility.channelfinder.CFClientManager;
import org.csstudio.utility.channelfinder.ChannelQuery;
import org.csstudio.utility.channelfinder.ChannelQueryListener;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.WaterfallWidget;
import org.eclipse.swt.widgets.Composite;

public class MultiChannelWaterfallWidget extends WaterfallWidget {

	public MultiChannelWaterfallWidget(Composite parent, int style) {
		super(parent, style);
	}

	private String inputText;

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
		queryChannels();
	}

	@Override
	public void setSortProperty(String sortProperty) {
		super.setSortProperty(sortProperty);

		queryChannels();
	}

	private void queryChannels() {
		if (inputText == null) {
			setWaveformPVName(null);
			setScalarPVNames(null);
			return;
		}
		
		final ChannelQuery query = ChannelQuery.Builder.query(inputText)
				.create();
		query.addChannelQueryListener(new ChannelQueryListener() {

			@Override
			public void getQueryResult() {
				List<String> channelNames = null;
				Exception ex = query.getLastException();
				if (ex == null) {
					Collection<Channel> channels = query.getResult();
					if (channels != null && !channels.isEmpty()) {
						// Sort if you can
						try {
							List<Channel> sortedChannels = new ArrayList<Channel>(
									channels);
							Collections.sort(sortedChannels,
									new Comparator<Channel>() {
										@Override
										public int compare(Channel o1,
												Channel o2) {
											return findProperty(o1).compareTo(
													findProperty(o2));
										}

										public Double findProperty(
												Channel channel) {
											for (Property property : channel
													.getProperties()) {
												if (property.getName().equals(
														getSortProperty())) {
													return Double
															.parseDouble(property
																	.getValue());
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
				}
				
				final List<String> finalChannels = channelNames;

				SWTUtil.swtThread().execute(new Runnable() {
					
					@Override
					public void run() {
						if (finalChannels == null || finalChannels.isEmpty()) {
							setWaveformPVName(inputText);
						} else if (finalChannels.size() == 1) {
							setWaveformPVName(finalChannels.get(0));
						} else
							setScalarPVNames(finalChannels);
					}
				});
			}

		});
		query.execute();

	}

}
