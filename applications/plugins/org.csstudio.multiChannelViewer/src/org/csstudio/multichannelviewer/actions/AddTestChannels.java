package org.csstudio.multichannelviewer.actions;

import static gov.bnl.channelfinder.api.Property.Builder.property;
import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import static gov.bnl.channelfinder.api.Channel.Builder.channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinderClient;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class AddTestChannels implements IViewActionDelegate {
	private ChannelFinderClient client = ChannelFinderClient.getInstance();
	private Random generator = new Random(19580427);

	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(IAction action) {
		// now add all the properties and tags we are going to use.
		// properties
		client.add(property("Test_PropA").owner("shroffk"));
		client.add(property("Test_PropB").owner("shroffk"));
		client.add(property("Test_PropC").owner("shroffk"));
		// tags
		client.add(tag("Test_TagA").owner("shroffk"));
		client.add(tag("Test_TagB").owner("shroffk"));

		Collection<Channel.Builder> channels = new ArrayList<Channel.Builder>();

		for (int i = 0; i < 2000; i++) {
			String channelName = "Test_";
			channelName += getName(i);
			Channel.Builder channel = channel(channelName).owner("shroffk")
					.with(
							property("Test_PropA", Integer.toString(i)).owner(
									"shroffk"));
			if (i < 1000)
				channel.with(tag("Test_TagA", "shroffk"));
			if ((i >= 500) || (i < 1500))
				channel.with(tag("Test_TagB", "Shroffk"));
			channel.with(property("Test_PropB", Integer.toString(generator
					.nextInt(100))));
			channel.with(property("Test_PropC", "ALL"));
			channels.add(channel);
		}
		// Add all the channels;
		try {
			ChannelFinderClient.getInstance().add(channels);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getName(int i) {
		if (i < 1000)
			return "first:" + getName500(i);
		else
			return "second:" + getName500(i - 1000);
	}

	private static String getName500(int i) {
		if (i < 500)
			return "a" + getName100(i);
		else
			return "b" + getName100(i - 500);
	}

	private static String getName100(int i) {
		return "<" + Integer.toString(i / 100) + "00>" + getNameID(i % 100);
	}

	private static String getNameID(int i) {
		return ":" + Integer.toString(i / 10) + ":" + Integer.toString(i);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}