/*
 * Copyright (c) 2009 CSIRO Australia Telescope National Facility (ATNF) Commonwealth
 * Scientific and Industrial Research Organisation (CSIRO) PO Box 76, Epping NSW 1710,
 * Australia atnf-enquiries@csiro.au
 *
 * This file is part of the ASKAP software distribution.
 *
 * The ASKAP software distribution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 */

package org.csstudio.askap.sb.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.sb.Preferences;

import askap.util.ParameterSet;

/**
 * @author wu049
 * @created Jun 17, 2010
 *
 */
public class ParamDataModel {
	
	private static final Logger logger = Logger.getLogger(ParamDataModel.class.getName());
	
	static final Map<String, String> TYPE_DEFINITION = new HashMap<String, String>();
	private List<SBParameter> paramList = new LinkedList<SBParameter>();
	
	static {
		// need to convert parameter types to Java types:
		// Bool, Int, Int16, Int32, Int64, Float, Double, String, Time
		TYPE_DEFINITION.put("Bool", "java.lang.Boolean");
		TYPE_DEFINITION.put("Int", "java.lang.Integer");
		TYPE_DEFINITION.put("Int16", "java.lang.Short");
		TYPE_DEFINITION.put("Int32", "java.lang.Integer");
		TYPE_DEFINITION.put("Int64", "java.lang.Long");
		TYPE_DEFINITION.put("Float", "java.lang.Float");
		TYPE_DEFINITION.put("Double", "java.lang.Double");
		TYPE_DEFINITION.put("String", "java.lang.String");
		TYPE_DEFINITION.put("Time", "java.lang.Double");
		
		// for vectors, we need to call vector method and passing the simple type to it
		TYPE_DEFINITION.put("BoolVector", "java.lang.Boolean");
		TYPE_DEFINITION.put("IntVector", "java.lang.Integer");
		TYPE_DEFINITION.put("Int16Vector", "java.lang.Short");
		TYPE_DEFINITION.put("Int32Vector", "java.lang.Integer");
		TYPE_DEFINITION.put("Int64Vector", "java.lang.Long");
		TYPE_DEFINITION.put("FloatVector", "java.lang.Float");
		TYPE_DEFINITION.put("DoubleVector", "java.lang.Double");
		TYPE_DEFINITION.put("StringVector", "java.lang.String");
		TYPE_DEFINITION.put("TimeVector", "java.lang.Double");
		
	}
	
	/**
	 * @param prop
	 */
	public static void loadParamModel(Map<String, String> paramMap, ParamDataModel userConfigModel, ParamDataModel fixedConfigModel) throws Exception {
		ParameterSet paramSet = new ParameterSet(paramMap);
		loadParam(paramSet, userConfigModel, fixedConfigModel);
	}
	
	public static void loadParamModel(String fileName, ParamDataModel userConfigModel, ParamDataModel fixedConfigModel) throws Exception {
		ParameterSet paramSet = new ParameterSet(fileName);
		loadParam(paramSet, userConfigModel, fixedConfigModel);
	}
	
	private static void loadParam(ParameterSet paramSet, ParamDataModel userConfigModel, ParamDataModel fixedConfigModel) throws Exception {
		List<SBParameter> params = constructParam(paramSet);
		
		for (SBParameter p : params) {
			if (p.isUserParameter())
				userConfigModel.add(p);
			else
				fixedConfigModel.add(p);
		}		
	}

	/**
	 * 
	 */
	public ParamDataModel() {
	}

