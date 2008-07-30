/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock;

import java.lang.reflect.Proxy;
import java.util.Comparator;

import org.easymock.internal.LastControl;
import org.easymock.internal.MocksControl;
import org.easymock.internal.ObjectMethodsFilter;
import org.easymock.internal.matchers.Any;
import org.easymock.internal.matchers.ArrayEquals;
import org.easymock.internal.matchers.Compare;
import org.easymock.internal.matchers.CompareEqual;
import org.easymock.internal.matchers.Contains;
import org.easymock.internal.matchers.EndsWith;
import org.easymock.internal.matchers.Equals;
import org.easymock.internal.matchers.EqualsWithDelta;
import org.easymock.internal.matchers.Find;
import org.easymock.internal.matchers.GreaterOrEqual;
import org.easymock.internal.matchers.GreaterThan;
import org.easymock.internal.matchers.InstanceOf;
import org.easymock.internal.matchers.LessOrEqual;
import org.easymock.internal.matchers.LessThan;
import org.easymock.internal.matchers.Matches;
import org.easymock.internal.matchers.NotNull;
import org.easymock.internal.matchers.Null;
import org.easymock.internal.matchers.Same;
import org.easymock.internal.matchers.StartsWith;

public class EasyMock {

	/**
	 * Expects a boolean that matches both given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>false</code>.
	 */
	public static boolean and(final boolean first, final boolean second) {
		LastControl.reportAnd(2);
		return false;
	}

	/**
	 * Expects a byte that matches both given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static byte and(final byte first, final byte second) {
		LastControl.reportAnd(2);
		return 0;
	}

	/**
	 * Expects a char that matches both given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static char and(final char first, final char second) {
		LastControl.reportAnd(2);
		return 0;
	}

	/**
	 * Expects a double that matches both given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static double and(final double first, final double second) {
		LastControl.reportAnd(2);
		return 0;
	}

	/**
	 * Expects a float that matches both given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static float and(final float first, final float second) {
		LastControl.reportAnd(2);
		return 0;
	}

	/**
	 * Expects an int that matches both given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static int and(final int first, final int second) {
		LastControl.reportAnd(2);
		return 0;
	}

	/**
	 * Expects a long that matches both given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static long and(final long first, final long second) {
		LastControl.reportAnd(2);
		return 0;
	}

	/**
	 * Expects a short that matches both given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static short and(final short first, final short second) {
		LastControl.reportAnd(2);
		return 0;
	}

	/**
	 * Expects an Object that matches both given expectations.
	 * 
	 * @param <T>
	 *            the type of the object, it is passed through to prevent casts.
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>null</code>.
	 */
	public static <T> T and(final T first, final T second) {
		LastControl.reportAnd(2);
		return null;
	}

	/**
	 * Expects any boolean argument. For details, see the EasyMock
	 * documentation.
	 * 
	 * @return <code>false</code>.
	 */
	public static boolean anyBoolean() {
		EasyMock.reportMatcher(Any.ANY);
		return false;
	}

	/**
	 * Expects any byte argument. For details, see the EasyMock documentation.
	 * 
	 * @return <code>0</code>.
	 */
	public static byte anyByte() {
		EasyMock.reportMatcher(Any.ANY);
		return 0;
	}

	/**
	 * Expects any char argument. For details, see the EasyMock documentation.
	 * 
	 * @return <code>0</code>.
	 */
	public static char anyChar() {
		EasyMock.reportMatcher(Any.ANY);
		return 0;
	}

	/**
	 * Expects any double argument. For details, see the EasyMock documentation.
	 * 
	 * @return <code>0</code>.
	 */
	public static double anyDouble() {
		EasyMock.reportMatcher(Any.ANY);
		return 0;
	}

	/**
	 * Expects any float argument. For details, see the EasyMock documentation.
	 * 
	 * @return <code>0</code>.
	 */
	public static float anyFloat() {
		EasyMock.reportMatcher(Any.ANY);
		return 0;
	}

