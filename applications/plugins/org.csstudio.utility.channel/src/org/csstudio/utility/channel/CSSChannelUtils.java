package org.csstudio.utility.channel;

import gov.bnl.channelfinder.api.Property;
import gov.bnl.channelfinder.api.Tag;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class CSSChannelUtils {

	private CSSChannelUtils(){
		
	}	
	
	/**
	 * Return all a set of channels names for the give set of channels <tt>channels</tt>
	 * @param channels
	 * @return
	 */
	public static Collection<String> getCSSChannelNames(Collection<ICSSChannel> channels){
		Collection<String> names = new HashSet<String>();
		for (ICSSChannel channel : channels) {
			names.add(channel.getName());
		}
		return Collections.unmodifiableCollection(names);
	}
	
	/**
	 * Get all the Tags present on the collection of ICSSChannels <tt>channels</tt>
	 * @param cssChannels
	 * @return
	 */
	public static Collection<String> getCSSChannelTagNames(Collection<ICSSChannel> cssChannels){
		Collection<String> tags = new HashSet<String>();
		for (ICSSChannel icssChannel : cssChannels) {
			for (Tag tag : icssChannel.getChannel().getTags()) {
				tags.add(tag.getName());
			}
		} 
		return tags;
	}
	
	/**
	 * Get all the Tags present on the collection of ICSSChannels <tt>channels</tt>
	 * @param cssChannels
	 * @return
	 */
	public static Collection<String> getCSSChannelPropertyNames(Collection<ICSSChannel> cssChannels){
		Collection<String> properties = new HashSet<String>();
		for (ICSSChannel icssChannel : cssChannels) {
			for (Property property : icssChannel.getChannel().getProperties()) {
				properties.add(property.getName());
			}
		} 
		return properties;
	}

}
