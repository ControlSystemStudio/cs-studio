/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv;

import java.util.List;

import org.epics.vtype.VType;
import org.epics.vtype.ValueUtil;

/**
 * A simple PV interface for common UI applications. <p>
 * <b>Note:</b> Implementations should make sure all methods are thread safe.
 * @author  Xihui Chen
 */
public interface IPV {


	/**Add a listener to the PV, which will be notified on
	 * events of the PV in the given notify thread.
	 * @param listener the listener
	 */
	public void addPVListener(IPVListener listener);

	/**
	 * Get all values that were buffered in last update cycle that has values. If value is
	 * not buffered, it should return a single item list that wraps {@link #getValue()}
	 * 
	 * @return all values buffered. It can be null.
	 * @throws Exception on error.
	 */
	public List<Object> getAllBufferedValues() throws Exception;

	/**
	 * Get name of the PV.
	 * 
	 * @return name of the PV, cannot be null.
	 */
	public String getName();

	/**
	 * Get the most recent value of the PV in last update cycle that has values.	
	 * In most cases, the returned value is a {@link VType} value. 
	 * {@link VTypeHelper} and {@link ValueUtil} can be used to get the number
	 * or string value, alarm, display, time stamp etc. from the {@link VType} value and
	 * help to format the value.
	 * 
	 * @return value of the PV. It can be null even the PV is connected. For example, 
	 * the value is not prepared yet or it has null as the initial value.
	 * @throws Exception on error.
	 */
	public Object getValue() throws Exception;

	/**
	 * Return true if all values during an update period should be buffered.
	 * 
	 * @return true if all values should be buffered.
	 */
	public boolean isBufferingValues();

	/**If the PV is connected. If the PV is an aggregate of multiple PVs,
	 * the connection state should be determined by the aggregator. For example,
	 * the aggregator countConnected(‘pv1’, ‘pv2’, ‘pv3’,…) should always return 
	 * connected. 
	 * @return true if the PV is connected.
	 */
	public boolean isConnected();

	/**If the pv is paused. When a pv is paused, it will stop sending notifications to listeners
	 * while keeps connected.
	 * @return true if the PV is paused or false if the pv is not started or not paused.
	 */
	public boolean isPaused();

	/**If the {@link #start()} has been called but {@link #stop()}
	 *         has not been called. This method tells nothing if the pv is connected.
	 *         To see if the PV is connected use {@link #isConnected()}.
	 * @return true if the pv is started but not stopped.
	 */
	public boolean isStarted();

	/** @return <code>true</code> if the PV is connected and allowed to write.*/
    public boolean isWriteAllowed();
	
	
    
    /**Remove a pv listener.
     * @param listener the listener to be removed.
     */
    public void removePVListener(IPVListener listener);
    
	/**
	 * Pause notifications while keep the connection.
	 * 
	 * @param paused
	 *            pause notifications if true or resume notifications if false.
	 *            No effect if it is same as {@link #isPaused()}.
	 */
	public void setPaused(boolean paused);
	
	/** Set PV to a given value asynchronously.
     *  Should accept number, number array,
     *  <code>String</code>, maybe more.
     *  @param value Value to write to the PV
     *  @throws Exception on error.
     */
    public void setValue(Object value) throws Exception;
	
    
    /**
	 * Start to connect and listen on the PV.
	 */
	public void start() throws Exception;
    
    /**
	 * Close the connection while keeping all listeners, so when it is restarted, it will work as before,
	 * but it is recommended to use {@link #setPaused(boolean)} instead of calling stop and start again 
	 * because {@link #setPaused(boolean)} will keep the connection. 
	 * When the PV is no longer needed, one should stop it
	 * to release resources. It has no effect if the pv has been stopped already.
	 */
	public void stop();


}
