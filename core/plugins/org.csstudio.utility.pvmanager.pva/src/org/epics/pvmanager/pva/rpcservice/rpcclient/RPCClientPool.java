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
import org.epics.pvdata.pv.Status;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a simple rpc client pool.
 * The Pool uses a {@link PoolProperties} object for storing all the meta information about the rpc client pool.
 * As the underlying implementation, the rpc client pool uses {@link java.util.concurrent.BlockingQueue} to store active and idle rpc clients.
 *
 * @author Filip Hanik (tomcat connection pool)
 * @author dkumar (modified for a rpc client pool implementation)
 */

class RPCClientPool implements PoolStatistics {

  /**
   * Logger
   */
  private final static Logger log = Logger.getLogger(RPCClientPool.class.getName());

  /**
   * Carries the size of the pool, instead of relying on a queue implementation
   * that usually iterates over to get an exact count
   */
  private AtomicInteger size = new AtomicInteger(0);

  /**
   * All the information about the pool
   * These are the properties the pool got instantiated with
   */
  private PoolConfiguration poolProperties;

  /**
   * Contains all the rpc clients that are in use
   * TODO - this shouldn't be a blocking queue, simply a list to hold our objects
   */
  private BlockingQueue<PooledRPCClient> busy;

  /**
   * Contains all the idle rpc clients
   */
  private BlockingQueue<PooledRPCClient> idle;

  /**
   * The thread that is responsible for checking abandoned and idle threads
   */
  private volatile PoolCleaner poolCleaner;

  /**
   * Pool closed flag
   */
  private volatile boolean closed = false;

  //RPCClientPool is a pool of rpc clients with the same host name and client name
  private final String hostName;
  private final String channelName;

  /**
   * counter to track how many threads are waiting for a rpc client
   */
  private AtomicInteger waitcount = new AtomicInteger(0);


  /**
   * Instantiate a rpc client pool. This will create rpc clients if initialSize is larger than 0.
   * The {@link PoolProperties} should not be reused for another rpc client pool.
   *
   * @param prop PoolProperties - all the properties for this rpc client pool
   * @throws RPCRequestException
   */
  public RPCClientPool(PoolConfiguration prop, String hostName, String channelName) throws RPCRequestException {
    //setup quick access variables and pools
    this.hostName = hostName;
    this.channelName = channelName;
    init(prop);
  }


  /**
   * Borrows a rpc client from the pool. If a rpc client is available (in the
   * idle queue) or the pool has not reached {@link PoolProperties#maxActive
   * maxActive} rpc clients, a rpc client is returned immediately. If no
   * rpc clients is available, the pool will attempt to fetch a rpc client for
   * {@link PoolProperties#maxWait maxWait} milliseconds.
   *
   * @return RPCClient - a PooledRPCClient object wrapping the real rpc client.
   * @throws RPCRequestException - if the wait times out or a failure occurs creating a rpc client
   */
  public RPCClient getRpcClient() throws RPCRequestException {
    // check out a rpc client
    return  borrowRpcClient(-1);
  }


  /**
   * Returns the name of this pool
   *
   * @return String - the name of the pool
   */
  @Override
  public String getName() {
    return "RPCClientPool(" + this.hostName + "-" + this.channelName+")";
  }


  /**
   * Return the number of threads waiting for a rpc client
   *
   * @return number of threads waiting for a rpc client
   */
  @Override
  public int getWaitCount() {
    return waitcount.get();
  }


  /**
   * Returns the pool properties associated with this rpc client pool
   *
   * @return PoolProperties
   */
  @Override
  public PoolConfiguration getPoolProperties() {
    return this.poolProperties;
  }


  /**
   * Returns the total size of this pool, this includes both busy and idle rpc clients
   *
   * @return int - number of established rpc clients
   */
  @Override
  public int getSize() {
    return size.get();
  }


  /**
   * Returns the number of rpc clients that are in use
   *
   * @return int - number of established rpc clients that are being used by the application
   */
  @Override
  public int getActive() {
    return busy.size();
  }


