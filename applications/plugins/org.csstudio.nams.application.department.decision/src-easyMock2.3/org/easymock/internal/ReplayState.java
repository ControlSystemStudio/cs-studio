/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import java.lang.reflect.Method;

import org.easymock.ArgumentsMatcher;
import org.easymock.IAnswer;

public class ReplayState implements IMocksControlState {

	private final IMocksBehavior behavior;

	public ReplayState(final IMocksBehavior behavior) {
		this.behavior = behavior;
	}

	public void andAnswer(final IAnswer answer) {
		this.throwWrappedIllegalStateException();
	}

	public void andReturn(final Object value) {
		this.throwWrappedIllegalStateException();
	}

	public void andStubAnswer(final IAnswer answer) {
		this.throwWrappedIllegalStateException();
	}

	public void andStubReturn(final Object value) {
		this.throwWrappedIllegalStateException();
	}

	public void andStubThrow(final Throwable throwable) {
		this.throwWrappedIllegalStateException();
	}

	public void andThrow(final Throwable throwable) {
		this.throwWrappedIllegalStateException();
	}

	public void assertRecordState() {
		this.throwWrappedIllegalStateException();
	}

	public void asStub() {
		this.throwWrappedIllegalStateException();
	}

	public void callback(final Runnable runnable) {
		this.throwWrappedIllegalStateException();
	}

	public void checkOrder(final boolean value) {
		this.throwWrappedIllegalStateException();
	}

	public Object invoke(final Invocation invocation) throws Throwable {
		final Result result = this.behavior.addActual(invocation);
		LastControl.pushCurrentArguments(invocation.getArguments());
		try {
			try {
				return result.answer();
			} catch (final Throwable t) {
				throw new ThrowableWrapper(t);
			}
		} finally {
			LastControl.popCurrentArguments();
		}
	}

	public void replay() {
		this.throwWrappedIllegalStateException();
	}

	public void setDefaultMatcher(final ArgumentsMatcher matcher) {
		this.throwWrappedIllegalStateException();
	}

	public void setDefaultReturnValue(final Object value) {
		this.throwWrappedIllegalStateException();
	}

	public void setDefaultThrowable(final Throwable throwable) {
		this.throwWrappedIllegalStateException();
	}

	public void setDefaultVoidCallable() {
		this.throwWrappedIllegalStateException();
	}

	public void setMatcher(final Method method, final ArgumentsMatcher matcher) {
		this.throwWrappedIllegalStateException();
	}

	public void times(final Range range) {
		this.throwWrappedIllegalStateException();
	}

	public void verify() {
		this.behavior.verify();
	}

	private void throwWrappedIllegalStateException() {
		throw new RuntimeExceptionWrapper(new IllegalStateException(
				"This method must not be called in replay state."));
	}
}
