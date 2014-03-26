/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.rpcservice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.epics.pvmanager.service.ServiceMethodDescription;
import org.epics.vtype.VByte;
import org.epics.vtype.VByteArray;
import org.epics.vtype.VDouble;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VFloat;
import org.epics.vtype.VFloatArray;
import org.epics.vtype.VInt;
import org.epics.vtype.VIntArray;
import org.epics.vtype.VLong;
import org.epics.vtype.VLongArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VShort;
import org.epics.vtype.VShortArray;

/**
 * The description for a pvAccess RPC rpcservice method.
 *
 * @author dkumar
 */
public class RPCServiceMethodDescription {

  final ServiceMethodDescription serviceMethodDescription;
  final ServiceMethodDescription relaxedServiceMethodDescription;
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
    this.relaxedServiceMethodDescription = new ServiceMethodDescription(name, description);
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


  static Class<?> relaxArgumentType(Class<?> type)
  {
	  if (type.equals(VDouble.class) ||
	      type.equals(VFloat.class) ||
	      type.equals(VInt.class) ||
	      type.equals(VLong.class) ||
	      type.equals(VShort.class) ||
	      type.equals(VByte.class))
		  type = VNumber.class;
	  else if (type.equals(VDoubleArray.class) ||
		       type.equals(VFloatArray.class) ||
		       type.equals(VIntArray.class) ||
		       type.equals(VLongArray.class) ||
		       type.equals(VShortArray.class) ||
		       type.equals(VByteArray.class))
		  type = VNumberArray.class;
	  
	  return type;
	  
  }
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
    relaxedServiceMethodDescription.addArgument(name, description, relaxArgumentType(type));
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
    relaxedServiceMethodDescription.addResult(name, description, type);
    
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
