/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package gov.bnl.channelfinder.api;

import gov.bnl.channelfinder.api.Channel.Builder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import static gov.bnl.channelfinder.api.Channel.Builder.*;

/**
 * @author shroffk
 * 
 */
public class ChannelUtil {

	/**
	 * This class is not meant to be instantiated or extended
	 */
	private ChannelUtil() {

	}

	/**
	 * Return a list of tag names associated with this channel
	 * 
	 * @param channel
	 * @return
	 */
	public static Collection<String> getTagNames(Channel channel) {
		Collection<String> tagNames = new HashSet<String>();
		for (Tag tag : channel.getTags()) {
			tagNames.add(tag.getName());
		}
		return tagNames;
	}

	/**
	 * Return a union of tag names associated with channels
	 * 
	 * @param channels
	 * @return a set of all unique tag names associated with atleast one or
	 *         more channel in channels
	 */
	public static Collection<String> getAllTagNames(Collection<Channel> channels) {
		Collection<String> tagNames = new HashSet<String>();
		for (Channel channel : channels) {
			tagNames.addAll(getTagNames(channel));
		}
		return tagNames;
	}

	/**
	 * Return a list of property names associated with this channel
	 * 
	 * @param channel
	 * @return
	 */
	public static Collection<String> getPropertyNames(Channel channel) {
		Collection<String> propertyNames = new HashSet<String>();
		for (Property property : channel.getProperties()) {
			propertyNames.add(property.getName());
		}
		return propertyNames;
	}

	/**
	 * Deprecated - use channel.getTag instead
	 * 
	 * Return the Tag object with name <tt>tagName</tt> if it exists on the
	 * channel <tt>channel</tt> else return null
	 * 
	 * @param channel
	 * @param tagName
	 * @return
	 */
	@Deprecated
	public static Tag getTag(Channel channel, String tagName) {
		Collection<Tag> tag = Collections2.filter(channel.getTags(),
				new TagNamePredicate(tagName));
		if (tag.size() == 1)
			return tag.iterator().next();
		else
			return null;
	}

	private static class TagNamePredicate implements Predicate<Tag> {

		private String tagName;

		TagNamePredicate(String tagName) {
			this.tagName = tagName;
		}

		@Override
		public boolean apply(Tag input) {
			if (input.getName().equals(tagName))
				return true;
			return false;
		}
	}

	/**
	 * deprecated - use the channel.getProperty instead
	 * 
	 * Return the property object with the name <tt>propertyName</tt> if it
	 * exists on the channel <tt>channel</tt> else return null
	 * 
	 * @param channel
	 * @param propertyName
	 * @return
	 */
	@Deprecated
	public static Property getProperty(Channel channel, String propertyName) {
		Collection<Property> property = Collections2.filter(
				channel.getProperties(),
				new PropertyNamePredicate(propertyName));
		if (property.size() == 1)
			return property.iterator().next();
		else
			return null;
	}

	private static class PropertyNamePredicate implements Predicate<Property> {

		private String propertyName;

		PropertyNamePredicate(String propertyName) {
			this.propertyName = propertyName;
		}

		@Override
		public boolean apply(Property input) {
			if (input.getName().equals(propertyName))
				return true;
			return false;
		}
	}

	/**
	 * Return a union of property names associated with channels
	 * 
	 * @param channels
	 * @return a set of all unique property names associated with atleast one or
	 *         more channel in channels
	 */
	public static Collection<String> getPropertyNames(
			Collection<Channel> channels) {
		Collection<String> propertyNames = new HashSet<String>();
		for (Channel channel : channels) {
			propertyNames.addAll(getPropertyNames(channel));
		}
		return propertyNames;
	}

	/**
	 * Returns all the channel Names in the given Collection of channels
	 * 
	 * @param channels
	 * @return a set of all the unique names associated with the each channel in
	 *         channels
	 */
	public static Collection<String> getChannelNames(
			Collection<Channel> channels) {
		Collection<String> channelNames = new HashSet<String>();
		for (Channel channel : channels) {
			channelNames.add(channel.getName());
		}
		return channelNames;
	}

	/**
	 * Returns a list of {@link Channel} built from the list of
	 * {@link Channel.Builder}s
	 * 
	 * @param channelBuilders
	 *            - list of Channel.Builder to be built.
	 * @return Collection of {@link Channel} built from the channelBuilders
	 */
	static Collection<Channel> toChannels(
			Collection<Channel.Builder> channelBuilders) {
		Collection<Channel> channels = new HashSet<Channel>();
		for (Channel.Builder builder : channelBuilders) {
			channels.add(builder.build());
		}
		return Collections.unmodifiableCollection(channels);
	}

	/**
	 * Returns a {@link XmlChannels} object built from the list of
	 * {@link Channel.Builder}s
	 * 
	 * @param channelBuilders
	 *            - list of Channel.Builder to be built.
	 * @return A {@link XmlChannels} built from the channelBuilders
	 */
	static XmlChannels toXmlChannels(Collection<Channel.Builder> channelBuilders) {
		XmlChannels xmlChannels = new XmlChannels();
		for (Builder channel : channelBuilders) {
			xmlChannels.addXmlChannel(channel.toXml());
		}
		return xmlChannels;
	}

}