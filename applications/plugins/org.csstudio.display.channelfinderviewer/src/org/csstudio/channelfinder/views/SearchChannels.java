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

import com.sun.jersey.core.util.MultivaluedMapImpl;

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
			// channels = sort(ChannelFinderClient.getInstance().findChannels(
			// buildSearchMap1(searchPattern)));
			for (Channel channel : ChannelFinderClient.getInstance().findChannels(
					buildSearchMap1(searchPattern))) {
				ICSSChannelFactory cssChannelFactory = CSSChannelFactory.getInstance();
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

	private static MultivaluedMapImpl buildSearchMap(String searchPattern) {
		MultivaluedMapImpl map = new MultivaluedMapImpl();
		String[] words = searchPattern.split("\\s");
		if (words.length < 0) {
			// ERROR
			throw new IllegalArgumentException("no arguments specified");
		}
		for (int index = 0; index < words.length; index++) {
			if (!words[index].contains("=")) {
				// this is a name value
				map.add("~name", words[index]);
			} else {
				// this is a property or tag
				String key = words[index].split("=")[0];
				String[] values = words[index].split("=")[1].split(",");
				if (key.equalsIgnoreCase("Tags")) {
					for (int i = 0; i < values.length; i++)
						map.add("~tag", values[i]);
				} else {
					for (int i = 0; i < values.length; i++)
						map.add(key, values[i]);
				}
			}
		}
		return map;
	}

	private static Map<String, String> buildSearchMap1(String searchPattern) {
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
					map.put(key, values);
					// for (int i = 0; i < values.length; i++)
					// map.put("~tag", values[i]);
				} else {
					map.put(key, values);
				}
			}
		}
		return map;
	}

//
//	private static Channel sort(Channel channel) {
//		Channel ch = new XmlChannel(channel.getName(), channel.getOwner());
//		Collection<Property> sortedProps = new TreeSet<Property>(
//				new PropertyComparator());
//		sortedProps.addAll(channel.getProperties());
//		ch.setXmlProperties(sortedProps);
//		Collection<XmlTag> sortedTags = new TreeSet<XmlTag>(new TagComparator());
//		sortedTags.addAll(channel.getXmlTags());
//		ch.setXmlTags(sortedTags);
//		return ch;
//	}
//
//	private static XmlChannels sort(XmlChannels channels) {
//		XmlChannels chs = new XmlChannels();
//		Collection<XmlChannel> sortedChannels = new TreeSet<XmlChannel>(
//				new ChannelComparator());
//		Iterator<XmlChannel> itr = channels.getChannels().iterator();
//		while (itr.hasNext()) {
//			sortedChannels.add(sort(itr.next()));
//		}
//		chs.setChannels(sortedChannels);
//		return chs;
//	}
//
//	public static XmlChannels testData() {
//		XmlChannels channels = new XmlChannels();
//		channels
//				.addChannel(getchannel("fff", "apple", "aProp", "aVal", "aTag"));
//		channels.addChannel(getchannel("bbb", "ball", "bProp", "bVal", "bTag"));
//		XmlChannel ch = getchannel("ccc", "egg", "cProp", "cVal", "cTag");
//		ch.addProperty(new XmlProperty("aProp", "egg", "aVal"));
//		ch.addProperty(new XmlProperty("bProp", "egg", "bVal"));
//		ch.addTag(new XmlTag("aTag", "kunal"));
//		channels.addChannel(ch);
//		channels.addChannel(getchannel("ddd", "dag", "dProp", "dVal", "dTag"));
//		channels.addChannel(getchannel("eee", "cat", "dProp", "dVal", "eTag"));
//		return channels;
//	}
//
//	private static XmlChannel getchannel(String name, String owner,
//			String prop, String val, String tag) {
//		XmlChannel ch = new XmlChannel(name, owner);
//		ch.addProperty(new XmlProperty(prop, owner, val));
//		ch.addTag(new XmlTag(tag, owner));
//		return ch;
//	}

}
