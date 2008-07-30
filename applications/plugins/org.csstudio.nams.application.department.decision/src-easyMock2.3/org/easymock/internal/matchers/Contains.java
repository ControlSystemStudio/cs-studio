/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import org.easymock.IArgumentMatcher;

public class Contains implements IArgumentMatcher {

	private final String substring;

	public Contains(final String substring) {
		this.substring = substring;
	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append("contains(\"" + this.substring + "\")");
	}

	public boolean matches(final Object actual) {
		return (actual instanceof String)
				&& (((String) actual).indexOf(this.substring) >= 0);
	}
}
