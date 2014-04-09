/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.epics.pvmanager.pva.rpcservice.rpcclient;


import org.epics.pvaccess.client.rpc.RPCClient;
import org.epics.pvaccess.server.rpc.RPCRequestException;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Status;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Represents a pooled rpc client
 *
 * @author Filip Hanik (tomcat connection pool)
 * @author dkumar (modified for a rpc client pool implementation)
 */
class PooledRPCClient implements RPCClient {

  /**
   * Validate when rpc client is borrowed flag
   */
  public static final int VALIDATE_BORROW = 1;

  /**
   * Validate when rpc client is returned flag
   */
  public static final int VALIDATE_RETURN = 2;

  /**
   * Validate when rpc client is idle flag
   */
  public static final int VALIDATE_IDLE = 3;

  /**
   * Validate when rpc client is initialized flag
   */
  public static final int VALIDATE_INIT = 4;

  /**
   * The properties for the rpc client pool
   */
  protected PoolConfiguration poolProperties;

  /**
   * Logger
   */
  private final static Logger log = Logger.getLogger(RPCClientPool.class.getName());

  /**
   * The underlying rpc client
   */
  private volatile RPCClientImpl rpcClient;

  /**
   * When we track abandon traces, this string holds the thread dump
   */
  private String abandonTrace = null;

  /**
   * Timestamp the rpc client was last 'touched' by the pool
   */
  private volatile long timestamp;

