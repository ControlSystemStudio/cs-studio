/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.rpcservice;

import org.epics.pvaccess.client.rpc.RPCClient;
import org.epics.pvaccess.server.rpc.RPCRequestException;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.pv.*;
import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.pva.adapters.PVFieldNTNameValueToVTable;
import org.epics.pvmanager.pva.adapters.PVFieldToVFloatArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVImage;
import org.epics.pvmanager.pva.adapters.PVFieldToVTable;
import org.epics.pvmanager.pva.rpcservice.rpcclient.PooledRPCClientFactory;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.vtype.*;

import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * The implementation of a pvAccess RPC rpcservice method.
 *
 * @author dkumar
 */
class RPCServiceMethod extends ServiceMethod {

  //consts
  private final static String FIELD_OPERATION_NAME = "op";

  private final static FieldCreate fieldCreate = FieldFactory.getFieldCreate();
  private final Structure requestStructure;
  private final Map<String, String> parameterNames;
  private final RPCServiceMethodDescription rpcServiceMethodDescription;
  private final String hostName;
  private final String channelName;


  /**
   * Creates a new rpcservice method.
   *
   * @param rpcServiceMethodDescription a method description
   */
  RPCServiceMethod(RPCServiceMethodDescription rpcServiceMethodDescription, String hostName, String channelName) {
    super(rpcServiceMethodDescription.serviceMethodDescription);
    this.rpcServiceMethodDescription = rpcServiceMethodDescription;
    this.parameterNames = rpcServiceMethodDescription.orderedParameterNames;
    this.requestStructure = createRequestStructure(rpcServiceMethodDescription.structureId);
    this.hostName = hostName;
    this.channelName = channelName;
  }


  private Structure createRequestStructure(String structureId) {
    if ((structureId == null) || (structureId.isEmpty())) {
      return fieldCreate.createStructure(createRequestFieldNames(), createRequestFieldTypes());
    } else
      return fieldCreate.createStructure(structureId, createRequestFieldNames(), createRequestFieldTypes());
  }


  private String[] createRequestFieldNames() {

    //only operation name
    if ((this.parameterNames == null) || (parameterNames.isEmpty())) {
      return new String[]{FIELD_OPERATION_NAME};
    }

    //operation name + parameter names/fieldnames
    List<String> fieldNames = new ArrayList<String>(Arrays.asList(FIELD_OPERATION_NAME));
    for (String parameterName : this.parameterNames.keySet()) {
      String fieldName = this.parameterNames.get(parameterName);
      if (fieldName.equals(RPCServiceMethodDescription.FIELD_NAME_EQUALS_NAME)) {
        fieldNames.add(parameterName);
      } else {
        fieldNames.add(fieldName);
      }
    }

    return fieldNames.toArray(new String[fieldNames.size()]);
  }


