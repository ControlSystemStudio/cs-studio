/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import org.easymock.IAnswer;

public class Result implements IAnswer {

	public static Result createAnswerResult(final IAnswer answer) {
		return new Result(answer);
	}

	public static Result createReturnResult(final Object value) {
		return new Result(new IAnswer<Object>() {
			public Object answer() throws Throwable {
				return value;
			}
		});
	}

	public static Result createThrowResult(final Throwable throwable) {
		return new Result(new IAnswer<Object>() {
			public Object answer() throws Throwable {
				throw throwable;
			}
		});
	}

	private final IAnswer value;

	private Result(final IAnswer value) {
		this.value = value;
	}

	public Object answer() throws Throwable {
		return this.value.answer();
	}
}