	/**
	 * Expects any int argument. For details, see the EasyMock documentation.
	 * 
	 * @return <code>0</code>.
	 */
	public static int anyInt() {
		EasyMock.reportMatcher(Any.ANY);
		return 0;
	}

	/**
	 * Expects any long argument. For details, see the EasyMock documentation.
	 * 
	 * @return <code>0</code>.
	 */
	public static long anyLong() {
		EasyMock.reportMatcher(Any.ANY);
		return 0;
	}

	/**
	 * Expects any Object argument. For details, see the EasyMock documentation.
	 * 
	 * @return <code>null</code>.
	 */
	public static Object anyObject() {
		EasyMock.reportMatcher(Any.ANY);
		return null;
	}

	/**
	 * Expects any short argument. For details, see the EasyMock documentation.
	 * 
	 * @return <code>0</code>.
	 */
	public static short anyShort() {
		EasyMock.reportMatcher(Any.ANY);
		return 0;
	}

	/**
	 * Expects a boolean array that is equal to the given array, i.e. it has to
	 * have the same length, and each element has to be equal.
	 * 
	 * @param value
	 *            the given arry.
	 * @return <code>null</code>.
	 */
	public static boolean[] aryEq(final boolean[] value) {
		EasyMock.reportMatcher(new ArrayEquals(value));
		return null;
	}

	/**
	 * Expects a byte array that is equal to the given array, i.e. it has to
	 * have the same length, and each element has to be equal.
	 * 
	 * @param value
	 *            the given arry.
	 * @return <code>null</code>.
	 */
	public static byte[] aryEq(final byte[] value) {
		EasyMock.reportMatcher(new ArrayEquals(value));
		return null;
	}

	/**
	 * Expects a char array that is equal to the given array, i.e. it has to
	 * have the same length, and each element has to be equal.
	 * 
	 * @param value
	 *            the given arry.
	 * @return <code>null</code>.
	 */
	public static char[] aryEq(final char[] value) {
		EasyMock.reportMatcher(new ArrayEquals(value));
		return null;
	}

	/**
	 * Expects a double array that is equal to the given array, i.e. it has to
	 * have the same length, and each element has to be equal.
	 * 
	 * @param value
	 *            the given arry.
	 * @return <code>null</code>.
	 */
	public static double[] aryEq(final double[] value) {
		EasyMock.reportMatcher(new ArrayEquals(value));
		return null;
	}

	/**
	 * Expects a float array that is equal to the given array, i.e. it has to
	 * have the same length, and each element has to be equal.
	 * 
	 * @param value
	 *            the given arry.
	 * @return <code>null</code>.
	 */
	public static float[] aryEq(final float[] value) {
		EasyMock.reportMatcher(new ArrayEquals(value));
		return null;
	}

	/**
	 * Expects an int array that is equal to the given array, i.e. it has to
	 * have the same length, and each element has to be equal.
	 * 
	 * @param value
	 *            the given arry.
	 * @return <code>null</code>.
	 */
	public static int[] aryEq(final int[] value) {
		EasyMock.reportMatcher(new ArrayEquals(value));
		return null;
	}

	/**
	 * Expects a long array that is equal to the given array, i.e. it has to
	 * have the same length, and each element has to be equal.
	 * 
	 * @param value
	 *            the given arry.
	 * @return <code>null</code>.
	 */
	public static long[] aryEq(final long[] value) {
		EasyMock.reportMatcher(new ArrayEquals(value));
		return null;
	}

	/**
	 * Expects a short array that is equal to the given array, i.e. it has to
	 * have the same length, and each element has to be equal.
	 * 
	 * @param value
	 *            the given arry.
	 * @return <code>null</code>.
	 */
	public static short[] aryEq(final short[] value) {
		EasyMock.reportMatcher(new ArrayEquals(value));
		return null;
	}

