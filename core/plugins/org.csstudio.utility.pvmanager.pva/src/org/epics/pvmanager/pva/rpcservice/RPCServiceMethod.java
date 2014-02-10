/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.rpcservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.epics.pvaccess.client.rpc.RPCClient;
import org.epics.pvaccess.server.rpc.RPCRequestException;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.FieldCreate;
import org.epics.pvdata.pv.PVBoolean;
import org.epics.pvdata.pv.PVByte;
import org.epics.pvdata.pv.PVByteArray;
import org.epics.pvdata.pv.PVDouble;
import org.epics.pvdata.pv.PVDoubleArray;
import org.epics.pvdata.pv.PVFloat;
import org.epics.pvdata.pv.PVFloatArray;
import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVShort;
import org.epics.pvdata.pv.PVShortArray;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Structure;
import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.pva.adapters.PVFieldNTMatrixToVDoubleArray;
import org.epics.pvmanager.pva.adapters.PVFieldNTNameValueToVTable;
import org.epics.pvmanager.pva.adapters.PVFieldToVByteArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVDoubleArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVFloatArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVImage;
import org.epics.pvmanager.pva.adapters.PVFieldToVIntArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVShortArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVStatistics;
import org.epics.pvmanager.pva.adapters.PVFieldToVStringArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVTable;
import org.epics.pvmanager.pva.rpcservice.rpcclient.PooledRPCClientFactory;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.vtype.VBoolean;
import org.epics.vtype.VByte;
import org.epics.vtype.VByteArray;
import org.epics.vtype.VDouble;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VFloat;
import org.epics.vtype.VFloatArray;
import org.epics.vtype.VImage;
import org.epics.vtype.VInt;
import org.epics.vtype.VIntArray;
import org.epics.vtype.VShort;
import org.epics.vtype.VShortArray;
import org.epics.vtype.VStatistics;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

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
    for (String parameterName : this.parameterNames.keySet()) {
      fieldList.add(convertToPvType(getArgumentTypes().get(parameterName)));
    }

    return fieldList.toArray(new Field[fieldList.size()]);
  }


  private Field convertToPvType(Class<?> argType) {

    if (argType == null) {
      throw new IllegalArgumentException("Type not set: null");
    }

    // TODO no unsigned and complex types
    
    if (argType.isAssignableFrom(VDouble.class)) {
      return fieldCreate.createScalar(ScalarType.pvDouble);
    } else if (argType.isAssignableFrom(VFloat.class)) {
      return fieldCreate.createScalar(ScalarType.pvFloat);
    } else if (argType.isAssignableFrom(VString.class)) {
      return fieldCreate.createScalar(ScalarType.pvString);
    } else if (argType.isAssignableFrom(VInt.class)) {
      return fieldCreate.createScalar(ScalarType.pvInt);
    } else if (argType.isAssignableFrom(VShort.class)) {
      return fieldCreate.createScalar(ScalarType.pvShort);
    } else if (argType.isAssignableFrom(VByte.class)) {
      return fieldCreate.createScalar(ScalarType.pvByte);
    } else if (argType.isAssignableFrom(VBoolean.class)) {
      return fieldCreate.createScalar(ScalarType.pvBoolean);
      
    } else if (argType.isAssignableFrom(VDoubleArray.class)) {
      return fieldCreate.createScalarArray(ScalarType.pvDouble);
    } else if (argType.isAssignableFrom(VFloatArray.class)) {
      return fieldCreate.createScalarArray(ScalarType.pvFloat);
    } else if (argType.isAssignableFrom(VStringArray.class)) {
      return fieldCreate.createScalarArray(ScalarType.pvString);
    } else if (argType.isAssignableFrom(VIntArray.class)) {
      return fieldCreate.createScalarArray(ScalarType.pvInt);
    } else if (argType.isAssignableFrom(VShortArray.class)) {
      return fieldCreate.createScalarArray(ScalarType.pvShort);
    } else if (argType.isAssignableFrom(VByteArray.class)) {
      return fieldCreate.createScalarArray(ScalarType.pvByte);
    }
    
    throw new IllegalArgumentException("Argument class " + argType.getSimpleName() + " not supported in pvAccess RPC Service");
  }

  /*
  private ExecutorService getExecutorService() {
    return this.rpcServiceMethodDescription.executorService;
  }
  */

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

      // TODO no unsigned types, and complex types (do we need to support them?)
      
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

      } else if (value instanceof VInt) {

          PVInt field = pvRequest.getIntField(fieldName != null ? fieldName : parameterName);
          if (field == null) {
            throw new IllegalArgumentException("Int field " + parameterName + " not found");
          }
          field.put(((VInt) value).getValue());

      } else if (value instanceof VShort) {

    	  PVShort field = pvRequest.getShortField(fieldName != null ? fieldName : parameterName);
          if (field == null) {
            throw new IllegalArgumentException("Short field " + parameterName + " not found");
          }
          field.put(((VShort) value).getValue());

      } else if (value instanceof VByte) {

    	  PVByte field = pvRequest.getByteField(fieldName != null ? fieldName : parameterName);
          if (field == null) {
            throw new IllegalArgumentException("Byte field " + parameterName + " not found");
          }
          field.put(((VByte) value).getValue());
          
      } else if (value instanceof VBoolean) {

        PVBoolean field = pvRequest.getBooleanField(fieldName != null ? fieldName : parameterName);
        if (field == null) {
          throw new IllegalArgumentException("Boolean field " + parameterName + " not found");
        }
        field.put(((VBoolean) value).getValue());

      } else if (value instanceof VStringArray) {

          PVStringArray field = (PVStringArray) pvRequest.getScalarArrayField(
            fieldName != null ? fieldName : parameterName, ScalarType.pvString);
          if (field == null) {
            throw new IllegalArgumentException("StringArray field " + parameterName + " not found");
          }

          VStringArray vStringArray = (VStringArray) value;
          String[] stringArr = new String[vStringArray.getData().size()];

          for (int i = 0; i < stringArr.length; i++) {
            stringArr[i] = vStringArray.getData().get(i);
          }

          field.put(0, stringArr.length, stringArr, 0);

      } else if (value instanceof VDoubleArray) {

          PVDoubleArray field = (PVDoubleArray) pvRequest.getScalarArrayField(
            fieldName != null ? fieldName : parameterName, ScalarType.pvDouble);
          if (field == null) {
            throw new IllegalArgumentException("DoubleArray field " + parameterName + " not found");
          }

          VDoubleArray vDoubleArray = (VDoubleArray) value;
          double[] doubleArr = new double[vDoubleArray.getData().size()];

          for (int i = 0; i < doubleArr.length; i++) {
            doubleArr[i] = vDoubleArray.getData().getDouble(i);
          }

          field.put(0, doubleArr.length, doubleArr, 0);
          
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
          
      } else if (value instanceof VIntArray) {

          PVIntArray field = (PVIntArray) pvRequest.getScalarArrayField(
            fieldName != null ? fieldName : parameterName, ScalarType.pvInt);
          if (field == null) {
            throw new IllegalArgumentException("IntArray field " + parameterName + " not found");
          }

          VIntArray vIntArray = (VIntArray) value;
          int[] intArr = new int[vIntArray.getData().size()];

          for (int i = 0; i < intArr.length; i++) {
            intArr[i] = vIntArray.getData().getInt(i);
          }

          field.put(0, intArr.length, intArr, 0);
          
      } else if (value instanceof VShortArray) {

          PVShortArray field = (PVShortArray) pvRequest.getScalarArrayField(
            fieldName != null ? fieldName : parameterName, ScalarType.pvShort);
          if (field == null) {
            throw new IllegalArgumentException("ShortArray field " + parameterName + " not found");
          }

          VShortArray vShortArray = (VShortArray) value;
          short[] shortArr = new short[vShortArray.getData().size()];

          for (int i = 0; i < shortArr.length; i++) {
            shortArr[i] = vShortArray.getData().getShort(i);
          }

          field.put(0, shortArr.length, shortArr, 0);
          
      } else if (value instanceof VByteArray) {

          PVByteArray field = (PVByteArray) pvRequest.getScalarArrayField(
            fieldName != null ? fieldName : parameterName, ScalarType.pvByte);
          if (field == null) {
            throw new IllegalArgumentException("ByteArray field " + parameterName + " not found");
          }

          VByteArray vByteArray = (VByteArray) value;
          byte[] byteArr = new byte[vByteArray.getData().size()];

          for (int i = 0; i < byteArr.length; i++) {
            byteArr[i] = vByteArray.getData().getByte(i);
          }

          field.put(0, byteArr.length, byteArr, 0);
          
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

        if ("uri:ev4:nt/2012/pwd:NTNameValue".equals(pvResult.getStructure().getID()))
          return new PVFieldNTNameValueToVTable(pvResult, false);
        else
        	return new PVFieldToVTable(pvResult, false);
        
      } else if (resultType.isAssignableFrom(VImage.class)) {
        return new PVFieldToVImage(pvResult, false);
      } else if (resultType.isAssignableFrom(VStatistics.class)) {
          return new PVFieldToVStatistics(pvResult, false);
      } else if (resultType.isAssignableFrom(VDoubleArray.class) &&
    		  "uri:ev4:nt/2012/pwd:NTMatrix".equals(pvResult.getStructure().getID())) {
          return new PVFieldNTMatrixToVDoubleArray(pvResult, false);
      }

      throw new IllegalArgumentException("Standalone result type " + resultType.getSimpleName() + " not supported in pvAccess RPC rpcservice");
    }

    // TODO unsigned types, complex types 

    String resultName = getResultDescriptions().keySet().toArray(new String[getResultDescriptions().size()])[0];
    String fieldName = this.rpcServiceMethodDescription.getFieldNames().get(resultName);

    if (resultType.isAssignableFrom(VDouble.class)) {
      return ValueFactory.newVDouble(pvResult.getDoubleField(fieldName != null ? fieldName : resultName).get(), ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
    } else if (resultType.isAssignableFrom(VFloat.class)) {
      return ValueFactory.newVFloat(pvResult.getFloatField(fieldName != null ? fieldName : resultName).get(), ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
    } else if (resultType.isAssignableFrom(VString.class)) {
      return ValueFactory.newVString(pvResult.getStringField(fieldName != null ? fieldName : resultName).get(), ValueFactory.alarmNone(), ValueFactory.timeNow());
    } else if (resultType.isAssignableFrom(VInt.class)) {
      return ValueFactory.newVInt(pvResult.getIntField(fieldName != null ? fieldName : resultName).get(), ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
    } else if (resultType.isAssignableFrom(VShort.class)) {
        return ValueFactory.newVShort(pvResult.getShortField(fieldName != null ? fieldName : resultName).get(), ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
    } else if (resultType.isAssignableFrom(VByte.class)) {
        return ValueFactory.newVByte(pvResult.getByteField(fieldName != null ? fieldName : resultName).get(), ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
    } else if (resultType.isAssignableFrom(VBoolean.class)) {
      return ValueFactory.newVBoolean(pvResult.getBooleanField(fieldName != null ? fieldName : resultName).get(), ValueFactory.alarmNone(), ValueFactory.timeNow());
    } else if (resultType.isAssignableFrom(VDoubleArray.class)) {
    	
		if ("uri:ev4:nt/2012/pwd:NTMatrix".equals(pvResult.getStructure().getID()))
	      return new PVFieldNTMatrixToVDoubleArray(pvResult, false);
	    else
          return new PVFieldToVDoubleArray(pvResult, fieldName != null ? fieldName : resultName, true);
		
    } else if (resultType.isAssignableFrom(VFloatArray.class)) {
        return new PVFieldToVFloatArray(pvResult, fieldName != null ? fieldName : resultName, true);
    } else if (resultType.isAssignableFrom(VIntArray.class)) {
        return new PVFieldToVIntArray(pvResult, fieldName != null ? fieldName : resultName, true);
    } else if (resultType.isAssignableFrom(VShortArray.class)) {
        return new PVFieldToVShortArray(pvResult, fieldName != null ? fieldName : resultName, true);
    } else if (resultType.isAssignableFrom(VByteArray.class)) {
        return new PVFieldToVByteArray(pvResult, fieldName != null ? fieldName : resultName, true);
    } else if (resultType.isAssignableFrom(VStringArray.class)) {
        return new PVFieldToVStringArray(pvResult, fieldName != null ? fieldName : resultName, true);
    } else if (resultType.isAssignableFrom(VTable.class)) {
        if ("uri:ev4:nt/2012/pwd:NTNameValue".equals(pvResult.getStructure().getID()))
            return new PVFieldNTNameValueToVTable(pvResult, false);
          else
          	return new PVFieldToVTable(pvResult, false);
    } else if (resultType.isAssignableFrom(VImage.class)) {
        return new PVFieldToVImage(pvResult,false);
    } else if (resultType.isAssignableFrom(VStatistics.class)) {
        return new PVFieldToVStatistics(pvResult,false);
    }
    
    throw new IllegalArgumentException("Result type " + resultType.getSimpleName() + " not supported in pvAccess RPC rpcservice");

  }
}
