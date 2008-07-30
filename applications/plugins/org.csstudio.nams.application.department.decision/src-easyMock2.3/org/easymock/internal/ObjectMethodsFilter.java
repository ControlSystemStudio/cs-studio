/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ObjectMethodsFilter implements InvocationHandler {
	private final Method equalsMethod;

	private final Method hashCodeMethod;

	private final Method toStringMethod;

	private final MockInvocationHandler delegate;

	private final String name;

	public ObjectMethodsFilter(final Class toMock,
			final MockInvocationHandler delegate, final String name) {
		Class localToMock = toMock;
		if ((name != null) && !Invocation.isJavaIdentifier(name)) {
			throw new IllegalArgumentException(String.format(
					"'%s' is not a valid Java identifier.", name));

		}
		try {
			if (localToMock.isInterface()) {
				localToMock = Object.class;
			}
			this.equalsMethod = localToMock.getMethod("equals",
					new Class[] { Object.class });
			this.hashCodeMethod = localToMock.getMethod("hashCode",
					(Class[]) null);
			this.toStringMethod = localToMock.getMethod("toString",
					(Class[]) null);
		} catch (final NoSuchMethodException e) {
			// ///CLOVER:OFF
			throw new RuntimeException("An Object method could not be found!");
			// ///CLOVER:ON
		}
		this.delegate = delegate;
		this.name = name;
	}

	public MockInvocationHandler getDelegate() {
		return this.delegate;
	}

	public final Object invoke(final Object proxy, final Method method,
			final Object[] args) throws Throwable {
		if (this.equalsMethod.equals(method)) {
			return Boolean.valueOf(proxy == args[0]);
		}
		if (this.hashCodeMethod.equals(method)) {
			return new Integer(System.identityHashCode(proxy));
		}
		if (this.toStringMethod.equals(method)) {
			return this.mockToString(proxy);
		}
		return this.delegate.invoke(proxy, method, args);
	}

	private String mockToString(final Object proxy) {
		return (this.name != null) ? this.name : "EasyMock for "
				+ this.mockType(proxy);
	}

	private String mockType(final Object proxy) {
		if (Proxy.isProxyClass(proxy.getClass())) {
			return proxy.getClass().getInterfaces()[0].toString();
		} else {
			return proxy.getClass().getSuperclass().toString();
		}
	}
}