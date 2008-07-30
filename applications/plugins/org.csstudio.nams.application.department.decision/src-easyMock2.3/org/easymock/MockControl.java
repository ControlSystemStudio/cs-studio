/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock;

import org.easymock.internal.AlwaysMatcher;
import org.easymock.internal.ArrayMatcher;
import org.easymock.internal.EqualsMatcher;
import org.easymock.internal.MocksControl;
import org.easymock.internal.Range;

/**
 * A <code>MockControl</code> object controls the behavior of its associated
 * mock object. For more information, see the EasyMock documentation.
 * 
 * @deprecated Since EasyMock 2.0, static methods on <code>EasyMock</code> are
 *             used to create and control mock objects.
 */
@Deprecated
public class MockControl<T> {
	/**
	 * Exactly one call.
	 */
	public static final Range ONE = MocksControl.ONCE;

	/**
	 * One or more calls.
	 */
	public static final Range ONE_OR_MORE = MocksControl.AT_LEAST_ONCE;

	/**
	 * Zero or more calls.
	 */
	public static final Range ZERO_OR_MORE = MocksControl.ZERO_OR_MORE;

	/**
	 * Matches if each expected argument is equal to the corresponding actual
	 * argument.
	 */
	public static final ArgumentsMatcher EQUALS_MATCHER = new EqualsMatcher();

	/**
	 * Matches always.
	 */
	public static final ArgumentsMatcher ALWAYS_MATCHER = new AlwaysMatcher();

	/**
	 * Matches if each expected argument is equal to the corresponding actual
	 * argument for non-array arguments; array arguments are compared with the
	 * appropriate <code>java.util.Arrays.equals()</code> -method.
	 */
	public static final ArgumentsMatcher ARRAY_MATCHER = new ArrayMatcher();

	/**
	 * Creates a mock control object for the specified interface. The
	 * <code>MockControl</code> and its associated mock object will not check
	 * the order of expected method calls. An unexpected method call on the mock
	 * object will lead to an <code>AssertionError</code>.
	 * 
	 * @param toMock
	 *            the class of the interface to mock.
	 * @return the mock control.
	 */
	public static <T> MockControl<T> createControl(final Class<T> toMock) {
		return new MockControl<T>((MocksControl) EasyMock.createControl(),
				toMock);
	}

	/**
	 * Creates a mock control object for the specified interface. The
	 * <code>MockControl</code> and its associated mock object will not check
	 * the order of expected method calls. An unexpected method call on the mock
	 * object will return an empty value (0, null, false).
	 * 
	 * @param toMock
	 *            the class of the interface to mock.
	 * @return the mock control.
	 */
	public static <T> MockControl<T> createNiceControl(final Class<T> toMock) {
		return new MockControl<T>((MocksControl) EasyMock.createNiceControl(),
				toMock);
	}

	/**
	 * Creates a mock control object for the specified interface. The
	 * <code>MockControl</code> and its associated mock object will check the
	 * order of expected method calls. An unexpected method call on the mock
	 * object will lead to an <code>AssertionError</code>.
	 * 
	 * @param toMock
	 *            the class of the interface to mock.
	 * @return the mock control.
	 */
	public static <T> MockControl<T> createStrictControl(final Class<T> toMock) {
		return new MockControl<T>(
				(MocksControl) EasyMock.createStrictControl(), toMock);
	}

	private final T mock;

	private final MocksControl ctrl;

	protected MockControl(final MocksControl ctrl, final Class<T> toMock) {
		this.ctrl = ctrl;
		this.mock = ctrl.createMock(toMock);
	}

	/**
	 * Same as {@link MockControl#setDefaultReturnValue(Object)}. For
	 * explanation, see "Convenience Methods for Return Values" in the EasyMock
	 * documentation.
	 * 
	 * @param ignored
	 *            an ignored value.
	 */
	public <V1, V2 extends V1> void expectAndDefaultReturn(final V1 ignored,
			final V2 value) {
		EasyMock.expectLastCall().andStubReturn(value);
	}