	/**
	 * Expects an Object array that is equal to the given array, i.e. it has to
	 * have the same type, length, and each element has to be equal.
	 * 
	 * @param <T>
	 *            the type of the array, it is passed through to prevent casts.
	 * @param value
	 *            the given arry.
	 * @return <code>null</code>.
	 */
	public static <T> T[] aryEq(final T[] value) {
		EasyMock.reportMatcher(new ArrayEquals(value));
		return null;
	}

	/**
	 * Switches order checking of the given mock object (more exactly: the
	 * control of the mock object) the on and off. For details, see the EasyMock
	 * documentation.
	 * 
	 * @param mock
	 *            the mock object.
	 * @param state
	 *            <code>true</code> switches order checking on,
	 *            <code>false</code> switches it off.
	 */
	public static void checkOrder(final Object mock, final boolean state) {
		EasyMock.getControl(mock).checkOrder(state);
	}

	/**
	 * Expects an argument that will be compared using the provided comparator.
	 * The following comparison will take place:
	 * <p>
	 * <code>comparator.compare(actual, expected) operator 0</code>
	 * </p>
	 * For details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @param comparator
	 *            Comparator used to compare the actual with expected value.
	 * @param operator
	 *            The comparison operator.
	 * @return <code>null</code>
	 */
	public static <T> T cmp(final T value, final Comparator<T> comparator,
			final LogicalOperator operator) {
		EasyMock.reportMatcher(new Compare<T>(value, comparator, operator));
		return null;
	}

	/**
	 * Expects a comparable argument equals to the given value according to
	 * their compareTo method. For details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>null</code>.
	 */
	public static <T extends Comparable<T>> T cmpEq(final Comparable<T> value) {
		EasyMock.reportMatcher(new CompareEqual<T>(value));
		return null;
	}

	/**
	 * Expects a string that contains the given substring. For details, see the
	 * EasyMock documentation.
	 * 
	 * @param substring
	 *            the substring.
	 * @return <code>null</code>.
	 */
	public static String contains(final String substring) {
		EasyMock.reportMatcher(new Contains(substring));
		return null;
	}

	/**
	 * Creates a control, order checking is disabled by default.
	 * 
	 * @return the control.
	 */
	public static IMocksControl createControl() {
		return new MocksControl(MocksControl.MockType.DEFAULT);
	}

	/**
	 * Creates a mock object that implements the given interface, order checking
	 * is disabled by default.
	 * 
	 * @param <T>
	 *            the interface that the mock object should implement.
	 * @param toMock
	 *            the class of the interface that the mock object should
	 *            implement.
	 * @return the mock object.
	 */
	public static <T> T createMock(final Class<T> toMock) {
		return EasyMock.createControl().createMock(toMock);
	}

	/**
	 * Creates a mock object that implements the given interface, order checking
	 * is disabled by default.
	 * 
	 * @param name
	 *            the name of the mock object.
	 * @param toMock
	 *            the class of the interface that the mock object should
	 *            implement.
	 * 
	 * @param <T>
	 *            the interface that the mock object should implement.
	 * @return the mock object.
	 * @throws IllegalArgumentException
	 *             if the name is not a valid Java identifier.
	 */
	public static <T> T createMock(final String name, final Class<T> toMock) {
		return EasyMock.createControl().createMock(name, toMock);
	}

	/**
	 * Creates a control, order checking is disabled by default, and the mock
	 * objects created by this control will return <code>0</code>,
	 * <code>null</code> or <code>false</code> for unexpected invocations.
	 * 
	 * @return the control.
	 */
	public static IMocksControl createNiceControl() {
		return new MocksControl(MocksControl.MockType.NICE);
	}

	/**
	 * Creates a mock object that implements the given interface, order checking
	 * is disabled by default, and the mock object will return <code>0</code>,
	 * <code>null</code> or <code>false</code> for unexpected invocations.
	 * 
	 * @param <T>
	 *            the interface that the mock object should implement.
	 * @param toMock
	 *            the class of the interface that the mock object should
	 *            implement.
	 * @return the mock object.
	 */
	public static <T> T createNiceMock(final Class<T> toMock) {
		return EasyMock.createNiceControl().createMock(toMock);
	}

