/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

public class Range {
	private final int minimum;

	private final int maximum;

	public Range(final int count) {
		this(count, count);
	}

	public Range(final int minimum, final int maximum) {
		if (!(minimum <= maximum)) {
			throw new RuntimeExceptionWrapper(new IllegalArgumentException(
					"minimum must be <= maximum"));
		}

		if (!(minimum >= 0)) {
			throw new RuntimeExceptionWrapper(new IllegalArgumentException(
					"minimum must be >= 0"));
		}

		if (!(maximum >= 1)) {
			throw new RuntimeExceptionWrapper(new IllegalArgumentException(
					"maximum must be >= 1"));
		}
		this.minimum = minimum;
		this.maximum = maximum;
	}

	public boolean contains(final int count) {
		return (this.minimum <= count) && (count <= this.maximum);
	}

	public String expectedAndActual(final int count) {
		return "expected: " + this.toString() + ", actual: " + count;
	}

	public int getMaximum() {
		return this.maximum;
	}

	public int getMinimum() {
		return this.minimum;
	}

	public boolean hasFixedCount() {
		return this.minimum == this.maximum;
	}

	public boolean hasOpenCount() {
		return this.maximum == Integer.MAX_VALUE;
	}

	@Override
	public String toString() {
		if (this.hasFixedCount()) {
			return "" + this.minimum;
		} else if (this.hasOpenCount()) {
			return "at least " + this.minimum;
		} else {
			return "between " + this.minimum + " and " + this.maximum;
		}
	}
}