	/**
	 * Same as {@link MockControl#setDefaultThrowable(Throwable)}. For
	 * explanation, see "Convenience Methods for Throwables" in the EasyMock
	 * documentation.
	 * 
	 * @param ignored
	 *            an ignored value.
	 */
	public void expectAndDefaultThrow(final Object ignored,
			final Throwable throwable) {
		this
				.expectLastCall(
						"method call on the mock needed before setting default Throwable")
				.andStubThrow(throwable);
	}

	public void expectAndReturn(final int ignored, final int value) {
		this.expectAndReturn((Object) ignored, (Object) value);
	}

	public void expectAndReturn(final int ignored, final int value,
			final int count) {
		this.expectAndReturn((Object) ignored, (Object) value, count);
	}

	public void expectAndReturn(final int ignored, final int value,
			final int min, final int max) {
		this.expectAndReturn((Object) ignored, (Object) value, min, max);
	}

	public void expectAndReturn(final int ignored, final int value,
			final Range range) {
		this.expectAndReturn((Object) ignored, (Object) value, range);
	}

	/**
	 * Same as {@link MockControl#setReturnValue(Object)}. For explanation, see
	 * "Convenience Methods for Return Values" in the EasyMock documentation.
	 * 
	 * @param ignored
	 *            an ignored value.
	 */
	public <V1, V2 extends V1> void expectAndReturn(final V1 ignored,
			final V2 value) {
		EasyMock.expectLastCall().andReturn(value).once();
	}

	/**
	 * Same as {@link MockControl#setReturnValue(Object, int)}. For
	 * explanation, see "Convenience Methods for Return Values" in the EasyMock
	 * documentation.
	 * 
	 * @param ignored
	 *            an ignored value.
	 */
	public <V1, V2 extends V1> void expectAndReturn(final V1 ignored,
			final V2 value, final int count) {
		EasyMock.expectLastCall().andReturn(value).times(count);
	}

	/**
	 * Same as {@link MockControl#setReturnValue(Object, int, int)}. For
	 * explanation, see "Convenience Methods for Return Values" in the EasyMock
	 * documentation.
	 * 
	 * @param ignored
	 *            an ignored value.
	 */
	public <V1, V2 extends V1> void expectAndReturn(final V1 ignored,
			final V2 value, final int min, final int max) {
		EasyMock.expectLastCall().andReturn(value).times(min, max);
	}

	/**
	 * Same as {@link MockControl#setReturnValue(Object, Range)}. For
	 * explanation, see "Convenience Methods for Return Values" in the EasyMock
	 * documentation.
	 * 
	 * @param ignored
	 *            an ignored value.
	 */
	public <V1, V2 extends V1> void expectAndReturn(final V1 ignored,
			final V2 value, final Range range) {
		final IExpectationSetters expectAndReturn = EasyMock.expectLastCall()
				.andReturn(value);
		this.callWithConvertedRange(expectAndReturn, range);
	}

	/**
	 * Same as {@link MockControl#setThrowable(Throwable)}. For explanation,
	 * see "Convenience Methods for Throwables" in the EasyMock documentation.
	 * 
	 * @param ignored
	 *            an ignored value.
	 */
	public void expectAndThrow(final Object ignored, final Throwable throwable) {
		EasyMock.expect(ignored).andThrow(throwable).once();
	}

	/**
	 * Same as {@link MockControl#setThrowable(Throwable, int)}. For
	 * explanation, see "Convenience Methods for Throwables" in the EasyMock
	 * documentation.
	 * 
	 * @param ignored
	 *            an ignored value.
	 */
	public void expectAndThrow(final Object ignored, final Throwable throwable,
			final int count) {
		EasyMock.expect(ignored).andThrow(throwable).times(count);
	}

