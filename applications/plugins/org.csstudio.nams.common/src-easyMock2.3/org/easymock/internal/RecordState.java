/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.ArgumentsMatcher;
import org.easymock.IAnswer;
import org.easymock.IArgumentMatcher;
import org.easymock.MockControl;

public class RecordState implements IMocksControlState {

	private static Map<Class, Object> emptyReturnValues = new HashMap<Class, Object>();

	private static Map<Class, Class> primitiveToWrapperType = new HashMap<Class, Class>();

	static {
		RecordState.emptyReturnValues.put(Void.TYPE, null);
		RecordState.emptyReturnValues.put(Boolean.TYPE, Boolean.FALSE);
		RecordState.emptyReturnValues.put(Byte.TYPE, new Byte((byte) 0));
		RecordState.emptyReturnValues.put(Short.TYPE, new Short((short) 0));
		RecordState.emptyReturnValues.put(Character.TYPE, new Character(
				(char) 0));
		RecordState.emptyReturnValues.put(Integer.TYPE, new Integer(0));
		RecordState.emptyReturnValues.put(Long.TYPE, new Long(0));
		RecordState.emptyReturnValues.put(Float.TYPE, new Float(0));
		RecordState.emptyReturnValues.put(Double.TYPE, new Double(0));
	}

	static {
		RecordState.primitiveToWrapperType.put(Boolean.TYPE, Boolean.class);
		RecordState.primitiveToWrapperType.put(Byte.TYPE, Byte.class);
		RecordState.primitiveToWrapperType.put(Short.TYPE, Short.class);
		RecordState.primitiveToWrapperType.put(Character.TYPE, Character.class);
		RecordState.primitiveToWrapperType.put(Integer.TYPE, Integer.class);
		RecordState.primitiveToWrapperType.put(Long.TYPE, Long.class);
		RecordState.primitiveToWrapperType.put(Float.TYPE, Float.class);
		RecordState.primitiveToWrapperType.put(Double.TYPE, Double.class);
	}

	public static Object emptyReturnValueFor(final Class type) {
		return type.isPrimitive() ? RecordState.emptyReturnValues.get(type)
				: null;
	}

	private ExpectedInvocation lastInvocation;

	private boolean lastInvocationUsed = true;

	private Result lastResult;

	private final IMocksBehavior behavior;

	public RecordState(final IMocksBehavior behavior) {
		this.behavior = behavior;
	}

	public void andAnswer(final IAnswer answer) {
		this.requireMethodCall("answer");
		this.requireValidAnswer(answer);
		if (this.lastResult != null) {
			this.times(MocksControl.ONCE);
		}
		this.lastResult = Result.createAnswerResult(answer);
	}

	public void andReturn(final Object value) {
		Object localValue = value;
		this.requireMethodCall("return value");
		localValue = this.convertNumberClassIfNeccessary(localValue);
		this.requireAssignable(localValue);
		if (this.lastResult != null) {
			this.times(MocksControl.ONCE);
		}
		this.lastResult = Result.createReturnResult(localValue);
	}

	public void andStubAnswer(final IAnswer answer) {
		this.requireMethodCall("stub answer");
		this.requireValidAnswer(answer);
		if (this.lastResult != null) {
			this.times(MocksControl.ONCE);
		}
		this.behavior.addStub(this.lastInvocation, Result
				.createAnswerResult(answer));
		this.lastInvocationUsed = true;
	}

	public void andStubReturn(final Object value) {
		Object localValue = value;
		this.requireMethodCall("stub return value");
		localValue = this.convertNumberClassIfNeccessary(localValue);
		this.requireAssignable(localValue);
		if (this.lastResult != null) {
			this.times(MocksControl.ONCE);
		}
		this.behavior.addStub(this.lastInvocation, Result
				.createReturnResult(localValue));
		this.lastInvocationUsed = true;
	}

