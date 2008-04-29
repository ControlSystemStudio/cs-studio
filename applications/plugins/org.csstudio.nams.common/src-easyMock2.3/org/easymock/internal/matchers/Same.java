/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import org.easymock.IArgumentMatcher;

public class Same implements IArgumentMatcher {

	private final Object expected;

	public Same(final Object expected) {
		this.expected = expected;
	}

	public boolean matches(final Object actual) {
		return this.expected == actual;
	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append("same(");
		this.appendQuoting(buffer);
		buffer.append(this.expected);
		this.appendQuoting(buffer);
		buffer.append(")");
	}

	private void appendQuoting(final StringBuffer buffer) {
		if (this.expected instanceof String) {
			buffer.append("\"");
		} else if (this.expected instanceof Character) {
			buffer.append("'");
		}
	}
}
