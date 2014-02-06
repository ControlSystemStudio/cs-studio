/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.rpcservice;

import org.epics.pvmanager.service.ServiceMethodDescription;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * The description for a pvAccess RPC rpcservice method.
 *
 * @author dkumar
 */
public class RPCServiceMethodDescription {

  final ServiceMethodDescription serviceMethodDescription;
  boolean resultAdded = false;
  ExecutorService executorService;
  final Map<String,String> orderedParameterNames = new HashMap<>();
  final Map<String,String> fieldNames = new HashMap<>();
  final String structureId;
  final boolean isResultStandalone;
  final String name;
  final String operationName;

  final static String FIELD_NAME_EQUALS_NAME = "__NOALIAS__";

  /**
   * A new rpcservice method with the given name and description.
   *
   * @param name the method name
   * @param description the method description
   * @param operationName operation name
   * @param structureId pvStructure id
   * @param isResultStandalone is result standalone i.e. image, table
   */
  public RPCServiceMethodDescription(String name, String description, String operationName,
                                     String structureId, boolean isResultStandalone) {
    this.serviceMethodDescription = new ServiceMethodDescription(name, description);
    this.operationName = operationName;
    this.structureId = structureId;
    this.isResultStandalone = isResultStandalone;
    this.name = name;
  }


  /**
   * Get name
   * @return rpc rpcservice method description name
   */
  public String getName() {
    return this.name;
  }


  /**
   * Get structure id
   * @return structure id
   */
  public String getStructureId() {
    return this.structureId;
  }


  /**
   * Get operation name
   * @return operation name
   */
  public String getOperationName() {return this.operationName;}


  /**
   * Get field names
   * @return field names
   */
  public Map<String,String> getFieldNames() {return this.fieldNames;}


  /**
   * Adds an argument for pvAccess RPC rpcservice.
   *
   * @param name argument name
   * @param description argument description
   * @param type the expected type of the argument
   * @return this
   */
  public RPCServiceMethodDescription addArgument(String name, String fieldName, String description, Class<?> type) {
    serviceMethodDescription.addArgument(name, description, type);
    orderedParameterNames.put(name, fieldName != null ? fieldName : FIELD_NAME_EQUALS_NAME);
    return this;
  }


  /**
   * Adds a result for the pvAccess RPC Service.
   *
   * @param name the result name
   * @param description the result description
   * @return this
   */
  public RPCServiceMethodDescription addRPCResult(String name, String fieldName, String description, Class<?> type) {
    if (resultAdded) {
      throw new IllegalArgumentException("The pvAccess RPC rpcservice can only have one result");
    }
    serviceMethodDescription.addResult(name, description, type);

    if (fieldName != null) {
      this.fieldNames.put(name, fieldName);
    }

    this.resultAdded = true;
    return this;
  }


  RPCServiceMethodDescription executorService(ExecutorService executorService) {
    if (this.executorService != null) {
      throw new IllegalArgumentException("ExecutorService was already set");
    }
    this.executorService = executorService;
    return this;
  }
}
