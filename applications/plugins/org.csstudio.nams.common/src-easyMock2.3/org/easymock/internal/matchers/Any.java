/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import org.easymock.IArgumentMatcher;

public class Any implements IArgumentMatcher {

	public static final Any ANY = new Any();

	private Any() {

	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append("<any>");
	}

	public boolean matches(final Object actual) {
		return true;
	}
}