  /**
   * Returns the number of idle rpc clients
   *
   * @return int - number of established rpc clients not being used
   */
  @Override
  public int getIdle() {
    return idle.size();
  }


  /**
   * Returns true if {@link #close close} has been called, and the rpc client pool is unusable
   *
   * @return boolean
   */
  @Override
  public boolean isClosed() {
    return this.closed;
  }


  /**
   * Get pool's host name
   * @return host name
   */
  @Override
  public String getHostName() {
    return this.hostName;
  }


  /**
   * Get pool's rpc client name
   * @return rpc client name
   */
  @Override
  public String getChannelName() {
    return channelName;
  }


  /**
   * Closes the pool and all disconnects all idle rpc clients
   * Active rpc clients will be closed upon the {@link org.epics.pvaccess.client.rpc.RPCClient#destroy()} method is called
   * on the underlying rpc client instead of being returned to the pool
   *
   * @param force - true to even close the active rpc client
   */
  protected void close(boolean force) {
    //are we already closed
    if (this.closed) return;
    //prevent other threads from entering
    this.closed = true;
    //stop background thread
    if (poolCleaner != null) {
      poolCleaner.stopRunning();
    }

    /* release all idle rpc clients */
    BlockingQueue<PooledRPCClient> pool = (idle.size() > 0) ? idle : (force ? busy : idle);
    while (pool.size() > 0) {
      try {
        //retrieve the next rpc client
        PooledRPCClient rpcClient = pool.poll(1000, TimeUnit.MILLISECONDS);
        //close it and retrieve the next one, if one is available
        while (rpcClient != null) {
          //close the rpc client
          if (pool == idle)
            release(rpcClient);
          else
            abandon(rpcClient);
          rpcClient = pool.poll(1000, TimeUnit.MILLISECONDS);
        } //while
      } catch (InterruptedException ex) {
        Thread.interrupted();
      }
      if (pool.size() == 0 && force && pool != busy) pool = busy;
    }
  } //closePool


  /**
   * Initialize the rpc client pool - called from the constructor
   *
   * @param properties PoolProperties - properties used to initialize the pool with
   * @throws RPCRequestException if initialization fails
   */
  protected void init(PoolConfiguration properties) throws RPCRequestException {

    poolProperties = properties;

    //make sure the pool is properly configured
    if (properties.getMaxActive() < 1) {
      log.log(Level.WARNING, "maxActive is smaller than 1, setting maxActive to: " + PoolProperties.DEFAULT_MAX_ACTIVE);
      properties.setMaxActive(PoolProperties.DEFAULT_MAX_ACTIVE);
    }
    if (properties.getMaxActive()<properties.getInitialSize()) {
      log.log(Level.FINE, "initialSize is larger than maxActive, setting initialSize to: " + properties.getMaxActive());
      properties.setInitialSize(properties.getMaxActive());
    }
    if (properties.getMinIdle() > properties.getMaxActive()) {
      log.log(Level.WARNING, "minIdle is larger than maxActive, setting minIdle to: " + properties.getMaxActive());
      properties.setMinIdle(properties.getMaxActive());
    }
    if (properties.getMaxIdle() > properties.getMaxActive()) {
      log.log(Level.WARNING, "maxIdle is larger than maxActive, setting maxIdle to: " + properties.getMaxActive());
      properties.setMaxIdle(properties.getMaxActive());
    }
    if (properties.getMaxIdle() < properties.getMinIdle()) {
      log.log(Level.WARNING, "maxIdle is smaller than minIdle, setting maxIdle to: " + properties.getMinIdle());
      properties.setMaxIdle(properties.getMinIdle());
    }

    busy = new ArrayBlockingQueue<PooledRPCClient>(properties.getMaxActive(), false);

    idle = new ArrayBlockingQueue<PooledRPCClient>(properties.getMaxActive(), false);


    //if the evictor thread is supposed to run, start it now
    if (properties.isPoolSweeperEnabled()) {
      poolCleaner = new PoolCleaner("[Pool-Cleaner]:" + this.hostName + "-"
        + this.channelName, this, properties.getTimeBetweenEvictionRunsMillis());
      poolCleaner.start();
    }


    //initialize the pool with its initial set of members
    PooledRPCClient[] initialPool = new PooledRPCClient[poolProperties.getInitialSize()];

    try {
      for (int i = 0; i < initialPool.length; i++) {
        initialPool[i] = this.borrowRpcClient(0); //don't wait, should be no contention
      } //for

    } catch (RPCRequestException x) {
      close(true);
      throw x;
    } finally {
      //return the members as idle to the pool
      for (int i = 0; i < initialPool.length; i++) {
        if (initialPool[i] != null) {
          try {this.returnRpcClient(initialPool[i]);}catch(Exception x){/*NOOP*/}
        } //end if
      } //for
    } //catch
    closed = false;
  }


