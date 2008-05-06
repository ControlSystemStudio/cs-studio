/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import org.easymock.IArgumentMatcher;

public abstract class CompareTo<T extends Comparable<T>> implements
		IArgumentMatcher {
	private final Comparable<T> expected;

	public CompareTo(final Comparable<T> value) {
		this.expected = value;
	}

	@SuppressWarnings("unchecked")
	public boolean matches(final Object actual) {

		if (!(actual instanceof Comparable)) {
			return false;
		}

		return this.matchResult(((Comparable) actual).compareTo(this.expected));
	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append(this.getName() + "(" + this.expected + ")");
	}

	protected abstract String getName();

	protected abstract boolean matchResult(int result);
}
