/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.loc;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.csstudio.autocomplete.parser.ContentDescriptor;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class LocalContentDescriptor extends ContentDescriptor {

	private static Map<String, String> vTypes = new TreeMap<String, String>();
	static {
		vTypes.put("VString", "Creates a text PV");
		vTypes.put("VStringArray", "Creates a text array PV");
		vTypes.put("VDouble", "Creates a numeric PV");
		vTypes.put("VDoubleArray", "Creates a numeric array PV");
		vTypes.put("VTable", "Creates a table PV");
		vTypes = Collections.unmodifiableMap(vTypes);
	}

	private String vType;
	private String pvName;
	private List<String> initialValues = new LinkedList<String>();
	private List<Class<?>> initialValuesTypes = new LinkedList<Class<?>>();

	private boolean completingVType = false;
	private boolean completingInitialValue = false;
	private boolean complete = false;

	public String getInitialValueTooltip() {
		if(vType != null) {
			switch (vType) {
			case "VString": return "\"string\"";
			case "VStringArray": return "\"string\",...";
			case "VDouble": return "number";
			case "VDoubleArray": return "number,...";
			case "VTable": break;
			default: return "initialValue";
			}
		}
		if (initialValuesTypes.isEmpty())
			return "initialValue";
		if (initialValuesTypes.get(0).equals(String.class))
			return initialValuesTypes.size() == 1 ? "\"string\"" : "\"string\",...";
		if (initialValuesTypes.get(0).equals(Double.class))
			return initialValuesTypes.size() == 1 ? "number" : "number,...";
		return "initialValue";
	}
	
	public boolean checkParameters() {
		if (initialValues.size() == 0 || vType == null)
			return true;
		switch (vType) {
		case "VString":
			return initialValues.size() == 1
					&& initialValuesTypes.get(0).equals(String.class);
		case "VStringArray":
			return initialValues.size() >= 1
					&& initialValuesTypes.get(0).equals(String.class);
		case "VDouble":
			return initialValues.size() == 1
					&& initialValuesTypes.get(0).equals(Double.class);
		case "VDoubleArray":
			return initialValues.size() >= 1
					&& initialValuesTypes.get(0).equals(Double.class);
		case "VTable":
			return true;
		default:
			return false;
		}
	}

	public static Collection<String> listVTypes() {
		return vTypes.keySet();
	}

	public static String getVTypeDescription(String vType) {
		return vTypes.get(vType);
	}

	public void addInitialvalue(String value, Class<?> type) {
		initialValues.add(value);
		initialValuesTypes.add(type);
	}

	public String getPvName() {
		return pvName;
	}

	public void setPvName(String pvName) {
		this.pvName = pvName;
	}

	public String getvType() {
		return vType;
	}

	public void setvType(String vType) {
		this.vType = vType;
	}

	public List<String> getInitialValues() {
		return initialValues;
	}

	public List<Class<?>> getInitialValuesTypes() {
		return initialValuesTypes;
	}

	public boolean isCompletingVType() {
		return completingVType;
	}

	public void setCompletingVType(boolean completingVType) {
		this.completingVType = completingVType;
	}

	public boolean isCompletingInitialValue() {
		return completingInitialValue;
	}

	public void setCompletingInitialValue(boolean completingInitialValue) {
		this.completingInitialValue = completingInitialValue;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	@Override
	public String toString() {
		return "LocalContentDescriptor [vType=" + vType + ", pvName=" + pvName
				+ ", initialValues=" + initialValues + ", initialValuesTypes="
				+ initialValuesTypes + ", completingVType=" + completingVType
				+ ", completingInitialValue=" + completingInitialValue
				+ ", complete=" + complete + ", toString()=" + super.toString()
				+ "]";
	}

}
