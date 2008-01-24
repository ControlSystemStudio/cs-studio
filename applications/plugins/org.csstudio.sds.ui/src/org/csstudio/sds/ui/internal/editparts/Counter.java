package org.csstudio.sds.ui.internal.editparts;

import java.text.DecimalFormat;

/**
 * A simple thread safe counter, which is used for threading tests and simple
 * performance output on the console.
 * 
 * @author Sven Wende
 * 
 */
public final class Counter {
	/**
	 * The number of Events that get collected, before a statistical output is
	 * provided.
	 */
	private static final int EVENTS = 100000;

	/**
	 * Start time of the latest round.
	 */
	private long _startTime;

	/**
	 * End time of the latest round.
	 */
	private long _endTime;

	/**
	 * Event counter.
	 */
	private int _count;

	/**
	 * A number formatter.
	 */
	private DecimalFormat _formatter = new DecimalFormat();

	/**
	 * Increments the counter.
	 */
	public synchronized void increment() {
		if (_count == 0) {
			_startTime = System.currentTimeMillis();
		}
		_count++;

		if (_count == EVENTS) {
			_endTime = System.currentTimeMillis();
			_count = 0;
			long diff = _endTime - _startTime;

			// output
			System.out.println("Processed "
					+ EVENTS
					+ " events in "
					+ diff
					+ " ms ->"
					+ _formatter
							.format(((double) EVENTS / (double) diff))
					+ " EVENTS/ms ->"
					+ _formatter
							.format(((double) diff / (double) EVENTS))
					+ " ms/Event");
		}
	}

}
