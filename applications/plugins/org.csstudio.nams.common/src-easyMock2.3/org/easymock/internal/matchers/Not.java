/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import org.easymock.IArgumentMatcher;

public class Not implements IArgumentMatcher {

	private final IArgumentMatcher first;

	public Not(final IArgumentMatcher first) {
		this.first = first;
	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append("not(");
		this.first.appendTo(buffer);
		buffer.append(")");
	}

	public boolean matches(final Object actual) {
		return !this.first.matches(actual);
	}
}