	public void andStubThrow(final Throwable throwable) {
		this.requireMethodCall("stub Throwable");
		this.requireValidThrowable(throwable);
		if (this.lastResult != null) {
			this.times(MocksControl.ONCE);
		}
		this.behavior.addStub(this.lastInvocation, Result
				.createThrowResult(throwable));
		this.lastInvocationUsed = true;
	}

	public void andThrow(final Throwable throwable) {
		this.requireMethodCall("Throwable");
		this.requireValidThrowable(throwable);
		if (this.lastResult != null) {
			this.times(MocksControl.ONCE);
		}
		this.lastResult = Result.createThrowResult(throwable);
	}

	public void assertRecordState() {
	}

	public void asStub() {
		this.requireMethodCall("stub behavior");
		this.requireVoidMethod();
		this.behavior.addStub(this.lastInvocation, Result
				.createReturnResult(null));
		this.lastInvocationUsed = true;
	}

	public void checkOrder(final boolean value) {
		this.closeMethod();
		this.behavior.checkOrder(value);
	}

	public java.lang.Object invoke(final Invocation invocation) {
		this.closeMethod();
		final List<IArgumentMatcher> lastMatchers = LastControl.pullMatchers();
		this.lastInvocation = new ExpectedInvocation(invocation, lastMatchers);
		this.lastInvocationUsed = false;
		return RecordState.emptyReturnValueFor(invocation.getMethod()
				.getReturnType());
	}

	public void replay() {
		this.closeMethod();
		if (LastControl.pullMatchers() != null) {
			throw new IllegalStateException(
					"matcher calls were used outside expectations");
		}
	}

	public void setDefaultMatcher(final ArgumentsMatcher matcher) {
		this.behavior.setDefaultMatcher(matcher);
	}

	public void setDefaultReturnValue(final Object value) {
		Object localValue = value;
		this.requireMethodCall("default return value");
		localValue = this.convertNumberClassIfNeccessary(localValue);
		this.requireAssignable(localValue);
		if (this.lastResult != null) {
			this.times(MocksControl.ONCE);
		}
		this.behavior.addStub(this.lastInvocation
				.withMatcher(MockControl.ALWAYS_MATCHER), Result
				.createReturnResult(localValue));
		this.lastInvocationUsed = true;
	}

	public void setDefaultThrowable(final Throwable throwable) {
		this.requireMethodCall("default Throwable");
		this.requireValidThrowable(throwable);
		if (this.lastResult != null) {
			this.times(MocksControl.ONCE);
		}
		this.behavior.addStub(this.lastInvocation
				.withMatcher(MockControl.ALWAYS_MATCHER), Result
				.createThrowResult(throwable));
		this.lastInvocationUsed = true;
	}

	public void setDefaultVoidCallable() {
		this.requireMethodCall("default void callable");
		this.requireVoidMethod();
		this.behavior.addStub(this.lastInvocation
				.withMatcher(MockControl.ALWAYS_MATCHER), Result
				.createReturnResult(null));
		this.lastInvocationUsed = true;
	}

	public void setMatcher(final Method method, final ArgumentsMatcher matcher) {
		this.requireMethodCall("matcher");
		this.behavior.setMatcher(this.lastInvocation.getMethod(), matcher);
	}

	public void times(final Range range) {
		this.requireMethodCall("times");
		this.requireLastResultOrVoidMethod();

		this.behavior.addExpected(this.lastInvocation,
				this.lastResult != null ? this.lastResult : Result
						.createReturnResult(null), range);
		this.lastInvocationUsed = true;
		this.lastResult = null;
	}

	public void verify() {
		throw new RuntimeExceptionWrapper(new IllegalStateException(
				"calling verify is not allowed in record state"));
	}

	private void closeMethod() {
		if (this.lastInvocationUsed && (this.lastResult == null)) {
			return;
		}
		if (!this.isLastResultOrVoidMethod()) {
			throw new RuntimeExceptionWrapper(new IllegalStateException(
					"missing behavior definition for the preceeding method call "
							+ this.lastInvocation.toString()));
		}
		this.times(MockControl.ONE);
	}

