/**
 * 
 */
package gov.bnl.channelfinder.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static gov.bnl.channelfinder.api.Channel.Builder.*;

/**
 * @author shroffk
 * 
 */
public class ChannelUtil {

	/**
	 * This class is not meant to be instantiated or extended
	 */
	private ChannelUtil(){
		
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
	 * @return
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
	 * Return a union of property names associated with channels
	 * 
	 * @param channels
	 * @return
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
	 * Returns all the channel Names
	 * 
	 * @param channels
	 * @return
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
	 * TODO evaluate need/correctness
	 * Returns a collection of objects of Type Channel derived from the
	 * collection of Channel.Builders <tt>channelBuilders</tt>
	 * 
	 * @param channelBuilders
	 * @return
	 */
	static Collection<Channel> toChannels(
			Collection<Channel.Builder> channelBuilders) {
		Collection<Channel> channels = new HashSet<Channel>();
		for (Channel.Builder builder : channelBuilders) {
			channels.add(builder.build());
		}
		return Collections.unmodifiableCollection(channels);
	}

}