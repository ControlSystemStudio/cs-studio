/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import org.easymock.IArgumentMatcher;

public class StartsWith implements IArgumentMatcher {

	private final String prefix;

	public StartsWith(final String prefix) {
		this.prefix = prefix;
	}

	public boolean matches(final Object actual) {
		return (actual instanceof String)
				&& ((String) actual).startsWith(this.prefix);
	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append("startsWith(\"" + this.prefix + "\")");
	}
}
