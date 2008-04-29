/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

public class LessThan<T extends Comparable<T>> extends CompareTo<T> {

	public LessThan(final Comparable<T> value) {
		super(value);
	}

	@Override
	protected String getName() {
		return "lt";
	}

	@Override
	protected boolean matchResult(final int result) {
		return result < 0;
	}
}