	/**
	 * Same as {@link MockControl#setThrowable(Throwable, int, int)}. For
	 * explanation, see "Convenience Methods for Throwables" in the EasyMock
	 * documentation.
	 * 
	 * @param ignored
	 *            an ignored value.
	 */
	public void expectAndThrow(final Object ignored, final Throwable throwable,
			final int min, final int max) {
		EasyMock.expect(ignored).andThrow(throwable).times(min, max);
	}

	/**
	 * Same as {@link MockControl#setThrowable(Throwable, Range)}. For
	 * explanation, see "Convenience Methods for Throwables" in the EasyMock
	 * documentation.
	 * 
	 * @param ignored
	 *            an ignored value.
	 */
	public void expectAndThrow(final Object ignored, final Throwable throwable,
			final Range range) {
		final IExpectationSetters setter = EasyMock.expect(ignored).andThrow(
				throwable);
		this.callWithConvertedRange(setter, range);
	}

	/**
	 * Returns the mock object.
	 * 
	 * @return the mock object of this control
	 */
	public T getMock() {
		return this.mock;
	}

	/**
	 * Switches the mock object from record state to replay state. For more
	 * information, see the EasyMock documentation.
	 * 
	 * @throws IllegalStateException
	 *             if the mock object already is in replay state.
	 */
	public void replay() {
		this.ctrl.replay();
	}

	/**
	 * Resets the mock control and the mock object to the state directly after
	 * creation.
	 */
	public final void reset() {
		this.ctrl.reset();
	}

	/**
	 * Sets the default ArgumentsMatcher for all methods of the mock object. The
	 * matcher must be set before any behavior is defined on the mock object.
	 * 
	 * @throws IllegalStateException
	 *             if called in replay state, or if any behavior is already
	 *             defined on the mock object.
	 */
	public void setDefaultMatcher(final ArgumentsMatcher matcher) {
		this.ctrl.setLegacyDefaultMatcher(matcher);
	}

	/**
	 * Records that the mock object will by default allow the last method
	 * specified by a method call, and will react by returning the provided
	 * return value.
	 * 
	 * @param value
	 *            the return value.
	 * @throws IllegalStateException
	 *             if the mock object is in replay state, if no method was
	 *             called on the mock object before. or if the last method
	 *             called on the mock does not return <code>boolean</code>.
	 */
	public void setDefaultReturnValue(final Object value) {
		this.ctrl.setLegacyDefaultReturnValue(value);
	}

	/**
	 * Records that the mock object will by default allow the last method
	 * specified by a method call, and will react by throwing the provided
	 * Throwable.
	 * 
	 * @param throwable
	 *            throwable the throwable to be thrown
	 * @exception IllegalArgumentException
	 *                if the last method called on the mock cannot throw the
	 *                provided Throwable.
	 * @exception NullPointerException
	 *                if throwable is null.
	 * @exception IllegalStateException
	 *                if the mock object is in replay state, or if no method was
	 *                called on the mock object before.
	 */
	public void setDefaultThrowable(final Throwable throwable) {
		this.ctrl.setLegacyDefaultThrowable(throwable);
	}

	/**
	 * Records that the mock object will by default allow the last method
	 * specified by a method call.
	 * 
	 * @exception IllegalStateException
	 *                if the mock object is in replay state, if no method was
	 *                called on the mock object before, or if the last method
	 *                called on the mock was no void method.
	 */
	public void setDefaultVoidCallable() {
		((MocksControl) this
				.expectLastCall("method call on the mock needed before setting default void callable"))
				.setLegacyDefaultVoidCallable();
	}

	/**
	 * Sets the ArgumentsMatcher for the last method called on the mock object.
	 * The matcher must be set before any behavior for the method is defined.
	 * 
	 * @throws IllegalStateException
	 *             if called in replay state, or if no method was called on the
	 *             mock object before.
	 */
	public void setMatcher(final ArgumentsMatcher matcher) {
		this.ctrl.setLegacyMatcher(matcher);
	}

