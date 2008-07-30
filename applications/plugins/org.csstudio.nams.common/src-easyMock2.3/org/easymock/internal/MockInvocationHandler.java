/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public final class MockInvocationHandler implements InvocationHandler {

	final MocksControl control;

	public MockInvocationHandler(final MocksControl control) {
		this.control = control;
	}

	public MocksControl getControl() {
		return this.control;
	}

	public Object invoke(final Object proxy, final Method method,
			final Object[] args) throws Throwable {
		try {
			if (this.control.getState() instanceof RecordState) {
				LastControl.reportLastControl(this.control);
			}
			return this.control.getState().invoke(
					new Invocation(proxy, method, args));
		} catch (final RuntimeExceptionWrapper e) {
			throw e.getRuntimeException().fillInStackTrace();
		} catch (final AssertionErrorWrapper e) {
			throw e.getAssertionError().fillInStackTrace();
		} catch (final ThrowableWrapper t) {
			throw t.getThrowable().fillInStackTrace();
		}
	}
}