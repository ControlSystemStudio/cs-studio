/*
 * Copyright (c) 2001-2007 OFFIS, Henri Tremblay.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock;

/**
 * See {@link EasyMock#cmp}
 */
public enum LogicalOperator {
	LESS_THAN("<") {
		@Override
		public boolean matchResult(final int result) {
			return result < 0;
		}
	},
	LESS_OR_EQUAL("<=") {
		@Override
		public boolean matchResult(final int result) {
			return result <= 0;
		}
	},
	EQUAL("==") {
		@Override
		public boolean matchResult(final int result) {
			return result == 0;
		}
	},
	GREATER_OR_EQUAL(">=") {
		@Override
		public boolean matchResult(final int result) {
			return result >= 0;
		}
	},
	GREATER(">") {
		@Override
		public boolean matchResult(final int result) {
			return result > 0;
		}
	};

	private String symbol;

	private LogicalOperator(final String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public abstract boolean matchResult(int result);
}