  /**
   * thread safe way to abandon a rpc client
   * signals a rpc client to be abandoned.
   * this will disconnect the rpc client, and log the stack trace if logAbanded=true
   *
   * @param rpcClient PooledRPCClient
   */
  protected void abandon(PooledRPCClient rpcClient) {
    if (rpcClient == null)
      return;
    try {
      rpcClient.lock();
      String trace = rpcClient.getStackTrace();
      if (getPoolProperties().isLogAbandoned()) {
        log.log(Level.WARNING, "Channel has been abandoned " + rpcClient + ":" + trace);
      }
      //release the rpc client
      release(rpcClient);
    } finally {
      rpcClient.unlock();
    }
  }


  /**
   * thread safe way to abandon a rpc client
   * signals a rpc client to be abandoned.
   * this will disconnect the rpc client, and log the stack trace if logAbanded=true
   *
   * @param rpcClient PooledRPCClient
   */
  protected void suspect(PooledRPCClient rpcClient) {
    if (rpcClient == null)
      return;
    if (rpcClient.isSuspect())
      return;
    try {
      rpcClient.lock();
      String trace = rpcClient.getStackTrace();
      if (getPoolProperties().isLogAbandoned()) {
        log.log(Level.WARNING, "Channel has been marked suspect, possibly abandoned " + rpcClient + "[" + (System.currentTimeMillis() - rpcClient.getTimestamp()) + " ms.]:" + trace);
      }

      rpcClient.setSuspect(true);
    } finally {
      rpcClient.unlock();
    }
  }


  /**
   * thread safe way to release a rpc client
   *
   * @param rpcClient PooledRPCClient
   */
  protected void release(PooledRPCClient rpcClient) {
    if (rpcClient == null)
      return;
    try {
      rpcClient.lock();
      if (rpcClient.release()) {
        //counter only decremented once
        size.addAndGet(-1);
      }
    } finally {
      rpcClient.unlock();
    }
    // we've asynchronously reduced the number of rpc clients
    // we could have threads stuck in idle.poll(timeout) that will never be
    // notified
    if (waitcount.get() > 0) {
      idle.offer(create(true));
    }
  }


