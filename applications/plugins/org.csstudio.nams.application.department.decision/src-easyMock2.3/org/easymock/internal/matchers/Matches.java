/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import org.easymock.IArgumentMatcher;

public class Matches implements IArgumentMatcher {

	private final String regex;

	public Matches(final String regex) {
		this.regex = regex;
	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append("matches(\"" + this.regex.replaceAll("\\\\", "\\\\\\\\")
				+ "\")");
	}

	public boolean matches(final Object actual) {
		return (actual instanceof String)
				&& ((String) actual).matches(this.regex);
	}
}
