/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.util;

/**
 * Manage an update rate per minute. If the update frequency is too fast,
 * overflow is set to <code>true</code>.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 * 
 */
public class OverflowManager {

	private final Double rate;
	private final Integer per;
	private Double allowance;
	private Long last_check;

	private boolean overflowed = false;

	public OverflowManager(final Integer rate, final Integer per) {
		this.rate = Double.valueOf(rate);
		this.per = per;
		this.allowance = Double.valueOf(rate);
		this.last_check = System.currentTimeMillis();
	}

	public synchronized void refreshOverflow() {
		final Long current = System.currentTimeMillis();
		final Long time_passed = current - last_check;
		this.last_check = current;
		this.allowance += time_passed * (rate / per);
		if (allowance > rate)
			allowance = rate; // throttle
		if (allowance < 1.0) {
			overflowed = true;
		} else {
			overflowed = false;
			allowance -= 1.0;
		}
	}

	public synchronized boolean isOverflowed() {
		return overflowed;
	}
}
