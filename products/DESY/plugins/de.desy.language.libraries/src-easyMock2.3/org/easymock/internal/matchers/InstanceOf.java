/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import org.easymock.IArgumentMatcher;

public class InstanceOf implements IArgumentMatcher {

	private final Class<?> clazz;

	public InstanceOf(final Class clazz) {
		this.clazz = clazz;
	}

	public boolean matches(final Object actual) {
		return (actual != null)
				&& this.clazz.isAssignableFrom(actual.getClass());
	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append("isA(" + this.clazz.getName() + ")");
	}
}