  /**
   * Thread safe way to retrieve a rpc client from the pool
   *
   * @param wait - time to wait, overrides the maxWait from the properties,
   *             set to -1 if you wish to use maxWait, 0 if you wish no wait time.
   * @return PooledRPCClient
   * @throws RPCRequestException
   */
  private PooledRPCClient borrowRpcClient(int wait) throws RPCRequestException {

    if (isClosed()) {
      throw new RPCRequestException(Status.StatusType.ERROR, "Channel pool closed.");
    } //end if

    //get the current time stamp
    long now = System.currentTimeMillis();
    //see if there is one available immediately
    PooledRPCClient rpcClient = idle.poll();

    while (true) {
      if (rpcClient != null) {
        //configure the rpc client and return it
        PooledRPCClient result = borrowRpcClient(now, rpcClient);
        //null should never be returned, but was in a previous impl.
        if (result != null) return result;
      }

      //if we get here, see if we need to create one
      //this is not 100% accurate since it doesn't use a shared
      //atomic variable - a rpc client can become idle while we are creating
      //a new rpc client
      if (size.get() < getPoolProperties().getMaxActive()) {
        //atomic duplicate check
        if (size.addAndGet(1) > getPoolProperties().getMaxActive()) {
          //if we got here, two threads passed through the first if
          size.decrementAndGet();
        } else {
          //create a rpc client, we're below the limit
          return createRpcClient(now, rpcClient);
        }
      } //end if

      //calculate wait time for this iteration
      long maxWait = wait;
      //if the passed in wait time is -1, means we should use the pool property value
      if (wait == -1) {
        maxWait = (getPoolProperties().getMaxWait() <= 0) ? Long.MAX_VALUE : getPoolProperties().getMaxWait();
      }

      long timetowait = Math.max(0, maxWait - (System.currentTimeMillis() - now));
      waitcount.incrementAndGet();
      try {
        //retrieve an existing rpc client
        rpcClient = idle.poll(timetowait, TimeUnit.MILLISECONDS);
      } catch (InterruptedException ex) {
        Thread.interrupted();//clear the flag, and bail out
        RPCRequestException sx = new RPCRequestException(Status.StatusType.ERROR, "Pool wait interrupted.");
        sx.initCause(ex);
        throw sx;
      } finally {
        waitcount.decrementAndGet();
      }
      if (maxWait == 0 && rpcClient == null) { //no wait, return one if we have one
        throw new RPCRequestException(Status.StatusType.ERROR, "[" + Thread.currentThread().getName() + "] " +
          "NoWait: Pool empty. Unable to fetch a channel, none available[" + busy.size() + " in use].");
      }
      //we didn't get a rpc client, lets see if we timed out
      if (rpcClient == null) {
        if ((System.currentTimeMillis() - now) >= maxWait) {
          throw new RPCRequestException(Status.StatusType.ERROR, "[" + Thread.currentThread().getName() + "] " +
            "Timeout: Pool empty. Unable to fetch a channel in " + (maxWait / 1000) +
            " seconds, none available[" + busy.size() + " in use].");
        } else {
          //no timeout, lets try again
          continue;
        }
      }
    } //while
  }


  /**
   * Creates a rpc client.
   *
   * @param now     timestamp of when this was called
   * @param notUsed Argument not used
   * @return a PooledRPCClient that has been connected
   * @throws RPCRequestException
   */
  protected PooledRPCClient createRpcClient(long now, PooledRPCClient notUsed) throws RPCRequestException {

    //no rpc client where available we'll create one
    PooledRPCClient rpcClient = create(false);

    boolean error = false;
    try {
      //connect and validate the rpc client
      rpcClient.lock();
      rpcClient.connect();
      if (rpcClient.validate(PooledRPCClient.VALIDATE_INIT)) {
        //no need to lock a new one, its not contented
        rpcClient.setTimestamp(now);
        if (getPoolProperties().isLogAbandoned()) {
          rpcClient.setStackTrace(getThreadDump());
        }
        if (!busy.offer(rpcClient)) {
          log.log(Level.FINE, "Channel doesn't fit into busy array, channel will not be traceable.");
        }
        return rpcClient;
      } else {
        //validation failed, make sure we disconnect
        //and clean up
        error = true;
      } //end if
    } catch (Exception e) {
      error = true;
      log.log(Level.FINE, "Unable to create a new ChannelRPC.", e);
      if (e instanceof RPCRequestException) {
        throw (RPCRequestException) e;
      } else {
        RPCRequestException ex = new RPCRequestException(Status.StatusType.ERROR, e.getMessage());
        ex.initCause(e);
        throw ex;
      }
    } finally {
      // rpc client can never be null here
      if (error) {
        release(rpcClient);
      }
      rpcClient.unlock();
    }//catch
    return null;
  }