	/**
	 * Creates a mock object that implements the given interface, order checking
	 * is disabled by default, and the mock object will return <code>0</code>,
	 * <code>null</code> or <code>false</code> for unexpected invocations.
	 * 
	 * @param name
	 *            the name of the mock object.
	 * @param toMock
	 *            the class of the interface that the mock object should
	 *            implement.
	 * 
	 * @param <T>
	 *            the interface that the mock object should implement.
	 * @return the mock object.
	 * @throws IllegalArgumentException
	 *             if the name is not a valid Java identifier.
	 */
	public static <T> T createNiceMock(final String name, final Class<T> toMock) {
		return EasyMock.createNiceControl().createMock(name, toMock);
	}

	/**
	 * Creates a control, order checking is enabled by default.
	 * 
	 * @return the control.
	 */
	public static IMocksControl createStrictControl() {
		return new MocksControl(MocksControl.MockType.STRICT);
	}

	/**
	 * Creates a mock object that implements the given interface, order checking
	 * is enabled by default.
	 * 
	 * @param <T>
	 *            the interface that the mock object should implement.
	 * @param toMock
	 *            the class of the interface that the mock object should
	 *            implement.
	 * @return the mock object.
	 */
	public static <T> T createStrictMock(final Class<T> toMock) {
		return EasyMock.createStrictControl().createMock(toMock);
	}

	/**
	 * Creates a mock object that implements the given interface, order checking
	 * is enabled by default.
	 * 
	 * @param name
	 *            the name of the mock object.
	 * @param toMock
	 *            the class of the interface that the mock object should
	 *            implement.
	 * @param <T>
	 *            the interface that the mock object should implement.
	 * @return the mock object.
	 * @throws IllegalArgumentException
	 *             if the name is not a valid Java identifier.
	 */
	public static <T> T createStrictMock(final String name,
			final Class<T> toMock) {
		return EasyMock.createStrictControl().createMock(name, toMock);
	}

	/**
	 * Expects a string that ends with the given suffix. For details, see the
	 * EasyMock documentation.
	 * 
	 * @param suffix
	 *            the suffix.
	 * @return <code>null</code>.
	 */
	public static String endsWith(final String suffix) {
		EasyMock.reportMatcher(new EndsWith(suffix));
		return null;
	}

	/**
	 * Expects a boolean that is equal to the given value.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static boolean eq(final boolean value) {
		EasyMock.reportMatcher(new Equals(value));
		return false;
	}

	/**
	 * Expects a byte that is equal to the given value.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static byte eq(final byte value) {
		EasyMock.reportMatcher(new Equals(value));
		return 0;
	}

	/**
	 * Expects a char that is equal to the given value.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static char eq(final char value) {
		EasyMock.reportMatcher(new Equals(value));
		return 0;
	}

	/**
	 * Expects a double that is equal to the given value.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static double eq(final double value) {
		EasyMock.reportMatcher(new Equals(value));
		return 0;
	}

	/**
	 * Expects a double that has an absolute difference to the given value that
	 * is less than the given delta. For details, see the EasyMock
	 * documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @param delta
	 *            the given delta.
	 * @return <code>0</code>.
	 */
	public static double eq(final double value, final double delta) {
		EasyMock.reportMatcher(new EqualsWithDelta(value, delta));
		return 0;
	}

	/**
	 * Expects a float that is equal to the given value.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static float eq(final float value) {
		EasyMock.reportMatcher(new Equals(value));
		return 0;
	}

	/**
	 * Expects a float that has an absolute difference to the given value that
	 * is less than the given delta. For details, see the EasyMock
	 * documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @param delta
	 *            the given delta.
	 * @return <code>0</code>.
	 */
	public static float eq(final float value, final float delta) {
		EasyMock.reportMatcher(new EqualsWithDelta(value, delta));
		return 0;
	}

