/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.rpcservice.rpcclient;

/**
 * Interface describing RPCClientPool statistics
 */
public interface PoolStatistics {

  /**
   * Returns the name of this pool
   *
   * @return String - the name of the pool
   */
  String getName();


  /**
   * Return the number of threads waiting for a rpc client
   *
   * @return number of threads waiting for a rpc client
   */
  int getWaitCount();


  /**
   * Returns the pool properties associated with this rpc client pool
   *
   * @return PoolProperties
   */
  PoolConfiguration getPoolProperties();


  /**
   * Returns the total size of this pool, this includes both busy and idle rpc clients
   *
   * @return int - number of established rpc clients
   */
  int getSize();


  /**
   * Returns the number of rpc clients that are in use
   *
   * @return int - number of established rpc clients that are being used by the application
   */
  int getActive();


  /**
   * Returns the number of idle rpc clients
   *
   * @return int - number of established rpc clients not being used
   */
  int getIdle();


  /**
   * Returns true if {@link org.epics.pvmanager.pva.rpcservice.rpcclient.RPCClientPool#close close} has been called,
   * and the rpc client pool is unusable
   *
   * @return boolean
   */
  boolean isClosed();


  /**
   * Get pool's host name
   * @return host name
   */
  String getHostName();


  /**
   * Get pool's rpc client name
   * @return rpc client name
   */
  String getChannelName();
}
