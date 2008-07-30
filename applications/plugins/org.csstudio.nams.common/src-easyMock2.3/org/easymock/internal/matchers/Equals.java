/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import org.easymock.IArgumentMatcher;

public class Equals implements IArgumentMatcher {

	private final Object expected;

	public Equals(final Object expected) {
		this.expected = expected;
	}

	public void appendTo(final StringBuffer buffer) {
		this.appendQuoting(buffer);
		buffer.append(this.expected);
		this.appendQuoting(buffer);
	}

	@Override
	public boolean equals(final Object o) {
		if ((o == null) || !this.getClass().equals(o.getClass())) {
			return false;
		}
		final Equals other = (Equals) o;
		return ((this.expected == null) && (other.expected == null))
				|| ((this.expected != null) && this.expected
						.equals(other.expected));
	}

	@Override
	public int hashCode() {
		throw new UnsupportedOperationException("hashCode() is not supported");
	}

	public boolean matches(final Object actual) {
		if (this.expected == null) {
			return actual == null;
		}
		return this.expected.equals(actual);
	}

	protected final Object getExpected() {
		return this.expected;
	}

	private void appendQuoting(final StringBuffer buffer) {
		if (this.expected instanceof String) {
			buffer.append("\"");
		} else if (this.expected instanceof Character) {
			buffer.append("'");
		}
	}

}