	/**
	 * Expects an int that is equal to the given value.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static int eq(final int value) {
		EasyMock.reportMatcher(new Equals(value));
		return 0;
	}

	/**
	 * Expects a long that is equal to the given value.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static long eq(final long value) {
		EasyMock.reportMatcher(new Equals(value));
		return 0;
	}

	/**
	 * Expects a short that is equal to the given value.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static short eq(final short value) {
		EasyMock.reportMatcher(new Equals(value));
		return 0;
	}

	/**
	 * Expects an Object that is equal to the given value.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>null</code>.
	 */
	public static <T> T eq(final T value) {
		EasyMock.reportMatcher(new Equals(value));
		return null;
	}

	/**
	 * Returns the expectation setter for the last expected invocation in the
	 * current thread.
	 * 
	 * @param value
	 *            the parameter is used to transport the type to the
	 *            ExpectationSetter. It allows writing the expected call as
	 *            argument, i.e.
	 *            <code>expect(mock.getName()).andReturn("John Doe")<code>.
	 * 
	 * @return the expectation setter.
	 */
	@SuppressWarnings("unchecked")
	public static <T> IExpectationSetters<T> expect(final T value) {
		return EasyMock.getControlForLastCall();
	}

	/**
	 * Returns the expectation setter for the last expected invocation in the
	 * current thread. This method is used for expected invocations on void
	 * methods.
	 * 
	 * @return the expectation setter.
	 */
	@SuppressWarnings("unchecked")
	public static IExpectationSetters<Object> expectLastCall() {
		return EasyMock.getControlForLastCall();
	}

	/**
	 * Expects a string that contains a substring that matches the given regular
	 * expression. For details, see the EasyMock documentation.
	 * 
	 * @param regex
	 *            the regular expression.
	 * @return <code>null</code>.
	 */
	public static String find(final String regex) {
		EasyMock.reportMatcher(new Find(regex));
		return null;
	}

	/**
	 * Expects a byte argument greater than or equal to the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static byte geq(final byte value) {
		EasyMock.reportMatcher(new GreaterOrEqual<Byte>(value));
		return 0;
	}

	/**
	 * Expects a comparable argument greater than or equal the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>null</code>.
	 */
	public static <T extends Comparable<T>> T geq(final Comparable<T> value) {
		EasyMock.reportMatcher(new GreaterOrEqual<T>(value));
		return null;
	}

	/**
	 * Expects a double argument greater than or equal to the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static double geq(final double value) {
		EasyMock.reportMatcher(new GreaterOrEqual<Double>(value));
		return 0;
	}

	/**
	 * Expects a float argument greater than or equal to the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static float geq(final float value) {
		EasyMock.reportMatcher(new GreaterOrEqual<Float>(value));
		return 0;
	}

	/**
	 * Expects an int argument greater than or equal to the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static int geq(final int value) {
		EasyMock.reportMatcher(new GreaterOrEqual<Integer>(value));
		return 0;
	}

	/**
	 * Expects a long argument greater than or equal to the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static long geq(final long value) {
		EasyMock.reportMatcher(new GreaterOrEqual<Long>(value));
		return 0;
	}

	/**
	 * Expects a short argument greater than or equal to the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static short geq(final short value) {
		EasyMock.reportMatcher(new GreaterOrEqual<Short>(value));
		return 0;
	}

	/**
	 * Returns the arguments of the current mock method call, if inside an
	 * <code>IAnswer</code> callback - be careful here, reordering parameters
	 * of method changes the semantics of your tests.
	 * 
	 * @return the arguments of the current mock method call.
	 * @throws IllegalStateException
	 *             if called outside of <code>IAnswer</code> callbacks.
	 */
	public static Object[] getCurrentArguments() {
		final Object[] result = LastControl.getCurrentArguments();
		if (result == null) {
			throw new IllegalStateException(
					"current arguments are only available when executing callback methods");
		}
		return result;
	}