	/**
	 * Records that the mock object will expect the last method call once, and
	 * will react by returning the provided return value.
	 * 
	 * @param value
	 *            the return value.
	 * @throws IllegalStateException
	 *             if the mock object is in replay state, if no method was
	 *             called on the mock object before. or if the last method
	 *             called on the mock does not return <code>boolean</code>.
	 */
	public void setReturnValue(final Object value) {
		this.expectLastCall(
				"method call on the mock needed before setting return value")
				.andReturn(value).once();
	}

	/**
	 * Records that the mock object will expect the last method call a fixed
	 * number of times, and will react by returning the provided return value.
	 * 
	 * @param value
	 *            the return value.
	 * @param times
	 *            the number of times that the call is expected.
	 * @throws IllegalStateException
	 *             if the mock object is in replay state, if no method was
	 *             called on the mock object before. or if the last method
	 *             called on the mock does not return <code>boolean</code>.
	 */
	public void setReturnValue(final Object value, final int times) {
		this.expectLastCall(
				"method call on the mock needed before setting return value")
				.andReturn(value).times(times);
	}

	/**
	 * Records that the mock object will expect the last method call between
	 * <code>minCount</code> and <code>maxCount</code> times, and will react
	 * by returning the provided return value.
	 * 
	 * @param value
	 *            the return value.
	 * @param minCount
	 *            the minimum number of times that the call is expected.
	 * @param maxCount
	 *            the maximum number of times that the call is expected.
	 * @throws IllegalStateException
	 *             if the mock object is in replay state, if no method was
	 *             called on the mock object before. or if the last method
	 *             called on the mock does not return <code>boolean</code>.
	 */
	public void setReturnValue(final Object value, final int minCount,
			final int maxCount) {
		this.expectLastCall(
				"method call on the mock needed before setting return value")
				.andReturn(value).times(minCount, maxCount);
	}

	/**
	 * Records that the mock object will expect the last method call a fixed
	 * number of times, and will react by returning the provided return value.
	 * 
	 * @param value
	 *            the return value.
	 * @param range
	 *            the number of times that the call is expected.
	 * @throws IllegalStateException
	 *             if the mock object is in replay state, if no method was
	 *             called on the mock object before. or if the last method
	 *             called on the mock does not return <code>boolean</code>.
	 */
	public void setReturnValue(final Object value, final Range range) {
		final IExpectationSetters setter = this.expectLastCall(
				"method call on the mock needed before setting return value")
				.andReturn(value);
		this.callWithConvertedRange(setter, range);
	}

	/**
	 * Records that the mock object will expect the last method call once, and
	 * will react by throwing the provided Throwable.
	 * 
	 * @param throwable
	 *            the Throwable to throw.
	 * @exception IllegalStateException
	 *                if the mock object is in replay state or if no method was
	 *                called on the mock object before.
	 * @exception IllegalArgumentException
	 *                if the last method called on the mock cannot throw the
	 *                provided Throwable.
	 * @exception NullPointerException
	 *                if throwable is null.
	 */
	public void setThrowable(final Throwable throwable) {
		this.expectLastCall(
				"method call on the mock needed before setting Throwable")
				.andThrow(throwable).once();
	}

	/**
	 * Records that the mock object will expect the last method call a fixed
	 * number of times, and will react by throwing the provided Throwable.
	 * 
	 * @param throwable
	 *            the Throwable to throw.
	 * @param times
	 *            the number of times that the call is expected.
	 * @exception IllegalStateException
	 *                if the mock object is in replay state or if no method was
	 *                called on the mock object before.
	 * @exception IllegalArgumentException
	 *                if the last method called on the mock cannot throw the
	 *                provided Throwable.
	 * @exception NullPointerException
	 *                if throwable is null.
	 */
	public void setThrowable(final Throwable throwable, final int times) {
		this.expectLastCall(
				"method call on the mock needed before setting Throwable")
				.andThrow(throwable).times(times);
	}

