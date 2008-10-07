/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import java.util.Iterator;
import java.util.List;

import org.easymock.IArgumentMatcher;

public class And implements IArgumentMatcher {

	private final List<IArgumentMatcher> matchers;

	public And(final List<IArgumentMatcher> matchers) {
		this.matchers = matchers;
	}

	public boolean matches(final Object actual) {
		for (final IArgumentMatcher matcher : this.matchers) {
			if (!matcher.matches(actual)) {
				return false;
			}
		}
		return true;
	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append("and(");
		for (final Iterator<IArgumentMatcher> it = this.matchers.iterator(); it
				.hasNext();) {
			it.next().appendTo(buffer);
			if (it.hasNext()) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
	}
}