	/**
	 * Each ObsParameter should have the following elements in LOFAR
	 * ParameterSet format:
	 * 
	 * Type (Mandatory)
	 * The standard Parameter Set types (including vectors of them) except
	 * unsigned values are supported. The names of these types correspond to the
	 * equivalent ParameterSet get method, e.g. ParameterSet::getIntVector
	 * corresponds to type = IntVector.
	 * These types are: Bool, Int, Int16, Int32, Int64, Float, Double, String, Time
	 * 
	 * Default (Mandatory) 
	 * The default to use if no userparameter is specified.
	 * 
	 * UserParameter (Mandatory) A boolean type, indicating this key is a
	 * tunable which the user can tune. If false, then the user does not have
	 * the ability to speficy a value, and the default value is used when
	 * building the ObsParameters.
	 * 
	 * Label (Mandatory) A short description, will be used by the operators
	 * displays so forms can be dynamically built to prompt the user to input
	 * the "User Parameters".
	 * 
	 * Description (Mandatory) A long description, will be used by the operators
	 * displays so forms can be dynamically built to prompt the user to input
	 * the "User Parameters".
	 * 
	 * Min (Optional) The minimum acceptable value. For a vector type this is
	 * per element.
	 * 
	 * Max (Optional) The maximum acceptable value. For a vector type this is
	 * per element.
	 * 
	 * Size (Optional) Fix the allowed number of elements in the vector to the
	 * given size.
	 * 
	 * Step (Optional) Optional.The optional step size for a value in a
	 * specified min/max range.
	 * 
	 * Enum (Optional) The optional list of fixed possible values for the Vector
	 * type (it emulates an enumeration).
	 * 
	 * Unit (Optional) Useful in general, but necessary for the operator
	 * displays so forms can be dynamically built to prompt the user to input
	 * the "User Parameters". Should use SI units where possible.
	 * 
	 * @param prop
	 * @return an array of {@link SBParameter} constructed from the given prop
	 * 
	 */
	static List<SBParameter> constructParam(ParameterSet paramSet) throws Exception {
		StringBuffer errorString = new StringBuffer();
		
		List<SBParameter> paramList = new ArrayList<SBParameter>();
		if (paramSet==null)
			return paramList;
		
		while (paramSet.keys().hasMoreElements()) {
			
			String key = (String) paramSet.keys().nextElement();
			
			if (!key.endsWith(".type") && !key.endsWith(".min") && !key.endsWith(".max")
				&& !key.endsWith(".default") && !key.endsWith(".unit") && !key.endsWith(".size")
				&& !key.endsWith(".userparameter") && !key.endsWith(".description")
				&& !key.endsWith(".step") && !key.endsWith(".enum") && !key.endsWith(".label")) {
				
				logger.log(Level.INFO, "Skipping key<" + key + ">");
				
				paramSet.remove(key);
				continue;
			}

			int index = key.lastIndexOf("." + Preferences.CALIBRATION_SOURCE + ".");
			if (index >0) {
				String param = key.substring(0, index);
				
				SBParameter sb = createSBParameter(paramSet, param+"." + Preferences.CALIBRATION_SOURCE_NAME + ".", errorString);
				if (sb != null)
					paramList.add(sb);		
				
				sb = createSBParameter(paramSet, param+"." + Preferences.CALIBRATION_SOURCE_FRAME + ".", errorString);
				if (sb != null)
					paramList.add(sb);		

				sb = createSBParameter(paramSet, param+"." + Preferences.CALIBRATION_SOURCE_C1 + ".", errorString);
				if (sb != null)
					paramList.add(sb);		
				
				sb = createSBParameter(paramSet, param+"." + Preferences.CALIBRATION_SOURCE_C2 + ".", errorString);
				if (sb != null)
					paramList.add(sb);
			} else {
				index = key.lastIndexOf(".");
				String param = key.substring(0, index+1);
				
				SBParameter sb = createSBParameter(paramSet, param, errorString);
				if (sb != null)
					paramList.add(sb);
			}
		}
		
		if (errorString.length()>0) {
			paramList.clear();
			throw new Exception(errorString.toString());
		}
		
		return paramList;
	}
	
