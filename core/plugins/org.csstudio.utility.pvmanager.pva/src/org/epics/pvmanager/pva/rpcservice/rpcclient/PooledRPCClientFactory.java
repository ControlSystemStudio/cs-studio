/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.rpcservice.rpcclient;

import org.epics.pvaccess.client.rpc.RPCClient;
import org.epics.pvaccess.server.rpc.RPCRequestException;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A factory creating pooled rpc clients
 * @author dkumar
 */
public class PooledRPCClientFactory {


  //rpc client pools are based on a hostname and a channel name (storage: copy on write list, a few inserts, a lot of traversals)
  private final static CopyOnWriteArrayList<RPCClientPool> poolList = new CopyOnWriteArrayList<>();

  //default pool configuration
  private static PoolConfiguration poolConfiguration = new PoolProperties();

  /**
   * Logger
   */
  private final static Logger log = Logger.getLogger(PooledRPCClientFactory.class.getName());


  /**
   * Override the default configuration with a new one
   * @param poolConfig
   */
  public synchronized static void setPoolConfiguration(PoolConfiguration poolConfig) {

    if (poolConfig == null) {
      log.log(Level.SEVERE, "invalid argument pool configuration:null");
      throw new IllegalArgumentException("setPoolConfiguration: invalid argument");
    }

    //TODO pool configuration should be copied not referenced (testing is easier this way)
    poolConfiguration = poolConfig;
  }


  /**
   * Set pool configuration from text (in java properties format)
   * @param poolConfigAsPropertiesText
   * @throws IOException
   */
  public synchronized static void setPoolConfiguration(String poolConfigAsPropertiesText) throws IOException {

    if (poolConfigAsPropertiesText == null) {
      log.log(Level.SEVERE, "invalid argument pool configuration text: null");
      throw new IllegalArgumentException("setPoolConfiguration: invalid argument");
    }

    PoolConfiguration newConfig = PoolProperties.createFromText(poolConfigAsPropertiesText);
    poolConfiguration = newConfig;
  }


  /**
   * Get the current configuration
   * @return current configuration
   */
  public synchronized  static PoolConfiguration getPoolConfiguration() {
    return poolConfiguration;
  }


  /**
   * Get a pooled rpc client
   *
   * @param hostName
   * @param channelName
   * @return RPCClient
   * @throws RPCRequestException
   */
  public synchronized static RPCClient getRPCClient(String hostName, String channelName) throws RPCRequestException {

    if (channelName == null) {
      throw new IllegalArgumentException("channelName must be set");
    }

    RPCClientPool pool = findPool(hostName, channelName);
    if (pool == null) {
      pool = createPool(hostName, channelName);
    }

    return pool.getRpcClient();
  }


  /**
   * Get a pool statistics for a pool that contains the rpc clients for a hostname and a channel name
   * @param hostName
   * @param channelName
   * @return PoolStatistics a pool statistics (null if a pool is not active)
   */
  public synchronized static PoolStatistics getPoolStatistics(String hostName, String channelName) {

    if (channelName == null) {
      throw new IllegalArgumentException("channelName must be set");
    }

    RPCClientPool pool = findPool(hostName,channelName);
    if (pool != null) {
      return (PoolStatistics)pool;
    }

    return null;
  }


  /**
   * Close the factory and its pools
   */
  public synchronized static void close() {

    Iterator<RPCClientPool> iterator = poolList.iterator();

    while (iterator.hasNext()) {
      RPCClientPool pool = iterator.next();
      pool.close(true);
      poolList.remove(pool);
    }

  }


  public synchronized static void resetConfiguration() {
    poolConfiguration = new PoolProperties();
  }

  private static RPCClientPool createPool(String hostName, String channelName) throws RPCRequestException {
    RPCClientPool rpcClientPool = new RPCClientPool(poolConfiguration, hostName, channelName);
    poolList.add(rpcClientPool);
    return rpcClientPool;
  }


  private static RPCClientPool findPool(String hostName, String channelName) {

    Iterator<RPCClientPool> iterator = poolList.iterator();

    while (iterator.hasNext()) {

      RPCClientPool pool = iterator.next();

      if (hostName == null) {
        if (pool.getHostName() != null) {
          continue;
        }
      } else {
        if (!hostName.equals(pool.getHostName())) {
          continue;
        }
      }

      if (!channelName.equals(pool.getChannelName())) {
        continue;
      }

      return pool;
    }

    return null;
  }
}