	/**
	 * Expects a byte argument greater than the given value. For details, see
	 * the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static byte gt(final byte value) {
		EasyMock.reportMatcher(new GreaterThan<Byte>(value));
		return 0;
	}

	/**
	 * Expects a comparable argument greater than the given value. For details,
	 * see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>null</code>.
	 */
	public static <T extends Comparable<T>> T gt(final Comparable<T> value) {
		EasyMock.reportMatcher(new GreaterThan<T>(value));
		return null;
	}

	/**
	 * Expects a double argument greater than the given value. For details, see
	 * the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static double gt(final double value) {
		EasyMock.reportMatcher(new GreaterThan<Double>(value));
		return 0;
	}

	/**
	 * Expects a float argument greater than the given value. For details, see
	 * the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static float gt(final float value) {
		EasyMock.reportMatcher(new GreaterThan<Float>(value));
		return 0;
	}

	/**
	 * Expects an int argument greater than the given value. For details, see
	 * the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static int gt(final int value) {
		EasyMock.reportMatcher(new GreaterThan<Integer>(value));
		return 0;
	}

	/**
	 * Expects a long argument greater than the given value. For details, see
	 * the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static long gt(final long value) {
		EasyMock.reportMatcher(new GreaterThan<Long>(value));
		return 0;
	}

	/**
	 * Expects a short argument greater than the given value. For details, see
	 * the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static short gt(final short value) {
		EasyMock.reportMatcher(new GreaterThan<Short>(value));
		return 0;
	}

	/**
	 * Expects an object implementing the given class. For details, see the
	 * EasyMock documentation.
	 * 
	 * @param <T>
	 *            the accepted type.
	 * @param clazz
	 *            the class of the accepted type.
	 * @return <code>null</code>.
	 */
	public static <T> T isA(final Class<T> clazz) {
		EasyMock.reportMatcher(new InstanceOf(clazz));
		return null;
	}

	/**
	 * Expects null.
	 * 
	 * @return <code>null</code>.
	 */
	public static Object isNull() {
		EasyMock.reportMatcher(Null.NULL);
		return null;
	}

	/**
	 * Expects a byte argument less than or equal to the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static byte leq(final byte value) {
		EasyMock.reportMatcher(new LessOrEqual<Byte>(value));
		return 0;
	}

	/**
	 * Expects a comparable argument less than or equal the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>null</code>.
	 */
	public static <T extends Comparable<T>> T leq(final Comparable<T> value) {
		EasyMock.reportMatcher(new LessOrEqual<T>(value));
		return null;
	}

	/**
	 * Expects a double argument less than or equal to the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static double leq(final double value) {
		EasyMock.reportMatcher(new LessOrEqual<Double>(value));
		return 0;
	}

	/**
	 * Expects a float argument less than or equal to the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static float leq(final float value) {
		EasyMock.reportMatcher(new LessOrEqual<Float>(value));
		return 0;
	}

	/**
	 * Expects an int argument less than or equal to the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static int leq(final int value) {
		EasyMock.reportMatcher(new LessOrEqual<Integer>(value));
		return 0;
	}

	/**
	 * Expects a long argument less than or equal to the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static long leq(final long value) {
		EasyMock.reportMatcher(new LessOrEqual<Long>(value));
		return 0;
	}

	/**
	 * Expects a short argument less than or equal to the given value. For
	 * details, see the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static short leq(final short value) {
		EasyMock.reportMatcher(new LessOrEqual<Short>(value));
		return 0;
	}

	/**
	 * Expects a byte argument less than the given value. For details, see the
	 * EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static byte lt(final byte value) {
		EasyMock.reportMatcher(new LessThan<Byte>(value));
		return 0;
	}

	/**
	 * Expects a comparable argument less than the given value. For details, see
	 * the EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>null</code>.
	 */
	public static <T extends Comparable<T>> T lt(final Comparable<T> value) {
		EasyMock.reportMatcher(new LessThan<T>(value));
		return null;
	}

