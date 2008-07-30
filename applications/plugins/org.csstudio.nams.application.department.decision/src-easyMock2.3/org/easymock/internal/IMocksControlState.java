/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import org.easymock.IAnswer;

public interface IMocksControlState extends ILegacyMethods {

	void andAnswer(IAnswer answer);

	void andReturn(Object value);

	void andStubAnswer(IAnswer answer);

	void andStubReturn(Object value);

	void andStubThrow(Throwable throwable);

	void andThrow(Throwable throwable);

	void assertRecordState();

	void asStub();

	void checkOrder(boolean value);

	Object invoke(Invocation invocation) throws Throwable;

	void replay();

	void times(Range range);

	void verify();
}
