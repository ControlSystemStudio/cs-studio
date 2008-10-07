/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

public class ExpectedInvocationAndResults {
	ExpectedInvocation expectedInvocation;

	Results results;

	public ExpectedInvocationAndResults(
			final ExpectedInvocation expectedInvocation, final Results results) {
		this.expectedInvocation = expectedInvocation;
		this.results = results;
	}

	public ExpectedInvocation getExpectedInvocation() {
		return this.expectedInvocation;
	}

	public Results getResults() {
		return this.results;
	}

	@Override
	public String toString() {
		return this.expectedInvocation.toString() + ": "
				+ this.results.toString();
	}
}