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
	 * The default background thread for PV change event notification.
	 */
	private final static ExecutorService SIMPLE_PV_THREAD = Executors
			.newSingleThreadExecutor();
	
	/**Create a PV.
	 * @param name name of the PV. Must not be null.
	 * @param readOnly true if the client doesn't need to write to the PV.
	 * @param maxUpdateRate the maximum update rate in milliseconds.
	 * @param bufferAllValues if all value on the PV should be buffered during two updates.
	 * @param notificationThread the thread on which the read and write listener will be notified. Must not be null.
	 * @param exceptionHandler the handler to handle all exceptions happened in pv connection layer. 
	 * If this is null, pv read listener or pv write listener will be notified on read or write exceptions respectively.
	 * 
	 * @return the PV.
	 */
	public abstract IPV createPV(final String name,
			final boolean readOnly, final int maxUpdateRate,
			final boolean bufferAllValues,
			final Executor notificationThread,
			final ExceptionHandler exceptionHandler);
	
	/**Create a PV with most of the parameters in default value:
	 * <pre>
	 * readOnly = false;
	 * maxUpdateRate = 10ms;
	 * bufferAllValues = false;
	 * notificationThread = a background non-UI thread.
	 * exptionHandler = null;
	 * </pre>
	 * @param name name of the PV. Must not be null.
	 * @return the pv.
	 */
	public IPV createPV(final String name){
		return createPV(name, false, 10, false, SIMPLE_PV_THREAD, null);
	}

}
