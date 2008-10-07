/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import org.easymock.AbstractMatcher;

public class AlwaysMatcher extends AbstractMatcher {
	@Override
	public boolean matches(final Object[] expected, final Object[] actual) {
		return true;
	}

	@Override
	protected String argumentToString(final Object argument) {
		return "<any>";
	}
}