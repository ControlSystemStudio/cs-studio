package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinderClient;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import org.csstudio.utility.channel.ICSSChannel;
import org.csstudio.utility.channel.ICSSChannelFactory;
import org.csstudio.utility.channel.nsls2.CSSChannelFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;

public class SearchChannels extends Job {
	private String searchPattern;
	private ChannelFinderView channelFinderView;

	public SearchChannels(String name, String pattern,
			ChannelFinderView channelFinderView) {
		super(name);
		this.searchPattern = pattern;
		this.channelFinderView = channelFinderView;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Seaching channels ", IProgressMonitor.UNKNOWN);
		final Collection<ICSSChannel> channels = new HashSet<ICSSChannel>();
		try {
			for (Channel channel : ChannelFinderClient.getInstance()
					.findChannels(buildSearchMap(searchPattern))) {
				ICSSChannelFactory cssChannelFactory = CSSChannelFactory
						.getInstance();
				channels.add(cssChannelFactory.getCSSChannel(channel));
			}
			// final XmlChannels channels = testData(); // to use test data
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					// TODO return set of channels sorted by name
					// each channel has its properties and tags sorted by name.
					channelFinderView.updateList(channels);
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		monitor.done();
		return Status.OK_STATUS;
	}

	private static Map<String, String> buildSearchMap(String searchPattern) {
		Hashtable<String, String> map = new Hashtable<String, String>();
		String[] words = searchPattern.split("\\s");
		if (words.length < 0) {
			// ERROR
		}
		for (int index = 0; index < words.length; index++) {
			if (!words[index].contains("=")) {
				// this is a name value
				map.put("~name", words[index]);
			} else {
				// this is a property or tag
				String key = words[index].split("=")[0];
				String values = words[index].split("=")[1];
				if (key.equals("Tags")) {
					map.put("~tag", values);
					// for (int i = 0; i < values.length; i++)
					// map.put("~tag", values[i]);
				} else {
					map.put(key, values);
				}
			}
		}
		return map;
	}

}
