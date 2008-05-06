/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

public class GreaterThan<T extends Comparable<T>> extends CompareTo<T> {

	public GreaterThan(final Comparable<T> value) {
		super(value);
	}

	@Override
	protected String getName() {
		return "gt";
	}

	@Override
	protected boolean matchResult(final int result) {
		return result > 0;
	}
}
