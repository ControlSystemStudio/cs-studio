/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import java.util.regex.Pattern;

import org.easymock.IArgumentMatcher;

public class Find implements IArgumentMatcher {

	private final String regex;

	public Find(final String regex) {
		this.regex = regex;
	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append("find(\"" + this.regex.replaceAll("\\\\", "\\\\\\\\")
				+ "\")");
	}

	public boolean matches(final Object actual) {
		return (actual instanceof String)
				&& Pattern.compile(this.regex).matcher((String) actual).find();
	}
}
