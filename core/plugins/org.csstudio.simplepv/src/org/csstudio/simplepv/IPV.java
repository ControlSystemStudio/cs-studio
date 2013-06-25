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
 * @author                     Xihui Chen
 */
public interface IPV {

	/**Add a read listener to the PV,
	 * which will be notified on value or connection change in notify thread. 
	 * @param listener the listener
	 */
	public void addPVReadListener(IPVReadListener listener);

	/**Add a write listener to the PV, which will be notified on
	 * write event in notify thread.
	 * @param listener the listener
	 */
	public void addPVWriteListener(IPVWriteListener listener);

	/**
	 * Get all values that were buffered during the update period. If value is
	 * not buffered, it should return a single item list that has the latest
	 * value.
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
	 * Get the most recent value of the PV. If {@link #isValuesBuffered()} is false,
	 * this should return the latest value of the PV, which should be a
	 * {@link VType} value, otherwise, it should return a
	 * {@link List} of all the values buffered during the update period.
	 * {@link VTypeHelper} and {@link ValueUtil} can be used to get the number
	 * value, string value, alarm, display, time stamp etc. from the value and
	 * help to format the value.
	 * 
	 * @return value of the PV. It can be null.
	 * @throws Exception on error.
	 */
	public VType getValue() throws Exception;

	/**If the PV is connected. If the PV is an aggregate of multiple PVs,
	 * the connection state should be determined by the aggregator. For example,
	 * the aggregator countConnected(‘pv1’, ‘pv2’, ‘pv3’,…) should always return 
	 * connected. 
	 * @return true if the PV is connected.
	 */
	public boolean isConnected();

	/**
	 * @return true if the PV is paused or false otherwise.
	 */
	public boolean isPaused();

	/**
	 * @return true if the {@link #start()} has been called but {@link #stop()}
	 *         has not been called.
	 */
	public boolean isStarted();

	/**
	 * Return true if all values during the update period should be buffered.
	 * 
	 * @return true if all values should be buffered.
	 */
	public boolean isValuesBuffered();

	/** @return <code>true</code> if the PV is connected and allowed to write.*/
    public boolean isWriteAllowed();
	
	
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
	

    /** Set PV to a given value synchronously.
     *  Should accept number, number array,
     *  <code>String</code>, maybe more.
     *  @param value Value to write to the PV
     *  @param timeout timeout for pv connection and write operation.
     *  @throws Exception on error.
     */
    public void setValue(Object value, int timeout) throws Exception;
	
    
    /**
	 * Start to connect and listen on the PV.
	 */
	public void start() throws Exception;
    
    /**
	 * Stop the connection. When the PV is no longer needed, one should stop it
	 * to release resources. It has no effect if the pv was stopped.
	 */
	public void stop();


}
