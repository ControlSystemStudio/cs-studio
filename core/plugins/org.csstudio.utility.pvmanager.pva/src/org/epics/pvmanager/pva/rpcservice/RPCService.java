/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.rpcservice;

import org.epics.pvmanager.service.Service;

/**
 * A pvmanager rpcservice for calling pvAccess RPC services.
 *
 * @author dkumar
 */
public class RPCService extends Service {

  private final RPCServiceDescription rpcServiceDescription;

  /**
   * Creates a new PVA RPC rpcservice from the given rpcservice description.
   *
   * @param serviceDescription
   */
  public RPCService(RPCServiceDescription serviceDescription) {
    super(serviceDescription.createServiceDescription());
    this.rpcServiceDescription = serviceDescription;
  }


  /**
   * Get RPC rpcservice description
   * @return RPCServiceDescription
   */
  public RPCServiceDescription getRPCServiceDescription() {
    return this.rpcServiceDescription;
  }
}