	/**
	 * Expects a double argument less than the given value. For details, see the
	 * EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static double lt(final double value) {
		EasyMock.reportMatcher(new LessThan<Double>(value));
		return 0;
	}

	/**
	 * Expects a float argument less than the given value. For details, see the
	 * EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static float lt(final float value) {
		EasyMock.reportMatcher(new LessThan<Float>(value));
		return 0;
	}

	/**
	 * Expects an int argument less than the given value. For details, see the
	 * EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static int lt(final int value) {
		EasyMock.reportMatcher(new LessThan<Integer>(value));
		return 0;
	}

	/**
	 * Expects a long argument less than the given value. For details, see the
	 * EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static long lt(final long value) {
		EasyMock.reportMatcher(new LessThan<Long>(value));
		return 0;
	}

	/**
	 * Expects a short argument less than the given value. For details, see the
	 * EasyMock documentation.
	 * 
	 * @param value
	 *            the given value.
	 * @return <code>0</code>.
	 */
	public static short lt(final short value) {
		EasyMock.reportMatcher(new LessThan<Short>(value));
		return 0;
	}

	/**
	 * Expects a string that matches the given regular expression. For details,
	 * see the EasyMock documentation.
	 * 
	 * @param regex
	 *            the regular expression.
	 * @return <code>null</code>.
	 */
	public static String matches(final String regex) {
		EasyMock.reportMatcher(new Matches(regex));
		return null;
	}

	/**
	 * Expects a boolean that does not match the given expectation.
	 * 
	 * @param first
	 *            placeholder for the expectation.
	 * @return <code>false</code>.
	 */
	public static boolean not(final boolean first) {
		LastControl.reportNot();
		return false;
	}

	/**
	 * Expects a byte that does not match the given expectation.
	 * 
	 * @param first
	 *            placeholder for the expectation.
	 * @return <code>0</code>.
	 */
	public static byte not(final byte first) {
		LastControl.reportNot();
		return 0;
	}

	/**
	 * Expects a char that does not match the given expectation.
	 * 
	 * @param first
	 *            placeholder for the expectation.
	 * @return <code>0</code>.
	 */
	public static char not(final char first) {
		LastControl.reportNot();
		return 0;
	}

	/**
	 * Expects a double that does not match the given expectation.
	 * 
	 * @param first
	 *            placeholder for the expectation.
	 * @return <code>0</code>.
	 */
	public static double not(final double first) {
		LastControl.reportNot();
		return 0;
	}

	/**
	 * Expects a float that does not match the given expectation.
	 * 
	 * @param first
	 *            placeholder for the expectation.
	 * @return <code>0</code>.
	 */
	public static float not(final float first) {
		LastControl.reportNot();
		return first;
	}

	/**
	 * Expects an int that does not match the given expectation.
	 * 
	 * @param first
	 *            placeholder for the expectation.
	 * @return <code>0</code>.
	 */
	public static int not(final int first) {
		LastControl.reportNot();
		return 0;
	}

	/**
	 * Expects a long that does not match the given expectation.
	 * 
	 * @param first
	 *            placeholder for the expectation.
	 * @return <code>0</code>.
	 */
	public static long not(final long first) {
		LastControl.reportNot();
		return 0;
	}

	/**
	 * Expects a short that does not match the given expectation.
	 * 
	 * @param first
	 *            placeholder for the expectation.
	 * @return <code>0</code>.
	 */
	public static short not(final short first) {
		LastControl.reportNot();
		return 0;
	}

	/**
	 * Expects an Object that does not match the given expectation.
	 * 
	 * @param <T>
	 *            the type of the object, it is passed through to prevent casts.
	 * @param first
	 *            placeholder for the expectation.
	 * @return <code>null</code>.
	 */
	public static <T> T not(final T first) {
		LastControl.reportNot();
		return null;
	}

	/**
	 * Expects not null.
	 * 
	 * @return <code>null</code>.
	 */
	public static Object notNull() {
		EasyMock.reportMatcher(NotNull.NOT_NULL);
		return null;
	}

	/**
	 * Expects a boolean that matches one of the given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>false</code>.
	 */
	public static boolean or(final boolean first, final boolean second) {
		LastControl.reportOr(2);
		return false;
	}

