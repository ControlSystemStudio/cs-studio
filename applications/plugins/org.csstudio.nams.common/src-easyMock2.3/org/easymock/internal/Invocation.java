/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import java.lang.reflect.Method;

import org.easymock.ArgumentsMatcher;
import org.easymock.internal.matchers.ArrayEquals;

public class Invocation {

	public static boolean isJavaIdentifier(final String mockName) {
		if ((mockName.length() == 0) || (mockName.indexOf(' ') > -1)
				|| !Character.isJavaIdentifierStart(mockName.charAt(0))) {
			return false;
		}
		for (final char c : mockName.substring(1).toCharArray()) {
			if (!Character.isJavaIdentifierPart(c)) {
				return false;
			}
		}
		return true;
	}

	private static Object[] expandVarArgs(final boolean isVarArgs,
			final Object[] args) {
		if (!isVarArgs
				|| (isVarArgs && (args[args.length - 1] != null) && !args[args.length - 1]
						.getClass().isArray())) {
			return args == null ? new Object[0] : args;
		}
		final Object[] varArgs = ArrayEquals
				.createObjectArray(args[args.length - 1]);
		final int nonVarArgsCount = args.length - 1;
		final int varArgsCount = varArgs.length;
		final Object[] newArgs = new Object[nonVarArgsCount + varArgsCount];
		System.arraycopy(args, 0, newArgs, 0, nonVarArgsCount);
		System.arraycopy(varArgs, 0, newArgs, nonVarArgsCount, varArgsCount);
		return newArgs;
	}

	private final Object mock;

	private final Method method;

	private final Object[] arguments;

	public Invocation(final Object mock, final Method method,
			final Object[] args) {
		this.mock = mock;
		this.method = method;
		this.arguments = Invocation.expandVarArgs(method.isVarArgs(), args);
	}

	@Override
	public boolean equals(final Object o) {
		if ((o == null) || !o.getClass().equals(this.getClass())) {
			return false;
		}

		final Invocation other = (Invocation) o;

		return this.mock.equals(other.mock) && this.method.equals(other.method)
				&& this.equalArguments(other.arguments);
	}

	public Object[] getArguments() {
		return this.arguments;
	}

	public Method getMethod() {
		return this.method;
	}

	public Object getMock() {
		return this.mock;
	}

	public String getMockAndMethodName() {
		final String mockName = this.mock.toString();
		final String methodName = this.method.getName();
		if (this.toStringIsDefined(this.mock)
				&& Invocation.isJavaIdentifier(mockName)) {
			return mockName + "." + methodName;
		} else {
			return methodName;
		}
	}

	@Override
	public int hashCode() {
		throw new UnsupportedOperationException("hashCode() is not implemented");
	}

	public boolean matches(final Invocation actual,
			final ArgumentsMatcher matcher) {
		return this.mock.equals(actual.mock)
				&& this.method.equals(actual.method)
				&& matcher.matches(this.arguments, actual.arguments);
	}

	public String toString(final ArgumentsMatcher matcher) {
		return this.getMockAndMethodName() + "("
				+ matcher.toString(this.arguments) + ")";
	}

	private boolean equalArguments(final Object[] arguments) {
		if (this.arguments.length != arguments.length) {
			return false;
		}
		for (int i = 0; i < this.arguments.length; i++) {
			final Object myArgument = this.arguments[i];
			final Object otherArgument = arguments[i];

			if (this.isPrimitiveParameter(i)) {
				if (!myArgument.equals(otherArgument)) {
					return false;
				}
			} else {
				if (myArgument != otherArgument) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isPrimitiveParameter(final int parameterPosition) {
		int localParameterPosition = parameterPosition;
		final Class<?>[] parameterTypes = this.method.getParameterTypes();
		if (this.method.isVarArgs()) {
			localParameterPosition = Math.min(localParameterPosition,
					parameterTypes.length - 1);
		}
		return parameterTypes[localParameterPosition].isPrimitive();
	}

	private boolean toStringIsDefined(final Object o) {
		try {
			o.getClass().getDeclaredMethod("toString", (Class[]) null)
					.getModifiers();
			return true;
		} catch (final SecurityException ignored) {
			// ///CLOVER:OFF
			return false;
			// ///CLOVER:ON
		} catch (final NoSuchMethodException shouldNeverHappen) {
			// ///CLOVER:OFF
			throw new RuntimeException(
					"The toString() method could not be found!");
			// ///CLOVER:ON
		}
	}
}
