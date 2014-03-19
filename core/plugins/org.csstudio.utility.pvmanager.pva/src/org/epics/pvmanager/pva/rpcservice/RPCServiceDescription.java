/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.rpcservice;

import org.epics.pvmanager.service.ServiceDescription;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * The description on how to construct a pvAccess RPC rpcservice.
 *
 * @author dkumar
 */
public class RPCServiceDescription {

  private final ServiceDescription serviceDescription;
  private final String hostName;
  private final String channelName;
  private final String methodFieldName;
  private final boolean useNTQuery;
  private ExecutorService executorService;
  private final Map<String, RPCServiceMethodDescription> rpcServiceMethodDescriptions = new HashMap<>();


  /**
   * A new rpcservice description with the given rpcservice name and description.
   *
   * @param name        the name of the rpcservice
   * @param description a brief description
   */
  public RPCServiceDescription(String name, String description, String hostName, String channelName,
		  String methodFieldName, boolean useNTQuery) {
    serviceDescription = new ServiceDescription(name, description);
    this.hostName = hostName;
    this.channelName = channelName;
    this.methodFieldName = methodFieldName;
    this.useNTQuery = useNTQuery;
  }


  /**
   * Adds a rpcservice method  to the rpcservice.
   *
   * @param rpcServiceMethodDescription a method description
   * @return this
   */
  public RPCServiceDescription addServiceMethod(RPCServiceMethodDescription rpcServiceMethodDescription) {
    rpcServiceMethodDescriptions.put(rpcServiceMethodDescription.getName(), rpcServiceMethodDescription);
    return this;
  }


  /**
   * The ExecutorService on which to execute the rpc rpcservice.
   *
   * @param executorService an executor rpcservice
   * @return this
   */
  public RPCServiceDescription executorService(ExecutorService executorService) {
    if (this.executorService != null) {
      throw new IllegalArgumentException("ExecutorService was already set");
    }
    this.executorService = executorService;
    return this;
  }


  /**
   * Get RPC rpcservice method descriptions
   * @return map with the RPC Service Method Descriptions
   */
  public Map<String, RPCServiceMethodDescription> getRPCServiceMethodDescriptions() {
    return this.rpcServiceMethodDescriptions;
  }


  /**
   * Create rpcservice description from RPC rpcservice methods
   *
   * @return Service description
   */
  ServiceDescription createServiceDescription() {
    for (RPCServiceMethodDescription rpcServiceMethodDescription : rpcServiceMethodDescriptions.values()) {
      rpcServiceMethodDescription.executorService(executorService);
      serviceDescription.addServiceMethod(new RPCServiceMethod(rpcServiceMethodDescription,
        this.hostName, this.channelName, this.methodFieldName, this.useNTQuery));
    }
    return serviceDescription;
  }


}
