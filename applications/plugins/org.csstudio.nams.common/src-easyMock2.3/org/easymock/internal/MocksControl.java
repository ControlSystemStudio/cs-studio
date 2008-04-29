/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import org.easymock.ArgumentsMatcher;
import org.easymock.IAnswer;
import org.easymock.IExpectationSetters;
import org.easymock.IMocksControl;

public class MocksControl implements IMocksControl {

	private IMocksControlState state;

	private IMocksBehavior behavior;

	public enum MockType {
		NICE, DEFAULT, STRICT
	}

	private final MockType type;

	public MocksControl(final MockType type) {
		this.type = type;
		this.reset();
	}

	public IMocksControlState getState() {
		return this.state;
	}

	public <T> T createMock(final Class<T> toMock) {
		try {
			this.state.assertRecordState();
			final IProxyFactory<T> proxyFactory = this
					.createProxyFactory(toMock);
			return proxyFactory.createProxy(toMock, new ObjectMethodsFilter(
					toMock, new MockInvocationHandler(this), null));
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public <T> T createMock(final String name, final Class<T> toMock) {
		try {
			this.state.assertRecordState();
			final IProxyFactory<T> proxyFactory = this
					.createProxyFactory(toMock);
			return proxyFactory.createProxy(toMock, new ObjectMethodsFilter(
					toMock, new MockInvocationHandler(this), name));
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	protected <T> IProxyFactory<T> createProxyFactory(final Class<T> toMock) {
		return new JavaProxyFactory<T>();
	}

	public final void reset() {
		this.behavior = new MocksBehavior(this.type == MockType.NICE);
		this.behavior.checkOrder(this.type == MockType.STRICT);
		this.state = new RecordState(this.behavior);
		LastControl.reportLastControl(null);
	}

	public void replay() {
		try {
			this.state.replay();
			this.state = new ReplayState(this.behavior);
			LastControl.reportLastControl(null);
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public void verify() {
		try {
			this.state.verify();
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		} catch (final AssertionErrorWrapper e) {
			throw (AssertionError) e.getAssertionError().fillInStackTrace();
		}
	}

	public void checkOrder(final boolean value) {
		try {
			this.state.checkOrder(value);
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	// methods from IBehaviorSetters

	public IExpectationSetters andReturn(final Object value) {
		try {
			this.state.andReturn(value);
			return this;
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public IExpectationSetters andThrow(final Throwable throwable) {
		try {
			this.state.andThrow(throwable);
			return this;
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public IExpectationSetters andAnswer(final IAnswer answer) {
		try {
			this.state.andAnswer(answer);
			return this;
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public void andStubReturn(final Object value) {
		try {
			this.state.andStubReturn(value);
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public void andStubThrow(final Throwable throwable) {
		try {
			this.state.andStubThrow(throwable);
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public void andStubAnswer(final IAnswer answer) {
		try {
			this.state.andStubAnswer(answer);
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public void asStub() {
		try {
			this.state.asStub();
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public IExpectationSetters times(final int times) {
		try {
			this.state.times(new Range(times));
			return this;
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public IExpectationSetters times(final int min, final int max) {
		try {
			this.state.times(new Range(min, max));
			return this;
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public IExpectationSetters once() {
		try {
			this.state.times(MocksControl.ONCE);
			return this;
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public IExpectationSetters atLeastOnce() {
		try {
			this.state.times(MocksControl.AT_LEAST_ONCE);
			return this;
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public IExpectationSetters anyTimes() {
		try {
			this.state.times(MocksControl.ZERO_OR_MORE);
			return this;
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	/**
	 * Exactly one call.
	 */
	public static final Range ONCE = new Range(1);

	/**
	 * One or more calls.
	 */
	public static final Range AT_LEAST_ONCE = new Range(1, Integer.MAX_VALUE);

	/**
	 * Zero or more calls.
	 */
	public static final Range ZERO_OR_MORE = new Range(0, Integer.MAX_VALUE);

	public void setLegacyDefaultMatcher(final ArgumentsMatcher matcher) {
		try {
			this.state.setDefaultMatcher(matcher);
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public void setLegacyMatcher(final ArgumentsMatcher matcher) {
		try {
			this.state.setMatcher(null, matcher);
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public void setLegacyDefaultReturnValue(final Object value) {
		try {
			this.state.setDefaultReturnValue(value);
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}

	public void setLegacyDefaultVoidCallable() {
		this.state.setDefaultVoidCallable();
	}

	public void setLegacyDefaultThrowable(final Throwable throwable) {
		try {
			this.state.setDefaultThrowable(throwable);
		} catch (final RuntimeExceptionWrapper e) {
			throw (RuntimeException) e.getRuntimeException().fillInStackTrace();
		}
	}
}
