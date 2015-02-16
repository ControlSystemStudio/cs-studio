/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The abstract factory that creates specific PV.
 * @author           Xihui Chen
 */
public abstract class AbstractPVFactory {	
	
	/**
	 * The default background thread for PV change event notification. It will only be created 
	 * on its first use.
	 */
	static ExecutorService SIMPLE_PV_THREAD = null;
	
	/**Create a PV.
	 * @param name name of the PV. Must not be null.
	 * @param readOnly true if the client doesn't need to write to the PV.
	 * @param minUpdatePeriodInMs the minimum update period in milliseconds, 
	 * which means the PV change event notification will not be faster than this period.
	 * @param bufferAllValues if all value on the PV should be buffered during two updates.
	 * @param notificationThread the thread on which the read and write listener will be notified. Must not be null.
	 * @param exceptionHandler the handler to handle all exceptions happened in pv connection layer. 
	 * If this is null, pv read listener or pv write listener will be notified on read or write exceptions respectively.
	 * 
	 * @return the PV.
	 * @throws Exception error on creating pv.
	 */
	public abstract IPV createPV(final String name,
			final boolean readOnly, final long minUpdatePeriodInMs,
			final boolean bufferAllValues,
			final Executor notificationThread,
			final ExceptionHandler exceptionHandler) throws Exception;
	
	/**Create a PV with most of the parameters in default value:
	 * <pre>
	 * readOnly = false;
	 * minUpdatePeriod = 10 ms;
	 * bufferAllValues = false;
	 * notificationThread = {@link #SIMPLE_PV_THREAD}
	 * exceptionHandler = null;
	 * </pre>
	 * @param name name of the PV. Must not be null.
	 * @return the pv.
	 * @throws Exception error on creating pv.
 	 */
	public synchronized IPV createPV(final String name) throws Exception{		
		if (SIMPLE_PV_THREAD == null)
			SIMPLE_PV_THREAD = Executors.newSingleThreadExecutor();	
		return createPV(name, false, 10,
				false, SIMPLE_PV_THREAD, null);
	}
	
	public static synchronized ExecutorService getDefaultPVNotificationThread() {
		if (SIMPLE_PV_THREAD == null)
			SIMPLE_PV_THREAD = Executors.newSingleThreadExecutor();
		return SIMPLE_PV_THREAD;
	}

}
