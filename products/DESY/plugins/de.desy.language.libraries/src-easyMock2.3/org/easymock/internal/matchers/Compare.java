/*
 * Copyright (c) 2001-2007 OFFIS, Henri Tremblay.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import java.util.Comparator;

import org.easymock.IArgumentMatcher;
import org.easymock.LogicalOperator;

public class Compare<T> implements IArgumentMatcher {

	private final T expected;

	private final Comparator<T> comparator;

	private final LogicalOperator operator;

	public Compare(final T expected, final Comparator<T> comparator,
			final LogicalOperator result) {
		this.expected = expected;
		this.comparator = comparator;
		this.operator = result;
	}

	public void appendTo(final StringBuffer buffer) {
		buffer.append(this.comparator + "(" + this.expected + ") "
				+ this.operator.getSymbol() + " 0");
	}

	@SuppressWarnings("unchecked")
	public boolean matches(final Object actual) {
		if (actual == null) {
			return false;
		}
		return this.operator.matchResult(this.comparator.compare((T) actual,
				this.expected));
	}

}
