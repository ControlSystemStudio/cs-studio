/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.easymock.ArgumentsMatcher;
import org.easymock.MockControl;

public class MocksBehavior implements IMocksBehavior {

	private final List<UnorderedBehavior> behaviorLists = new ArrayList<UnorderedBehavior>();

	private final List<ExpectedInvocationAndResult> stubResults = new ArrayList<ExpectedInvocationAndResult>();

	private final boolean nice;

	private boolean checkOrder;

	private int position = 0;

	public MocksBehavior(final boolean nice) {
		this.nice = nice;
	}

	public final void addStub(final ExpectedInvocation expected,
			final Result result) {
		this.stubResults.add(new ExpectedInvocationAndResult(expected, result));
	}

	public void addExpected(final ExpectedInvocation expected,
			final Result result, final Range count) {
		ExpectedInvocation localExpected = expected;
		if (this.legacyMatcherProvider != null) {
			localExpected = localExpected
					.withMatcher(this.legacyMatcherProvider
							.getMatcher(localExpected.getMethod()));
		}
		this.addBehaviorListIfNecessary(localExpected);
		this.lastBehaviorList().addExpected(localExpected, result, count);
	}

	private final Result getStubResult(final Invocation actual) {
		for (final ExpectedInvocationAndResult each : this.stubResults) {
			if (each.getExpectedInvocation().matches(actual)) {
				return each.getResult();
			}
		}
		return null;
	}

	private void addBehaviorListIfNecessary(final ExpectedInvocation expected) {
		if (this.behaviorLists.isEmpty()
				|| !this.lastBehaviorList().allowsExpectedInvocation(expected,
						this.checkOrder)) {
			this.behaviorLists.add(new UnorderedBehavior(this.checkOrder));
		}
	}

	private UnorderedBehavior lastBehaviorList() {
		return this.behaviorLists.get(this.behaviorLists.size() - 1);
	}

	public final Result addActual(final Invocation actual) {
		final int tempPosition = this.position;
		String errorMessage = "";
		while (this.position < this.behaviorLists.size()) {
			final Result result = this.behaviorLists.get(this.position)
					.addActual(actual);
			if (result != null) {
				return result;
			}
			errorMessage += this.behaviorLists.get(this.position).toString(
					actual);
			if (!this.behaviorLists.get(this.position).verify()) {
				break;
			}
			this.position++;
		}
		Result stubOrNice = this.getStubResult(actual);
		if ((stubOrNice == null) && this.nice) {
			stubOrNice = Result.createReturnResult(RecordState
					.emptyReturnValueFor(actual.getMethod().getReturnType()));
		}
		if (stubOrNice != null) {
			this.position = tempPosition;
			return stubOrNice;
		}
		throw new AssertionErrorWrapper(new AssertionError(
				"\n  Unexpected method call "
						+ actual.toString(MockControl.EQUALS_MATCHER) + ":"
						+ errorMessage.toString()));
	}

	public void verify() {
		boolean verified = true;
		final StringBuffer errorMessage = new StringBuffer();

		for (final UnorderedBehavior behaviorList : this.behaviorLists.subList(
				this.position, this.behaviorLists.size())) {
			errorMessage.append(behaviorList.toString());
			if (!behaviorList.verify()) {
				verified = false;
			}
		}
		if (verified) {
			return;
		}

		throw new AssertionErrorWrapper(new AssertionError(
				"\n  Expectation failure on verify:" + errorMessage.toString()));
	}

	public void checkOrder(final boolean value) {
		this.checkOrder = value;
	}

	private LegacyMatcherProvider legacyMatcherProvider;

	public LegacyMatcherProvider getLegacyMatcherProvider() {
		if (this.legacyMatcherProvider == null) {
			this.legacyMatcherProvider = new LegacyMatcherProvider();
		}
		return this.legacyMatcherProvider;
	}

	public void setDefaultMatcher(final ArgumentsMatcher matcher) {
		this.getLegacyMatcherProvider().setDefaultMatcher(matcher);
	}

	public void setMatcher(final Method method, final ArgumentsMatcher matcher) {
		this.getLegacyMatcherProvider().setMatcher(method, matcher);
	}
}
