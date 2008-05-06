/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class JavaProxyFactory<T> implements IProxyFactory<T> {
	public T createProxy(final Class<T> toMock, final InvocationHandler handler) {
		return (T) Proxy.newProxyInstance(toMock.getClassLoader(),
				new Class[] { toMock }, handler);
	}
}
