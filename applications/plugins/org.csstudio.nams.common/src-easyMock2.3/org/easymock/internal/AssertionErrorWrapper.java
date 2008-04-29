/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

public class AssertionErrorWrapper extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2087349195182278608L;
	private final AssertionError error;

	public AssertionErrorWrapper(final AssertionError error) {
		this.error = error;
	}

	public AssertionError getAssertionError() {
		return this.error;
	}
}
