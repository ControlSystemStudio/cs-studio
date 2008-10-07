/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import org.easymock.IArgumentMatcher;

public class NotNull implements IArgumentMatcher {

	public static final NotNull NOT_NULL = new NotNull();

	private NotNull() {

	}

	public boolean matches(final Object actual) {
		return actual != null;
	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append("notNull()");
	}
}