  /**
   * Validates and configures a previously idle rpc client
   *
   * @param now - timestamp
   * @param rpcClient - the rpc client to validate and configure
   * @return ch
   * @throws RPCRequestException if a validation error happens
   */
  protected PooledRPCClient borrowRpcClient(long now, PooledRPCClient rpcClient) throws RPCRequestException {
    //we have a rpc client, lets set it up

    //flag to see if we need to nullify
    boolean setToNull = false;
    try {
      rpcClient.lock();


      if (rpcClient.isReleased()) {
        return null;
      }

      if (!rpcClient.isDiscarded() && !rpcClient.isInitialized()) {
        //attempt to connect
        rpcClient.connect();
      }

      if ((!rpcClient.isDiscarded()) && rpcClient.validate(PooledRPCClient.VALIDATE_BORROW)) {
        //set the timestamp
        rpcClient.setTimestamp(now);
        if (getPoolProperties().isLogAbandoned()) {
          //set the stack trace for this pool
          rpcClient.setStackTrace(getThreadDump());
        }
        if (!busy.offer(rpcClient)) {
          log.log(Level.FINE, "Channel doesn't fit into busy array, channel will not be traceable.");
        }
        return rpcClient;
      }
      //if we reached here, that means the rpc client
      //is either has another principal, is discarded or validation failed.
      //we will make one more attempt
      //in order to guarantee that the thread that just acquired
      //the rpc client shouldn't have to poll again.
      try {
        rpcClient.reconnect();
        if (rpcClient.validate(PooledRPCClient.VALIDATE_INIT)) {
          //set the timestamp
          rpcClient.setTimestamp(now);
          if (getPoolProperties().isLogAbandoned()) {
            //set the stack trace for this pool
            rpcClient.setStackTrace(getThreadDump());
          }
          if (!busy.offer(rpcClient)) {
            log.log(Level.FINE, "Channel doesn't fit into busy array, channel will not be traceable.");
          }
          return rpcClient;
        } else {
          //validation failed.
          release(rpcClient);
          setToNull = true;
          throw new RPCRequestException(Status.StatusType.ERROR, "Failed to validate a newly established channel.");
        }
      } catch (Exception x) {
        release(rpcClient);
        setToNull = true;
        if (x instanceof RPCRequestException) {
          throw (RPCRequestException) x;
        } else {
          RPCRequestException ex = new RPCRequestException(Status.StatusType.ERROR, x.getMessage());
          ex.initCause(x);
          throw ex;
        }
      }
    } finally {
      rpcClient.unlock();
      if (setToNull) {
        rpcClient = null;
      }
    }
  }


  /**
   * Determines if a rpc client should be closed upon return to the pool.
   *
   * @param ch    - the rpc client
   * @param action - the validation action that should be performed
   * @return true if the rpc client should be closed
   */
  protected boolean shouldClose(PooledRPCClient ch, int action) {
    if (ch.isDiscarded()) return true;
    if (isClosed()) return true;
    if (!ch.validate(action)) return true;
    if (getPoolProperties().getMaxAge() > 0) {
      return (System.currentTimeMillis() - ch.getLastConnected()) > getPoolProperties().getMaxAge();
    } else {
      return false;
    }
  }


  /**
   * Returns a rpc client to the pool
   * If the pool is closed, the rpc client will be released
   * If the rpc client is not part of the busy queue, it will be released.
   * If {@link PoolProperties#testOnReturn} is set to true it will be validated
   *
   * @param ch PooledRPCClient to be returned to the pool
   */
  protected void returnRpcClient(PooledRPCClient ch) {
    if (isClosed()) {
      //if the rpc client pool is closed
      //close the rpc client instead of returning it
      release(ch);
      return;
    } //end if

    if (ch != null) {
      try {
        ch.lock();

        if (busy.remove(ch)) {

          if (!shouldClose(ch, PooledRPCClient.VALIDATE_RETURN)) {
            ch.setStackTrace(null);
            ch.setTimestamp(System.currentTimeMillis());
            if (((idle.size() >= poolProperties.getMaxIdle()) && !poolProperties.isPoolSweeperEnabled()) || (!idle.offer(ch))) {
              log.log(Level.FINE, "Channel [" + ch + "] will be closed and not returned to the pool, idle["
                + idle.size() + "]>=maxIdle[" + poolProperties.getMaxIdle() + "] idle.offer failed.");

              release(ch);
            }
          } else {
            log.log(Level.FINE, "Channel [" + ch + "] will be closed and not returned to the pool.");

            release(ch);
          } //end if
        } else {
          log.log(Level.FINE, "Channel [" + ch + "] will be closed and not returned to the pool, busy.remove failed.");

          release(ch);
        }
      } finally {
        ch.unlock();
      }
    } //end if
  } //checkIn