	/**
	 * Records that the mock object will expect the last method call between
	 * <code>minCount</code> and <code>maxCount</code> times, and will react
	 * by throwing the provided Throwable.
	 * 
	 * @param throwable
	 *            the Throwable to throw.
	 * @param minCount
	 *            the minimum number of times that the call is expected.
	 * @param maxCount
	 *            the maximum number of times that the call is expected.
	 * @exception IllegalStateException
	 *                if the mock object is in replay state or if no method was
	 *                called on the mock object before.
	 * @exception IllegalArgumentException
	 *                if the last method called on the mock cannot throw the
	 *                provided Throwable.
	 * @exception NullPointerException
	 *                if throwable is null.
	 */
	public void setThrowable(final Throwable throwable, final int minCount,
			final int maxCount) {
		this.expectLastCall(
				"method call on the mock needed before setting Throwable")
				.andThrow(throwable).times(minCount, maxCount);
	}

	public void setThrowable(final Throwable throwable, final Range range) {
		final IExpectationSetters setter = this.expectLastCall(
				"method call on the mock needed before setting Throwable")
				.andThrow(throwable);
		this.callWithConvertedRange(setter, range);
	}

	/**
	 * Records that the mock object will expect the last method call once, and
	 * will react by returning silently.
	 * 
	 * @exception IllegalStateException
	 *                if the mock object is in replay state, if no method was
	 *                called on the mock object before, or if the last method
	 *                called on the mock was no void method.
	 */
	public void setVoidCallable() {
		this.expectLastCall(
				"method call on the mock needed before setting void callable")
				.once();
	}

	/**
	 * Records that the mock object will expect the last method call a fixed
	 * number of times, and will react by returning silently.
	 * 
	 * @param times
	 *            the number of times that the call is expected.
	 * @exception IllegalStateException
	 *                if the mock object is in replay state, if no method was
	 *                called on the mock object before, or if the last method
	 *                called on the mock was no void method.
	 */
	public void setVoidCallable(final int times) {
		this.expectLastCall(
				"method call on the mock needed before setting void callable")
				.times(times);
	}

	/**
	 * Records that the mock object will expect the last method call between
	 * <code>minCount</code> and <code>maxCount</code> times, and will react
	 * by returning silently.
	 * 
	 * @param minCount
	 *            the minimum number of times that the call is expected.
	 * @param maxCount
	 *            the maximum number of times that the call is expected.
	 * @exception IllegalStateException
	 *                if the mock object is in replay state, if no method was
	 *                called on the mock object before, or if the last method
	 *                called on the mock was no void method.
	 */
	public void setVoidCallable(final int minCount, final int maxCount) {
		this.expectLastCall(
				"method call on the mock needed before setting void callable")
				.times(minCount, maxCount);
	}

	public void setVoidCallable(final Range range) {
		final IExpectationSetters setter = this
				.expectLastCall("method call on the mock needed before setting void callable");
		this.callWithConvertedRange(setter, range);
	}

	/**
	 * Verifies that all expectations have been met. For more information, see
	 * the EasyMock documentation.
	 * 
	 * @throws IllegalStateException
	 *             if the mock object is in record state.
	 * @throws AssertionError
	 *             if any expectation has not been met.
	 */
	public void verify() {
		this.ctrl.verify();
	}

	private void callWithConvertedRange(final IExpectationSetters setter,
			final Range range) {
		if (range == MockControl.ONE) {
			setter.once();
		} else if (range == MockControl.ONE_OR_MORE) {
			setter.atLeastOnce();
		} else if (range == MockControl.ZERO_OR_MORE) {
			setter.anyTimes();
		} else {
			throw new IllegalArgumentException("Unexpected Range");
		}
	}

	private IExpectationSetters<Object> expectLastCall(
			final String failureMessage) {
		try {
			return EasyMock.expectLastCall();
		} catch (final IllegalStateException e) {
			throw new IllegalStateException(failureMessage);
		}
	}

}