	private Object convertNumberClassIfNeccessary(final Object o) {
		final Class returnType = this.lastInvocation.getMethod()
				.getReturnType();
		return this.createNumberObject(o, returnType);
	}

	private Object createNumberObject(final Object value, final Class returnType) {
		if (!(value instanceof Number)) {
			return value;
		}
		final Number number = (Number) value;
		if (returnType.equals(Byte.TYPE)) {
			return number.byteValue();
		} else if (returnType.equals(Short.TYPE)) {
			return number.shortValue();
		} else if (returnType.equals(Character.TYPE)) {
			return (char) number.intValue();
		} else if (returnType.equals(Integer.TYPE)) {
			return number.intValue();
		} else if (returnType.equals(Long.TYPE)) {
			return number.longValue();
		} else if (returnType.equals(Float.TYPE)) {
			return number.floatValue();
		} else if (returnType.equals(Double.TYPE)) {
			return number.doubleValue();
		} else {
			return number;
		}
	}

	private boolean isLastResultOrVoidMethod() {
		return (this.lastResult != null) || this.lastMethodIsVoidMethod();
	}

	private boolean isValidThrowable(final Throwable throwable) {
		if (throwable instanceof RuntimeException) {
			return true;
		}
		if (throwable instanceof Error) {
			return true;
		}
		final Class<?>[] exceptions = this.lastInvocation.getMethod()
				.getExceptionTypes();
		final Class<?> throwableClass = throwable.getClass();
		for (final Class<?> exception : exceptions) {
			if (exception.isAssignableFrom(throwableClass)) {
				return true;
			}
		}
		return false;
	}

	private boolean lastMethodIsVoidMethod() {
		final Class returnType = this.lastInvocation.getMethod()
				.getReturnType();
		return returnType.equals(Void.TYPE);
	}

	private void requireAssignable(final Object returnValue) {
		if (this.lastMethodIsVoidMethod()) {
			throw new RuntimeExceptionWrapper(new IllegalStateException(
					"void method cannot return a value"));
		}
		if (returnValue == null) {
			return;
		}
		Class<?> returnedType = this.lastInvocation.getMethod().getReturnType();
		if (returnedType.isPrimitive()) {
			returnedType = RecordState.primitiveToWrapperType.get(returnedType);

		}
		if (!returnedType.isAssignableFrom(returnValue.getClass())) {
			throw new RuntimeExceptionWrapper(new IllegalStateException(
					"incompatible return value type"));
		}
	}

	private void requireLastResultOrVoidMethod() {
		if (this.isLastResultOrVoidMethod()) {
			return;
		}
		throw new RuntimeExceptionWrapper(new IllegalStateException(
				"last method called on mock is not a void method"));
	}

	private void requireMethodCall(final String failMessage) {
		if (this.lastInvocation == null) {
			throw new RuntimeExceptionWrapper(new IllegalStateException(
					"method call on the mock needed before setting "
							+ failMessage));
		}
	}

	private void requireValidAnswer(final IAnswer answer) {
		if (answer == null) {
			throw new RuntimeExceptionWrapper(new NullPointerException(
					"answer object must not be null"));
		}
	}

	private void requireValidThrowable(final Throwable throwable) {
		if (throwable == null) {
			throw new RuntimeExceptionWrapper(new NullPointerException(
					"null cannot be thrown"));
		}
		if (this.isValidThrowable(throwable)) {
			return;
		}

		throw new RuntimeExceptionWrapper(new IllegalArgumentException(
				"last method called on mock cannot throw "
						+ throwable.getClass().getName()));
	}

	private void requireVoidMethod() {
		if (this.lastMethodIsVoidMethod()) {
			return;
		}
		throw new RuntimeExceptionWrapper(new IllegalStateException(
				"last method called on mock is not a void method"));
	}
}