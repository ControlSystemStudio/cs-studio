/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package gov.bnl.channelfinder.api;

import gov.bnl.channelfinder.api.Channel.Builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
	 * @return a set of all unique tag names associated with atleast one or more
	 *         channel in channels
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
			if (property.getValue() != null)
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

	public static Collection<String> getPropValues(
			Collection<Channel> channels, String propertyName) {
		SortedSet<String> propertyValues = new TreeSet<String>();
		for (Channel channel : channels) {
			if (channel.getProperty(propertyName) != null
					&& channel.getProperty(propertyName).getValue() != null)
				propertyValues
						.add(channel.getProperty(propertyName).getValue());
		}
		return propertyValues;
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
	 * Given a Collection of channels returns a new collection of channels
	 * containing only those channels which have all the properties in the
	 * <tt>propNames</tt>
	 * 
	 * @param channels
	 *            - the input list of channels
	 * @param propNames
	 *            - the list of properties required on all channels
	 * @return
	 */
	public static Collection<Channel> filterbyProperties(
			Collection<Channel> channels, Collection<String> propNames) {
		Collection<Channel> result = new ArrayList<Channel>();
		Collection<Channel> input = new ArrayList<Channel>(channels);
		for (Channel channel : input) {
			if (channel.getPropertyNames().containsAll(propNames)) {
				result.add(channel);
			}
		}
		return result;
	}

	/**
	 * Given a Collection of channels returns a new collection of channels
	 * containing only those channels which have all the tags in the
	 * <tt>tagNames</tt> AND all the properties in <tt></tt>
	 * 
	 * @param channels
	 *            - the input list of channels
	 * @param tagNames
	 *            - the list of tags required on all channels
	 * @return
	 */
	public static Collection<Channel> filterbyTags(
			Collection<Channel> channels, Collection<String> tagNames) {
		Collection<Channel> result = new ArrayList<Channel>();
		Collection<Channel> input = new ArrayList<Channel>(channels);
		for (Channel channel : input) {
			if (channel.getTagNames().containsAll(tagNames)) {
				result.add(channel);
			}
		}
		return result;
	}

	/**
	 * Given a Collection of channels returns a new collection of channels
	 * containing only those channels which have all the tags in the
	 * <tt>tagNames</tt>
	 * 
	 * @param channels
	 *            - the input list of channels
	 * @param propNames
	 *            - the list of properties required on all channels
	 * @param tagNames
	 *            - the list of tags required on all channels
	 * @return
	 */
	public static Collection<Channel> filterbyElements(
			Collection<Channel> channels, Collection<String> propNames,
			Collection<String> tagNames) {
		Collection<Channel> result = new ArrayList<Channel>();
		Collection<Channel> input = new ArrayList<Channel>(channels);
		for (Channel channel : input) {
			if (channel.getPropertyNames().containsAll(propNames)
					&& channel.getTagNames().containsAll(tagNames)) {
				result.add(channel);
			}
		}
		return result;
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