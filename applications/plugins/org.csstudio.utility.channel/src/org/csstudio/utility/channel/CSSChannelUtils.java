package org.csstudio.utility.channel;

import gov.bnl.channelfinder.api.Channel;
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
	public static Collection<String> getCSSChannelNames(Collection<Channel> channels){
		Collection<String> names = new HashSet<String>();
		for (Channel channel : channels) {
			names.add(channel.getName());
		}
		return Collections.unmodifiableCollection(names);
	}
	
	/**
	 * Get all the Tags present on the collection of ICSSChannels <tt>channels</tt>
	 * @param cssChannels
	 * @return
	 */
	public static Collection<String> getCSSChannelTagNames(Collection<Channel> cssChannels){
		Collection<String> tags = new HashSet<String>();
		for (Channel channel : cssChannels) {
			for (Tag tag : channel.getTags()) {
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
	public static Collection<String> getCSSChannelPropertyNames(Collection<Channel> cssChannels){
		Collection<String> properties = new HashSet<String>();
		for (Channel channel : cssChannels) {
			for (Property property : channel.getProperties()) {
				properties.add(property.getName());
			}
		} 
		return properties;
	}
	
	private static <T> boolean overlap(Collection<T> items, Collection<T> otherItems) {
		for (T item : items) {
			if (otherItems.contains(item))
				return true;
		}
		return false;
	}
	
	public static Collection<Channel> filterByOneOrMoreElements(Collection<Channel> channels,
			Collection<String> propNames, Collection<String> tagNames) {
		Collection<Channel> result = new HashSet<Channel>();
		for (Channel channel : channels) {
			if (overlap(channel.getPropertyNames(), propNames)) {
				result.add(channel);
			}
			if (overlap(channel.getTagNames(), tagNames)) {
				result.add(channel);
			}
		}
		return result;
	}

}
