/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

public interface IMocksBehavior extends ILegacyMatcherMethods {

	// replay
	Result addActual(Invocation invocation);

	// record
	void addExpected(ExpectedInvocation expected, Result result, Range count);

	void addStub(ExpectedInvocation expected, Result result);

	void checkOrder(boolean value);

	// verify
	void verify();

}