  /**
   * Determines if a rpc client should be abandoned based on
   * {@link PoolProperties#abandonWhenPercentageFull} setting.
   *
   * @return true if the rpc client should be abandoned
   */
  protected boolean shouldAbandon() {
    if (poolProperties.getAbandonWhenPercentageFull() == 0) return true;
    float used = busy.size();
    float max = poolProperties.getMaxActive();
    float perc = poolProperties.getAbandonWhenPercentageFull();
    return (used / max * 100f) >= perc;
  }

  /**
   * Iterates through all the busy rpc clients and checks for rpc clients that have timed out
   */
  public void checkAbandoned() {
    try {
      if (busy.size() == 0) return;
      Iterator<PooledRPCClient> locked = busy.iterator();
      int sto = getPoolProperties().getSuspectTimeout();
      while (locked.hasNext()) {
        PooledRPCClient ch = locked.next();
        boolean setToNull = false;
        try {
          ch.lock();
          //the rpc client has been returned to the pool
          //ignore it
          if (idle.contains(ch))
            continue;
          long time = ch.getTimestamp();
          long now = System.currentTimeMillis();
          if (shouldAbandon() && (now - time) > ch.getAbandonTimeout()) {
            busy.remove(ch);
            abandon(ch);
            setToNull = true;
          } else if (sto > 0 && (now - time) > (sto * 1000)) {
            suspect(ch);
          } else {
            //do nothing
          } //end if
        } finally {
          ch.unlock();
          if (setToNull)
            ch = null;
        }
      } //while
    } catch (ConcurrentModificationException e) {
      log.log(Level.WARNING, "checkAbandoned failed.", e);
    } catch (Exception e) {
      log.log(Level.WARNING, "checkAbandoned failed, it will be retried.", e);
    }
  }

  /**
   * Iterates through the idle rpc clients and resizes the idle pool based on parameters
   * {@link PoolProperties#maxIdle}, {@link PoolProperties#minIdle}, {@link PoolProperties#minEvictableIdleTimeMillis}
   */
  public void checkIdle() {
    try {
      if (idle.size() == 0) return;
      long now = System.currentTimeMillis();
      Iterator<PooledRPCClient> unlocked = idle.iterator();
      while ((idle.size() >= getPoolProperties().getMinIdle()) && unlocked.hasNext()) {
        PooledRPCClient ch = unlocked.next();
        boolean setToNull = false;
        try {
          ch.lock();
          //the rpc client been taken out, we can't clean it up
          if (busy.contains(ch))
            continue;
          long time = ch.getTimestamp();
          if ((ch.getReleaseTime() > 0) && ((now - time) > ch.getReleaseTime()) &&
            (getSize() > getPoolProperties().getMinIdle())) {
            release(ch);
            idle.remove(ch);
            setToNull = true;
          } else {
            //do nothing
          } //end if
        } finally {
          ch.unlock();
          if (setToNull)
            ch = null;
        }
      } //while
    } catch (ConcurrentModificationException e) {
      log.log(Level.FINE, "checkIdle failed.", e);
    } catch (Exception e) {
      log.log(Level.FINE, "checkIdle failed, it will be retried.", e);
    }

  }