  /**
   * Lock for this rpc client only
   */
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);

  /**
   * Set to true if this rpc client has been discarded by the pool
   */
  private volatile boolean discarded = false;

  /**
   * The Timestamp when the last time the connect() method was called successfully
   */
  private volatile long lastConnected = -1;

  /**
   * timestamp to keep track of validation intervals
   */
  private volatile long lastValidated = System.currentTimeMillis();

  /**
   * The pool
   */
  protected RPCClientPool pool;

  private AtomicBoolean released = new AtomicBoolean(false);

  private volatile boolean suspect = false;


  /**
   * Constructor
   * @param prop - pool properties
   * @param pool - the rpc client pool
   */
  public PooledRPCClient(PoolConfiguration prop, RPCClientPool pool) {
    poolProperties = prop;
    this.pool = pool;
  }


  /**
   * Connects the underlying rpc client.
   * @throws RPCRequestException if the method {@link #release()} has been called.
   */
  public void connect() throws RPCRequestException {
    if (released.get()) throw new RPCRequestException(Status.StatusType.ERROR,
      "A rpc client once released, can't be reestablished.");
    if (this.rpcClient != null) {
      try {
        this.disconnect(false);
      } catch (Exception x) {
        log.log(Level.FINE,"Unable to disconnect previous rpc client.", x);
      } //catch
    } //end if


    this.rpcClient = new RPCClientImpl(this.pool.getHostName(), this.pool.getChannelName());

    this.discarded = false;
    this.lastConnected = System.currentTimeMillis();
  }


  /**
   *
   * @return true if connect() was called successfully and disconnect has not yet been called
   */
  public boolean isInitialized() {
    return this.rpcClient !=null;
  }


  /**
   * Issues a call to {@link #disconnect(boolean)} with the argument false followed by a call to
   * {@link #connect()}
   * @throws RPCRequestException if the call to {@link #connect()} fails.
   */
  public void reconnect() throws RPCRequestException {
    this.disconnect(false);
    this.connect();
  } //reconnect


  /**
   * Disconnects the rpc client. All exceptions are logged using debug level.
   * @param finalize if set to true, a call to {@link RPCClientPool#finalize(PooledRPCClient)} is called.
   */
  private void disconnect(boolean finalize) {
    if (isDiscarded()) {
      return;
    }
    setDiscarded(true);
    if (this.rpcClient != null) {
      try {
        pool.disconnectEvent(this, finalize);
        this.rpcClient.destroy();
      }catch (Exception ignore) {
          log.log(Level.FINE, "Unable to close underlying rpc client", ignore);
      }
    }
    this.rpcClient = null;
    lastConnected = -1;
    if (finalize) pool.finalize(this);
  }


  /**
   * Returns abandon timeout in milliseconds
   * @return abandon timeout in milliseconds
   */
  public long getAbandonTimeout() {
    if (poolProperties.getRemoveAbandonedTimeout() <= 0) {
      return Long.MAX_VALUE;
    } else {
      return poolProperties.getRemoveAbandonedTimeout()*1000;
    } //end if
  }


  /**
   * Returns true if the rpc client pool is configured
   * to do validation for a certain action.
   * @param action
   * @return
   */
  private boolean doValidate(int action) {
    if (action == PooledRPCClient.VALIDATE_BORROW &&
      poolProperties.isTestOnBorrow())
      return true;
    else if (action == PooledRPCClient.VALIDATE_RETURN &&
      poolProperties.isTestOnReturn())
      return true;
    else if (action == PooledRPCClient.VALIDATE_IDLE &&
      poolProperties.isTestWhileIdle())
      return true;
    else
      return false;
  }


  /**
   * Validates a rpc client.
   * @param validateAction the action used. One of {@link #VALIDATE_BORROW}, {@link #VALIDATE_IDLE},
   * {@link #VALIDATE_INIT} or {@link #VALIDATE_RETURN} value.
   *
   * @return true if the rpc client was validated successfully. It returns true even if validation was not performed, such as when
   * {@link PoolConfiguration#setValidationInterval(long)} has been called with a positive value.
   * <p>
   * false if the validation failed. The caller should close the rpc client if false is returned since a session could have been left in
   * an unknown state during initialization.
   */
  public boolean validate(int validateAction) {

    if (this.isDiscarded()) {
      return false;
    }

    if (!doValidate(validateAction)) {
      //no validation required
      return true;
    }

    //Don't bother validating if already have recently enough
    long now = System.currentTimeMillis();
    if (validateAction!=VALIDATE_INIT &&
      poolProperties.getValidationInterval() > 0 &&
      (now - this.lastValidated) <
        poolProperties.getValidationInterval()) {
      return true;
    }

    //TODO validate channel rpc

    return false;
  } //validate


  /**
   * The time limit for how long the object
   * can remain unused before it is released
   * @return {@link PoolConfiguration#getMinEvictableIdleTimeMillis()}
   */
  public long getReleaseTime() {
    return this.poolProperties.getMinEvictableIdleTimeMillis();
  }


  /**
   * This method is called if (Now - timeCheckedIn > getReleaseTime())
   * This method disconnects the rpc client, logs an error in debug mode if it happens
   * then sets the {@link #released} flag to false. Any attempts to connect this cached object again
   * will fail per {@link #connect()}
   * The rpc client pool uses the atomic return value to decrement the pool size counter.
   * @return true if this is the first time this method has been called. false if this method has been called before.
   */
  public boolean release() {
    try {
      disconnect(true);
    } catch (Exception x) {
        log.log(Level.FINE, "Unable to close the rpc client", x);
    }
    return released.compareAndSet(false, true);

  }


  /**
   * The pool will set the stack trace when it is check out and
   * checked in
   * @param trace the stack trace for this rpc client
   */

  public void setStackTrace(String trace) {
    abandonTrace = trace;
  }


  /**
   * Returns the stack trace from when this rpc client was borrowed. Can return null if no stack trace was set.
   * @return the stack trace or null of no trace was set
   */
  public String getStackTrace() {
    return abandonTrace;
  }


  /**
   * Sets a timestamp on this rpc client. A timestamp usually means that some operation
   * performed successfully.
   * @param timestamp the timestamp as defined by {@link System#currentTimeMillis()}
   */
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
    setSuspect(false);
  }


  /**
   * Is a rpc client a suspect for a close
   * @return true if suspect
   */
  public boolean isSuspect() {
    return suspect;
  }


  /**
   * Set this rpc client as a suspect/not a suspect
   * @param suspect
   */
  public void setSuspect(boolean suspect) {
    this.suspect = suspect;
  }


  /**
   * An interceptor can call this method with the value true, and the rpc client will be closed when it is returned to the pool.
   * @param discarded - only valid value is true
   * @throws IllegalStateException if this method is called with the value false and the value true has already been set.
   */
  public void setDiscarded(boolean discarded) {
    if (this.discarded && !discarded) throw new IllegalStateException("Unable to change the state once the rpc client has been discarded");
    this.discarded = discarded;
  }


  /**
   * Set the timestamp the rpc client was last validated.
   * This flag is used to keep track when we are using a {@link PoolConfiguration#setValidationInterval(long) validation-interval}.
   * @param lastValidated a timestamp as defined by {@link System#currentTimeMillis()}
   */
  public void setLastValidated(long lastValidated) {
    this.lastValidated = lastValidated;
  }


  /**
   * Sets the pool configuration for this rpc client and rpc client pool.
   * @param poolProperties
   */
  public void setPoolProperties(PoolConfiguration poolProperties) {
    this.poolProperties = poolProperties;
  }


  /**
   * Return the timestamps of last pool action. Timestamps are typically set when rpc clients
   * are borrowed from the pool. It is used to keep track of {@link PoolConfiguration#setRemoveAbandonedTimeout(int) abandon-timeouts}.
   * @return the timestamp of the last pool action as defined by {@link System#currentTimeMillis()}
   */
  public long getTimestamp() {
    return timestamp;
  }


  /**
   * Returns the discarded flag.
   * @return the discarded flag. If the value is true,
   * either {@link #disconnect(boolean)} has been called or it will be called when the rpc client is returned to the pool.
   */
  public boolean isDiscarded() {
    return discarded;
  }


  /**
   * Returns the timestamp of the last successful validation query execution.
   * @return the timestamp of the last successful validation query execution as defined by {@link System#currentTimeMillis()}
   */
  public long getLastValidated() {
    return lastValidated;
  }


  /**
   * Returns the configuration for this rpc client and pool
   * @return the configuration for this rpc client and pool
   */
  public PoolConfiguration getPoolProperties() {
    return poolProperties;
  }


  /**
   * Locks the rpc client only if either {@link PoolConfiguration#isPoolSweeperEnabled()} or
   * {@link PoolConfiguration#getUseLock()} return true. The per rpc client lock ensures thread safety is
   * multiple threads are performing operations on the rpc client.
   * Otherwise this is a noop for performance
   */
  public void lock() {
    if (poolProperties.getUseLock() || this.poolProperties.isPoolSweeperEnabled()) {
      //optimized, only use a lock when there is concurrency
      lock.writeLock().lock();
    }
  }


  /**
   * Unlocks the rpc client only if the sweeper is enabled
   * Otherwise this is a noop for performance
   */
  public void unlock() {
    if (poolProperties.getUseLock() || this.poolProperties.isPoolSweeperEnabled()) {
      //optimized, only use a lock when there is concurrency
      lock.writeLock().unlock();
    }
  }


  /**
   * Returns the timestamp of when the rpc client was last connected.
   * @return the timestamp when this rpc client was created as defined by {@link System#currentTimeMillis()}
   */
  public long getLastConnected() {
    return lastConnected;
  }


  @Override
  public String toString() {
    return "PooledRPCClient["+(this.rpcClient!=null?this.rpcClient.toString():"null")+"]";
  }


  /**
   * Returns true if this rpc client has been released and wont be reused.
   * @return true if the method {@link #release()} has been called
   */
  public boolean isReleased() {
    return released.get();
  }


  /**
   * Return the rpc client back to the pool
   */
  @Override
  public void destroy() {
    this.pool.returnRpcClient(this);
  }


  @Override
  public boolean waitConnect(double timeout) {
    return this.rpcClient.waitConnect(timeout);
  }


  @Override
  public PVStructure request(PVStructure pvArgument, double timeout) throws RPCRequestException {
    return this.rpcClient.request(pvArgument,timeout);
  }


  @Override
  public void sendRequest(PVStructure pvArgument) {
    this.rpcClient.sendRequest(pvArgument);
  }


  @Override
  public boolean waitResponse(double timeout) {
    return this.rpcClient.waitResponse(timeout);
  }
}