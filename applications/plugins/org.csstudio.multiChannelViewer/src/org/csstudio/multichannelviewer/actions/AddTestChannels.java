package org.csstudio.multichannelviewer.actions;

import static gov.bnl.channelfinder.api.Channel.Builder.channel;
import static gov.bnl.channelfinder.api.Property.Builder.property;
import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import gov.bnl.channelfinder.api.Channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.csstudio.multichannelviewer.ChannelsListView;
import org.csstudio.multichannelviewer.model.CSSChannelGroup;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class AddTestChannels implements IViewActionDelegate {
	private Random generator = new Random(19580427);
	private IViewPart view;
	private Collection<Channel> channels;
	
	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		this.view = view;
	}

	@Override
	public void run(IAction action) {
		channels = new ArrayList<Channel>();

		for (int i = 0; i < 2000; i++) {
			String channelName = "SR:C01";
			channelName += getName(i);
			Channel.Builder channel = channel(channelName).owner("simulation")
					.with(property("ChannelCount", Integer.toString(i)).owner(
							"shroffk"));
			if (i < 1000)
				channel.with(tag("GoldenOrbit", "shroffk"));
			if ((i >= 500) || (i < 1500))
				channel.with(tag("myFavorite", "Shroffk"));
			channel.with(property("Position",
					generateString(new Random(), "0123456789.", 4)));
			channel.with(property("Facility", "storageRing"));
			channel.with(property("element", i%2 == 0?"sextupole":"quadrupole"));
			channels.add(channel.build());
		}
		// Add all the channels;
		try {
			ChannelsListView viewB = (ChannelsListView) view
					.getSite()
					.getPage()
					.findView("org.csstudio.multichannelviewer.ChannelListView");
			if (viewB != null) {
				viewB.setChannelsGroup(new CSSChannelGroup("Test Channels", channels));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

	private static String generateString(Random rng, String characters,
			int length) {
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
}