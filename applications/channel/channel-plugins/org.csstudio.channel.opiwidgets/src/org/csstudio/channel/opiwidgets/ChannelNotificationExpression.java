package org.csstudio.channel.opiwidgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelUtil;
import gov.bnl.channelfinder.api.Property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Notification string for a channel based widget. It parses the string, and then
 * is able to create the notification string from the property value, a map of
 * available property values or a channel. Notification support can use
 * this to go from the current selection to the appropriate selection notification string.
 * 
 * @author carcassi
 */
public class ChannelNotificationExpression {
	
	private final List<String> requiredProperties = new ArrayList<String>();
	private final List<String> textTokens = new ArrayList<String>();
	private static final String TOKEN_PATTERN = "#\\((\\w*)\\)|#\\(Channel Name\\)";
	private static final Pattern PATTERN = Pattern.compile(TOKEN_PATTERN);
	
	/**
	 * Parses the string and creates a new notification.
	 * 
	 * @param notificationString the notification string
	 */
	public ChannelNotificationExpression(String notificationString) {
		Matcher matcher = PATTERN.matcher(notificationString);
		int previousEnd = 0;
		while (matcher.find()) {
			textTokens.add(notificationString.substring(previousEnd, matcher.start()));
			if (matcher.group(1) == null) {
				requiredProperties.add("Channel Name");
			} else {
				requiredProperties.add(matcher.group(1));
			}
			previousEnd = matcher.end();
		}
		textTokens.add(notificationString.substring(previousEnd, notificationString.length()));
	}

	/**
	 * Returns the full list of required properties.
	 * 
	 * @return a list of strings
	 */
	public List<String> getRequiredProperties() {
		return Collections.unmodifiableList(requiredProperties);
	}
	
	/**
	 * Creates a notification given the property values in the same order given returned
	 * by {@link ChannelNotificationExpression#getRequiredProperties()}.
	 * 
	 * @param propertyValues the values
	 * @return the new notification string
	 */
	public String notification(List<String> propertyValues) {
		StringBuilder builder = new StringBuilder();
		int n = 0;
		for (n = 0; n < requiredProperties.size(); n++) {
			builder.append(textTokens.get(n)).append(propertyValues.get(n));
		}
		builder.append(textTokens.get(n));
		return builder.toString();
	}

	/**
	 * Creates a new notification string given a map of property name/value.
	 * 
	 * @param map a map from names to values
	 * @return the notification string
	 */
	public String notification(Map<String, String> map) {
		StringBuilder builder = new StringBuilder();
		int n = 0;
		for (n = 0; n < requiredProperties.size(); n++) {
			builder.append(textTokens.get(n));
			String value = map.get(requiredProperties.get(n));
			if (value == null || value.isEmpty()) {
				return "";
			} else {
				builder.append(value);
			}
		}
		builder.append(textTokens.get(n));
		return builder.toString();
	}

	/**
	 * Creates a new notification string based on the properties of the given channel.
	 * 
	 * @param channel the selected channel
	 * @return the notification string
	 */
	public String notification(Channel channel) {
		StringBuilder builder = new StringBuilder();
		int n = 0;
		for (n = 0; n < requiredProperties.size(); n++) {
			builder.append(textTokens.get(n));
			String propertyName = requiredProperties.get(n);
			
			// Check if the property is actually the channel name
			if ("Channel Name".equals(propertyName)) {
				builder.append(channel.getName());
			} else {
				
				// Find a matching property
				Property property = channel.getProperty(propertyName);
				if (property != null) {
					if (property.getValue() == null ||
							property.getValue().isEmpty()) {
						// Matching property has no value. Abort!
						return "";
					}
					builder.append(property.getValue());
				} else {
					return "";
				}
			}
		}
		builder.append(textTokens.get(n));
		return builder.toString();
	}

	public String notification(Collection<Channel> channels) {
		if (channels.size() == 1)
			return notification(channels.iterator().next());
		
		if (channels.isEmpty())
			return "";
					
		List<String> commonValues = new ArrayList<String>();
		for (String propertyName : getRequiredProperties()) {
			Collection<String> values = ChannelUtil.getPropValues(channels, propertyName);
			if (values.size() == 1) {
				commonValues.add(values.iterator().next());
			} else {
				return "";
			}
		}
		return notification(commonValues);
	}
}
