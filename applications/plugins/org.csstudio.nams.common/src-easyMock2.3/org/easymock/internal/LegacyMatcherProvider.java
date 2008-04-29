/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.easymock.ArgumentsMatcher;
import org.easymock.MockControl;

public class LegacyMatcherProvider {

	private ArgumentsMatcher defaultMatcher;

	private boolean defaultMatcherSet;

	private final Map<Method, ArgumentsMatcher> matchers = new HashMap<Method, ArgumentsMatcher>();

	public ArgumentsMatcher getMatcher(final Method method) {
		if (!this.matchers.containsKey(method)) {
			if (!this.defaultMatcherSet) {
				this.setDefaultMatcher(MockControl.EQUALS_MATCHER);
			}
			this.matchers.put(method, this.defaultMatcher);
		}
		return this.matchers.get(method);
	}

	public void setDefaultMatcher(final ArgumentsMatcher matcher) {
		if (this.defaultMatcherSet) {
			throw new RuntimeExceptionWrapper(
					new IllegalStateException(
							"default matcher can only be set once directly after creation of the MockControl"));
		}
		this.defaultMatcher = matcher;
		this.defaultMatcherSet = true;
	}

	public void setMatcher(final Method method, final ArgumentsMatcher matcher) {
		if (this.matchers.containsKey(method)
				&& (this.matchers.get(method) != matcher)) {
			throw new RuntimeExceptionWrapper(new IllegalStateException(
					"for method "
							+ method.getName()
							+ "("
							+ (method.getParameterTypes().length == 0 ? ""
									: "...")
							+ "), a matcher has already been set"));
		}
		this.matchers.put(method, matcher);
	}
}