  /**
   * Forces a validation of all idle rpc client if {@link PoolProperties#testWhileIdle} is set.
   */
  public void testAllIdle() {
    try {
      if (idle.size() == 0) return;
      Iterator<PooledRPCClient> unlocked = idle.iterator();
      while (unlocked.hasNext()) {
        PooledRPCClient ch = unlocked.next();
        try {
          ch.lock();
          //the rpc client been taken out, we can't clean it up
          if (busy.contains(ch))
            continue;
          if (!ch.validate(PooledRPCClient.VALIDATE_IDLE)) {
            idle.remove(ch);
            release(ch);
          }
        } finally {
          ch.unlock();
        }
      } //while
    } catch (ConcurrentModificationException e) {
      log.log(Level.WARNING, "testAllIdle failed.", e);
    } catch (Exception e) {
      log.log(Level.WARNING, "testAllIdle failed, it will be retried.", e);
    }

  }


  /**
   * Creates a stack trace representing the existing thread's current state.
   *
   * @return a string object representing the current state.
   */
  protected static String getThreadDump() {
    Exception x = new Exception();
    x.fillInStackTrace();
    return getStackTrace(x);
  }


  /**
   * Convert an exception into a String
   *
   * @param x - the throwable
   * @return a string representing the stack trace
   */
  public static String getStackTrace(Throwable x) {
    if (x == null) {
      return null;
    } else {
      java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
      java.io.PrintStream writer = new java.io.PrintStream(bout);
      x.printStackTrace(writer);
      String result = bout.toString();
      return (x.getMessage() != null && x.getMessage().length() > 0) ? x.getMessage() + ";" + result : result;
    } //end if
  }


  /**
   * Create a new pooled rpc client  object. Not connected nor validated.
   *
   * @return a pooled rpc client rpc object
   */
  protected PooledRPCClient create(boolean incrementCounter) {
    if (incrementCounter) size.incrementAndGet();
    PooledRPCClient rpcClient = new PooledRPCClient(getPoolProperties(), this);
    return rpcClient;
  }


  /**
   * Hook to perform final actions on a pooled rpc client object once it has been disconnected and will be discarded
   *
   * @param rpcClient
   */
  protected void finalize(PooledRPCClient rpcClient) {
  }


  /**
   * Hook to perform final actions on a pooled rpc client object once it has been disconnected and will be discarded
   *
   * @param rpcClient
   */
  protected void disconnectEvent(PooledRPCClient rpcClient, boolean finalizing) {
  }


  protected class PoolCleaner extends Thread {
    protected RPCClientPool pool;
    protected long sleepTime;
    protected volatile boolean run = true;

    PoolCleaner(String name, RPCClientPool pool, long sleepTime) {
      super(name);
      this.setDaemon(true);
      this.pool = pool;
      this.sleepTime = sleepTime;
      if (sleepTime <= 0) {
        log.log(Level.WARNING, "channel pool evicter thread interval is set to 0, defaulting to 30 seconds");
        this.sleepTime = 1000 * 30;
      } else if (sleepTime < 1000) {
        log.log(Level.WARNING, "channel pool evicter thread interval is set to lower than 1 second.");
      }
    }

    @Override
    public void run() {
      while (run) {
        try {
          sleep(sleepTime);
        } catch (InterruptedException e) {
          // ignore it
          Thread.interrupted();
          continue;
        } //catch

        if (pool.isClosed()) {
          if (pool.getSize() <= 0) {
            run = false;
          }
        } else {
          try {
            if (pool.getPoolProperties().isRemoveAbandoned())
              pool.checkAbandoned();
            if (pool.getPoolProperties().getMinIdle() < pool.idle.size())
              pool.checkIdle();
            if (pool.getPoolProperties().isTestWhileIdle())
              pool.testAllIdle();
          } catch (Exception x) {
            log.log(Level.SEVERE,"", x);
          } //catch
        } //end if
      } //while
    } //run


    public void stopRunning() {
      run = false;
      interrupt();
    }
  }
}