	/**
	 * creates the SBParameter from given paramSet for the give param
	 * and removes all the parameter pairs for this param from paramSet
	 * 
	 * @param paramSet
	 * @param param
	 * @return
	 */
	private static SBParameter createSBParameter(ParameterSet paramSet, String param, StringBuffer errorString) {
		String type = (String) paramSet.remove(param + "type");
		if (type==null)
			type = "";
		
		String unit = (String) paramSet.remove(param + "unit");
		unit = (unit==null?"":unit);
		
		String description = (String) paramSet.remove(param + "description");
		String name = (String) paramSet.remove(param + "label");
		String defaultStr = (String) paramSet.remove(param + "default");
		String userparam = (String) paramSet.remove(param + "userparameter");
		String minStr = (String) paramSet.remove(param + "min");
		String maxStr = (String) paramSet.remove(param + "max");
		String sizeStr = (String) paramSet.remove(param + "size");
		String stepStr = (String) paramSet.remove(param + "step");
		String enumStr = (String) paramSet.remove(param + "enum");
		
		try {
			boolean isUserParam = false;
			if (userparam!=null)
				isUserParam = ((Boolean) ParameterSet.getTypedValue(userparam, "java.lang.Boolean")).booleanValue();
			
			Number min = null;
			if (minStr!=null && minStr.trim().length()>0)
				min = (Number) getSimpleTypeValue(minStr, type);
			
			Number max = null;
			if (maxStr!=null && maxStr.trim().length()>0)
				max = (Number) getSimpleTypeValue(maxStr, type);
			
			int size = 0;
			if (sizeStr!=null && sizeStr.trim().length()>0) {
				Integer s = (Integer) ParameterSet.getTypedValue(sizeStr, "java.lang.Integer");
				if (s != null)
					size = s.intValue();
			}
			
			int step = 1;
			if (stepStr!=null && stepStr.trim().length()>0) {
				Integer s = (Integer) ParameterSet.getTypedValue(stepStr, "java.lang.Integer");
				if (s != null)
					step = s.intValue();
			}
			
			SBParameter sb = new SBParameter(param.substring(0, param
					.length() - 1), name, description, type, unit, min,
					max, size, step, enumStr, defaultStr, isUserParam);
			
			return sb;
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not create SBParameter for " + name, e);
			errorString.append(System.getProperty("line.separator") 
							+ "Could not create SBParameter for " + name + ": " + e.getMessage());
		}
			
		return null;
	}

	/**
	 * @param param
	 */
	private void add(SBParameter param) {
		paramList.add(param);
	}

	public int getSize() {
		return paramList.size();
	}
	
	public List<SBParameter> getParamters() {
		return paramList;
	}

	/**
	 * 
	 */
	public void clear() {
		paramList.clear();
	}

	/**
	 * @param out
	 */
	public void print(PrintStream out) {
		for (SBParameter p : paramList) {
			String param = p.getParam();
			out.println(param + "=" + p.getStrValue());
		}
	}
	
	public Map<String, String> getParamValues() {
		Map<String, String> paramValues = new HashMap<String, String>();
		for (SBParameter p : paramList) {
			String param = p.getParam();
			paramValues.put(param, p.getStrValue());
		}
		
		return paramValues;
	}

	public void loadValue(Map<String, String> paramMap) throws Exception {
		ParameterSet paramSet = new ParameterSet(paramMap);
		loadValue(paramSet);		
	}
	/**
	 * @param paramSet
	 */
	public void loadValue(String fileName) throws Exception {
		ParameterSet paramSet = new ParameterSet(fileName);
		loadValue(paramSet);
	}
	
	private void loadValue(ParameterSet paramSet) throws Exception {
		for (SBParameter p : paramList) {
			String param = p.getParam();
			String val = paramSet.getProperty(param);
			p.setValue(val);
		}		
	}

	/**
	 * @param value
	 * @param type
	 * @return
	 */
	public static Object getTypedValue(String value, String type) {		
		if (type!=null && type.endsWith("Vector")) {
			String temp = value;
			if (value==null || value.trim().length()==0)
				temp = "[]";
			
			return ParameterSet.getVector(temp, TYPE_DEFINITION.get(type));
		}
					
		return ParameterSet.getTypedValue(value, TYPE_DEFINITION.get(type));
	}

	
	private static Object getSimpleTypeValue(String value, String type) {
		String simpleType = type;
		
		if (type==null)
			simpleType = "";
		
		return ParameterSet.getTypedValue(value, TYPE_DEFINITION.get(simpleType));
	}
}
