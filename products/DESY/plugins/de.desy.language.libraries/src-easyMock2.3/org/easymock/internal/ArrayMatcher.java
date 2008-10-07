/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import org.easymock.AbstractMatcher;
import org.easymock.internal.matchers.ArrayEquals;

public class ArrayMatcher extends AbstractMatcher {
	@Override
	public String argumentToString(final Object argument) {
		final StringBuffer result = new StringBuffer();
		new ArrayEquals(argument).appendTo(result);
		return result.toString();
	}

	@Override
	public boolean argumentMatches(final Object expected, final Object actual) {
		return new ArrayEquals(expected).matches(actual);
	}
}