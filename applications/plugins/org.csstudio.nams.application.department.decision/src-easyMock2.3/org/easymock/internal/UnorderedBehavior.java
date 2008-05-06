/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import java.util.ArrayList;
import java.util.List;

public class UnorderedBehavior {

	private final List<ExpectedInvocationAndResults> results = new ArrayList<ExpectedInvocationAndResults>();

	private final boolean checkOrder;

	public UnorderedBehavior(final boolean checkOrder) {
		this.checkOrder = checkOrder;
	}

	public void addExpected(final ExpectedInvocation expected,
			final Result result, final Range count) {
		for (final ExpectedInvocationAndResults entry : this.results) {
			if (entry.getExpectedInvocation().equals(expected)) {
				entry.getResults().add(result, count);
				return;
			}
		}
		final Results list = new Results();
		list.add(result, count);
		this.results.add(new ExpectedInvocationAndResults(expected, list));
	}

	public Result addActual(final Invocation actual) {
		for (final ExpectedInvocationAndResults entry : this.results) {
			if (!entry.getExpectedInvocation().matches(actual)) {
				continue;
			}
			final Result result = entry.getResults().next();
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public boolean verify() {
		for (final ExpectedInvocationAndResults entry : this.results) {
			if (!entry.getResults().hasValidCallCount()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return this.toString(null);
	}

	public String toString(final Invocation invocation) {
		final StringBuffer result = new StringBuffer();
		for (final ExpectedInvocationAndResults entry : this.results) {
			final boolean unordered = !this.checkOrder;
			final boolean validCallCount = entry.getResults()
					.hasValidCallCount();
			boolean match = (invocation != null)
					&& entry.getExpectedInvocation().matches(invocation);

			if (unordered && validCallCount && !match) {
				continue;
			}
			result.append("\n    " + entry.toString());
			if (match) {
				result.append(" (+1)");
			}
		}
		return result.toString();
	}

	public boolean allowsExpectedInvocation(final ExpectedInvocation expected,
			final boolean checkOrder) {
		if (this.checkOrder != checkOrder) {
			return false;
		} else if (this.results.isEmpty() || !this.checkOrder) {
			return true;
		} else {
			final ExpectedInvocation lastMethodCall = this.results.get(
					this.results.size() - 1).getExpectedInvocation();
			return lastMethodCall.equals(expected);
		}
	}

}