	/**
	 * Expects a byte that matches one of the given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static byte or(final byte first, final byte second) {
		LastControl.reportOr(2);
		return 0;
	}

	/**
	 * Expects a char that matches one of the given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static char or(final char first, final char second) {
		LastControl.reportOr(2);
		return 0;
	}

	/**
	 * Expects a double that matches one of the given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static double or(final double first, final double second) {
		LastControl.reportOr(2);
		return 0;
	}

	/**
	 * Expects a float that matches one of the given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static float or(final float first, final float second) {
		LastControl.reportOr(2);
		return 0;
	}

	/**
	 * Expects an int that matches one of the given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static int or(final int first, final int second) {
		LastControl.reportOr(2);
		return first;
	}

	/**
	 * Expects a long that matches one of the given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static long or(final long first, final long second) {
		LastControl.reportOr(2);
		return 0;
	}

	/**
	 * Expects a short that matches one of the given expectations.
	 * 
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>0</code>.
	 */
	public static short or(final short first, final short second) {
		LastControl.reportOr(2);
		return 0;
	}

	/**
	 * Expects an Object that matches one of the given expectations.
	 * 
	 * @param <T>
	 *            the type of the object, it is passed through to prevent casts.
	 * @param first
	 *            placeholder for the first expectation.
	 * @param second
	 *            placeholder for the second expectation.
	 * @return <code>null</code>.
	 */
	public static <T> T or(final T first, final T second) {
		LastControl.reportOr(2);
		return null;
	}

	/**
	 * Switches the given mock objects (more exactly: the controls of the mock
	 * objects) to replay mode. For details, see the EasyMock documentation.
	 * 
	 * @param mocks
	 *            the mock objects.
	 */
	public static void replay(final Object... mocks) {
		for (final Object mock : mocks) {
			EasyMock.getControl(mock).replay();
		}
	}

	/**
	 * Reports an argument matcher. This method is needed to define own argument
	 * matchers. For details, see the EasyMock documentation.
	 * 
	 * @param matcher
	 */
	public static void reportMatcher(final IArgumentMatcher matcher) {
		LastControl.reportMatcher(matcher);
	}

	/**
	 * Resets the given mock objects (more exactly: the controls of the mock
	 * objects). For details, see the EasyMock documentation.
	 * 
	 * @param mocks
	 *            the mock objects.
	 */
	public static void reset(final Object... mocks) {
		for (final Object mock : mocks) {
			EasyMock.getControl(mock).reset();
		}
	}

	/**
	 * Expects an Object that is the same as the given value. For details, see
	 * the EasyMock documentation.
	 * 
	 * @param <T>
	 *            the type of the object, it is passed through to prevent casts.
	 * @param value
	 *            the given value.
	 * @return <code>null</code>.
	 */
	public static <T> T same(final T value) {
		EasyMock.reportMatcher(new Same(value));
		return null;
	}

	/**
	 * Expects a string that starts with the given prefix. For details, see the
	 * EasyMock documentation.
	 * 
	 * @param prefix
	 *            the prefix.
	 * @return <code>null</code>.
	 */
	public static String startsWith(final String prefix) {
		EasyMock.reportMatcher(new StartsWith(prefix));
		return null;
	}

	/**
	 * Verifies the given mock objects (more exactly: the controls of the mock
	 * objects).
	 * 
	 * @param mocks
	 *            the mock objects.
	 */
	public static void verify(final Object... mocks) {
		for (final Object mock : mocks) {
			EasyMock.getControl(mock).verify();
		}
	}

	private static MocksControl getControl(final Object mock) {
		return ((ObjectMethodsFilter) Proxy.getInvocationHandler(mock))
				.getDelegate().getControl();
	}

	private static IExpectationSetters getControlForLastCall() {
		final MocksControl lastControl = LastControl.lastControl();
		if (lastControl == null) {
			throw new IllegalStateException("no last call on a mock available");
		}
		return lastControl;
	}
}
