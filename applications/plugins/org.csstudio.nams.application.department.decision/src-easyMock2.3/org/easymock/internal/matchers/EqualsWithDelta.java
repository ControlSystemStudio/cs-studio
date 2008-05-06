/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import org.easymock.IArgumentMatcher;

public class EqualsWithDelta implements IArgumentMatcher {
	private final Number expected;

	private final Number delta;

	public EqualsWithDelta(final Number value, final Number delta) {
		this.expected = value;
		this.delta = delta;
	}

	public boolean matches(final Object actual) {
		final Number actualNumber = (Number) actual;
		return (this.expected.doubleValue() - this.delta.doubleValue() <= actualNumber
				.doubleValue())
				&& (actualNumber.doubleValue() <= this.expected.doubleValue()
						+ this.delta.doubleValue());
	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append("eq(" + this.expected + ", " + this.delta + ")");
	}
}
