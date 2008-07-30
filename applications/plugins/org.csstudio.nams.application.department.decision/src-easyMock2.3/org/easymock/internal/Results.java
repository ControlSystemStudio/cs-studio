/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Results {

	private int callCount;

	private final LinkedList<Range> ranges = new LinkedList<Range>();

	private final List<Result> results = new ArrayList<Result>();

	public void add(final Result result, final Range range) {
		if (!this.ranges.isEmpty()) {
			final Range lastRange = this.ranges.getLast();
			if (!lastRange.hasFixedCount()) {
				throw new RuntimeExceptionWrapper(
						new IllegalStateException(
								"last method called on mock already has a non-fixed count set."));
			}
		}
		this.ranges.add(range);
		this.results.add(result);
	}

	public int getCallCount() {
		return this.callCount;
	}

	public boolean hasValidCallCount() {
		return this.getMainInterval().contains(this.getCallCount());
	}

	public Result next() {
		int currentPosition = 0;
		for (int i = 0; i < this.ranges.size(); i++) {
			final Range interval = this.ranges.get(i);
			if (interval.hasOpenCount()) {
				this.callCount += 1;
				return this.results.get(i);
			}
			currentPosition += interval.getMaximum();
			if (currentPosition > this.callCount) {
				this.callCount += 1;
				return this.results.get(i);
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.getMainInterval().expectedAndActual(this.getCallCount());
	}

	private Range getMainInterval() {
		int min = 0, max = 0;

		for (final Range interval : this.ranges) {
			min += interval.getMinimum();
			if (interval.hasOpenCount() || (max == Integer.MAX_VALUE)) {
				max = Integer.MAX_VALUE;
			} else {
				max += interval.getMaximum();
			}
		}

		return new Range(min, max);
	}
}
