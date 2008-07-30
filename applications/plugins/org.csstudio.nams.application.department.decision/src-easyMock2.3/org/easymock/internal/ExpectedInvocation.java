/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.easymock.IArgumentMatcher;
import org.easymock.internal.matchers.Equals;

public class ExpectedInvocation {

	private final Invocation invocation;

	@SuppressWarnings("deprecation")
	private final org.easymock.ArgumentsMatcher matcher;

	private final List<IArgumentMatcher> matchers;

	public ExpectedInvocation(final Invocation invocation,
			final List<IArgumentMatcher> matchers) {
		this(invocation, matchers, null);
	}

	private ExpectedInvocation(final Invocation invocation,
			final List<IArgumentMatcher> matchers,
			@SuppressWarnings("deprecation")
			final org.easymock.ArgumentsMatcher matcher) {
		this.invocation = invocation;
		this.matcher = matcher;
		this.matchers = (matcher == null) ? this.createMissingMatchers(
				invocation, matchers) : null;
	}

	@Override
	public boolean equals(final Object o) {
		if ((o == null) || !this.getClass().equals(o.getClass())) {
			return false;
		}

		final ExpectedInvocation other = (ExpectedInvocation) o;
		return this.invocation.equals(other.invocation)
				&& (((this.matcher == null) && (other.matcher == null)) || ((this.matcher != null) && this.matcher
						.equals(other.matcher)))
				&& (((this.matchers == null) && (other.matchers == null)) || ((this.matchers != null) && this.matchers
						.equals(other.matchers)));
	}

	public Method getMethod() {
		return this.invocation.getMethod();
	}

	@Override
	public int hashCode() {
		return this.invocation.hashCode();
	}

	public boolean matches(final Invocation actual) {
		return this.matchers != null ? this.invocation.getMock().equals(
				actual.getMock())
				&& this.invocation.getMethod().equals(actual.getMethod())
				&& this.matches(actual.getArguments()) : this.invocation
				.matches(actual, this.matcher);
	}

	@Override
	public String toString() {
		return this.matchers != null ? this.myToString() : this.invocation
				.toString(this.matcher);
	}

	public ExpectedInvocation withMatcher(@SuppressWarnings("deprecation")
	final org.easymock.ArgumentsMatcher matcher) {
		return new ExpectedInvocation(this.invocation, null, matcher);
	}

	private List<IArgumentMatcher> createMissingMatchers(
			final Invocation invocation, final List<IArgumentMatcher> matchers) {
		if (matchers != null) {
			if (matchers.size() != invocation.getArguments().length) {
				throw new IllegalStateException(""
						+ invocation.getArguments().length
						+ " matchers expected, " + matchers.size()
						+ " recorded.");
			}
			;
			return matchers;
		}
		final List<IArgumentMatcher> result = new ArrayList<IArgumentMatcher>();
		for (final Object argument : invocation.getArguments()) {
			result.add(new Equals(argument));
		}
		return result;
	}

	private boolean matches(final Object[] arguments) {
		if (arguments.length != this.matchers.size()) {
			return false;
		}
		for (int i = 0; i < arguments.length; i++) {
			if (!this.matchers.get(i).matches(arguments[i])) {
				return false;
			}
		}
		return true;
	}

	private String myToString() {
		final StringBuffer result = new StringBuffer();
		result.append(this.invocation.getMockAndMethodName());
		result.append("(");
		for (final Iterator<IArgumentMatcher> it = this.matchers.iterator(); it
				.hasNext();) {
			it.next().appendTo(result);
			if (it.hasNext()) {
				result.append(", ");
			}
		}
		result.append(")");
		return result.toString();
	}
}
