package org.csstudio.channelviewer.util;

import static gov.bnl.channelfinder.api.Channel.Builder.channel;
import static gov.bnl.channelfinder.api.Property.Builder.property;
import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import gov.bnl.channelfinder.api.Channel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class GenerateTestChannels {
	private static Collection<Channel> channels;
	private static String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/*
	 * Returns a set of test channels with unique names and with 3 properties
	 */
	public static Collection<Channel> getChannels(int channelCount) {
		if (channelCount > 0 && channelCount <= 10000) {
			channels = new ArrayList<Channel>();
			for (int i = 0; i < channelCount; i++) {
				channels.add(getChannel(i));
			}
			return channels;
		} else {
			return null;
		}

	}

	private static Channel getChannel(int i) {
		return channel(getName(i))
				.owner("testOwner")
				.with(property("NumericProp", String.valueOf(i)))
				.with(property("AlphabeticProp",
						generateString(new Random(), characters, 9)))
				.with(property(
						"AlphaNumericProp",
						generateString(new Random(), characters + "0123456789",
								18))).with(property("prop1", "111"))
				.with(property("prop2", "222")).with(property("prop3", "333"))
				.with(property("prop4", "444")).with(tag("tagA"))
				.with(tag("tagB")).build();
	}

	private static String getName(int i) {
		return String.valueOf(i / 1000) + "{" + getName1000(i % 1000) + "}";
	}

	private static String getName1000(int i) {
		if (i < 1000)
			return "first:" + getName500(i);
		else
			return "second:" + getName500(i - 1000);
	}

	private static String getName500(int i) {
		if (i < 500)
			return "a" + getName100(i);
		else
			return "b" + getName100(i - 500);
	}

	private static String getName100(int i) {
		return "<" + Integer.toString(i / 100) + "00>" + getNameID(i % 100);
	}

	private static String getNameID(int i) {
		return ":" + Integer.toString(i / 10) + ":" + Integer.toString(i);
	}

	private static String generateString(Random rng, String characters,
			int length) {
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}

}