  private Field[] createRequestFieldTypes() {

    //only operation name type
    if ((this.parameterNames == null) || (parameterNames.isEmpty())) {
      return new Field[]{fieldCreate.createScalar(ScalarType.pvString)};
    }

    //operation name type + parameter types
    List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fieldCreate.createScalar(ScalarType.pvString)));
    int i = 0;
    for (String parameterName : this.parameterNames.keySet()) {
      fieldList.add(convertToPvType(getArgumentTypes().get(parameterName)));
    }

    return fieldList.toArray(new Field[fieldList.size()]);
  }


  private Field convertToPvType(Class<?> argType) {

    if (argType == null) {
      throw new IllegalArgumentException("Type not set: null");
    }

    if (argType.isAssignableFrom(VDouble.class)) {
      return fieldCreate.createScalar(ScalarType.pvDouble);
    } else if (argType.isAssignableFrom(VFloat.class)) {
      return fieldCreate.createScalar(ScalarType.pvFloat);
    } else if (argType.isAssignableFrom(VFloatArray.class)) {
      return fieldCreate.createScalarArray(ScalarType.pvFloat);
    } else if (argType.isAssignableFrom(VString.class)) {
      return fieldCreate.createScalar(ScalarType.pvString);
    } else if (argType.isAssignableFrom(VInt.class)) {
      return fieldCreate.createScalar(ScalarType.pvInt);
    } else if (argType.isAssignableFrom(VBoolean.class)) {
      return fieldCreate.createScalar(ScalarType.pvBoolean);
    }

    throw new IllegalArgumentException("Argument class " + argType.getSimpleName() + " not supported in pvAccess RPC Service");
  }


  private ExecutorService getExecutorService() {
    return this.rpcServiceMethodDescription.executorService;
  }


  private boolean isResultQuery() {
    return !getResultDescriptions().isEmpty();
  }


  @Override
  public void executeMethod(final Map<String, Object> parameters, final WriteFunction<Map<String, Object>> callback, final WriteFunction<Exception> errorCallback) {

    RPCClient rpcClient = null;

    try {

      rpcClient = PooledRPCClientFactory.getRPCClient(this.hostName, this.channelName);

    } catch (RPCRequestException e) {
      errorCallback.writeValue(e);
      return;
    }


    try {
      String methodName = this.rpcServiceMethodDescription.getOperationName() != null ?
        this.rpcServiceMethodDescription.getOperationName() : this.rpcServiceMethodDescription.getName();

      PVStructure pvResult = rpcClient.request(createPvRequest(parameters, methodName), 3.0);

      VType vResult = createResult(pvResult);
      if (vResult != null) {
        Map<String, Object> resultMap = new HashMap<>();
        String resultName = getResultDescriptions().keySet().toArray(new String[getResultDescriptions().size()])[0];
        resultMap.put(resultName, vResult);
        callback.writeValue(resultMap);
      }
    } catch (RPCRequestException rre) {
      errorCallback.writeValue(rre);

    } finally {
      //back to the pool
      rpcClient.destroy();
    }
  }


  PVStructure createPvRequest(final Map<String, Object> parameters, String methodName) {

    PVStructure pvRequest = PVDataFactory.getPVDataCreate().createPVStructure(this.requestStructure);

    pvRequest.getStringField(FIELD_OPERATION_NAME).put(methodName);

    if ((parameters == null) || (parameters.isEmpty())) {
      return pvRequest;
    }

    for (String parameterName : parameters.keySet()) {

      //check if the parameter name is only an alias (if we get a field name back, it's an alias)
      String fieldName = this.parameterNames.get(parameterName);

      if ((fieldName == null) || (fieldName.equals(RPCServiceMethodDescription.FIELD_NAME_EQUALS_NAME))) {
        fieldName = null;
      }

      Object value = parameters.get(parameterName);

      if (value instanceof VString) {

        PVString field = pvRequest.getStringField(fieldName != null ? fieldName : parameterName);
        if (field == null) {
          throw new IllegalArgumentException("String field " + parameterName + " not found");
        }
        field.put(((VString) value).getValue());

      } else if (value instanceof VDouble) {

        PVDouble field = pvRequest.getDoubleField(fieldName != null ? fieldName : parameterName);
        if (field == null) {
          throw new IllegalArgumentException("Double field " + parameterName + " not found");
        }
        field.put(((VDouble) value).getValue());

      } else if (value instanceof VFloat) {

        PVFloat field = pvRequest.getFloatField(fieldName != null ? fieldName : parameterName);
        if (field == null) {
          throw new IllegalArgumentException("Float field " + parameterName + " not found");
        }
        field.put(((VFloat) value).getValue());

      } else if (value instanceof VFloatArray) {

        PVFloatArray field = (PVFloatArray) pvRequest.getScalarArrayField(
          fieldName != null ? fieldName : parameterName, ScalarType.pvFloat);
        if (field == null) {
          throw new IllegalArgumentException("FloatArray field " + parameterName + " not found");
        }

        VFloatArray vFloatArray = (VFloatArray) value;
        float[] floatArr = new float[vFloatArray.getData().size()];

        for (int i = 0; i < floatArr.length; i++) {
          floatArr[i] = vFloatArray.getData().getFloat(i);
        }

        field.put(0, floatArr.length, floatArr, 0);

      } else if (value instanceof VBoolean) {

        PVBoolean field = pvRequest.getBooleanField(fieldName != null ? fieldName : parameterName);
        if (field == null) {
          throw new IllegalArgumentException("Boolean field " + parameterName + " not found");
        }
        field.put(((VBoolean) value).getValue());

      } else if (value instanceof VInt) {

        PVInt field = pvRequest.getIntField(fieldName != null ? fieldName : parameterName);
        if (field == null) {
          throw new IllegalArgumentException("Int field " + parameterName + " not found");
        }
        field.put(((VInt) value).getValue());

      } else {
        throw new RuntimeException("pvAccess RPC Service mapping support for " + value.getClass().getSimpleName() + " not implemented");
      }
    }
    return pvRequest;
  }


  private VType createResult(PVStructure pvResult) {

    if (pvResult == null) {
      return null;
    }

    if (!isResultQuery()) {
      return null;
    }

    Class<?> resultType = getResultTypes().values().toArray(new Class<?>[getResultTypes().size()])[0];

    if (this.rpcServiceMethodDescription.isResultStandalone) {
      if (resultType.isAssignableFrom(VTable.class)) {

        if ("uri:ev4:nt/2012/pwd:NTNameValue".equals(pvResult.getStructure().getID())) {
          return new PVFieldNTNameValueToVTable(pvResult, false);
        }

        return new PVFieldToVTable(pvResult, false);
      } else if (resultType.isAssignableFrom(VImage.class)) {
        return new PVFieldToVImage(pvResult, false);
      }

      throw new IllegalArgumentException("Standalone result type " + resultType.getSimpleName() + " not supported in pvAccess RPC rpcservice");
    }

    //non standalone results

    String resultName = getResultDescriptions().keySet().toArray(new String[getResultDescriptions().size()])[0];
    String fieldName = this.rpcServiceMethodDescription.getFieldNames().get(resultName);

    if (resultType.isAssignableFrom(VDouble.class)) {
      return ValueFactory.newVDouble(pvResult.getDoubleField(fieldName != null ? fieldName : resultName).get());
    } else if (resultType.isAssignableFrom(VFloat.class)) {
      return ValueFactory.newVFloat(pvResult.getFloatField(fieldName != null ? fieldName : resultName).get(), ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
    } else if (resultType.isAssignableFrom(VFloatArray.class)) {
      return new PVFieldToVFloatArray(pvResult, fieldName != null ? fieldName : resultName, true);
    } else if (resultType.isAssignableFrom(VString.class)) {
      return ValueFactory.newVString(pvResult.getStringField(fieldName != null ? fieldName : resultName).get(), ValueFactory.alarmNone(), ValueFactory.timeNow());
    } else if (resultType.isAssignableFrom(VInt.class)) {
      return ValueFactory.newVInt(pvResult.getIntField(fieldName != null ? fieldName : resultName).get(), ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
    } else if (resultType.isAssignableFrom(VBoolean.class)) {
      return ValueFactory.newVBoolean(pvResult.getBooleanField(fieldName != null ? fieldName : resultName).get(), ValueFactory.alarmNone(), ValueFactory.timeNow());
    } else if (resultType.isAssignableFrom(VTable.class)) {
      return new PVFieldToVTable(pvResult,false);
    }

    throw new IllegalArgumentException("Result type " + resultType.getSimpleName() + " not supported in pvAccess RPC rpcservice");

  }
}
