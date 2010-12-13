package org.csstudio.multichannelviewer.actions;

import java.util.Collection;

import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.api.ChannelUtil;
import static gov.bnl.channelfinder.api.Channel.Builder.*;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

// This will remove all channels with the prefix "Test_"
public class RemoveTestChannels implements IViewActionDelegate {

	private ChannelFinderClient client = ChannelFinderClient.getInstance();

	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(IAction action) {
		Collection<String> channelNames = ChannelUtil.getChannelNames(client.findChannelsByName("Test_*"));
		for (String name : channelNames) {
			client.remove(channel(name));
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
