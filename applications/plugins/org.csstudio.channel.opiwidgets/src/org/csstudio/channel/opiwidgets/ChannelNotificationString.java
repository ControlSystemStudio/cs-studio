package org.csstudio.channel.opiwidgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChannelNotificationString {
	
	private final String notificationString;
	private final List<String> requiredProperties = new ArrayList<String>();
	private final List<String> textTokens = new ArrayList<String>();
	private static final String TOKEN_PATTERN = "#\\((\\w*)\\)";
	private static final Pattern PATTERN = Pattern.compile(TOKEN_PATTERN);
	
	public ChannelNotificationString(String notificationString) {
		this.notificationString = notificationString;
		Matcher matcher = PATTERN.matcher(notificationString);
		int previousEnd = 0;
		while (matcher.find()) {
			textTokens.add(notificationString.substring(previousEnd, matcher.start()));
			requiredProperties.add(matcher.group(1));
			previousEnd = matcher.end();
		}
		textTokens.add(notificationString.substring(previousEnd, notificationString.length()));
	}

	public List<String> getRequiredProperties() {
		return Collections.unmodifiableList(requiredProperties);
	}
	
	public String notification(List<String> propertyValues) {
		StringBuilder builder = new StringBuilder();
		int n = 0;
		for (n = 0; n < requiredProperties.size(); n++) {
			builder.append(textTokens.get(n)).append(propertyValues.get(n));
		}
		builder.append(textTokens.get(n));
		return builder.toString();
	}
}
