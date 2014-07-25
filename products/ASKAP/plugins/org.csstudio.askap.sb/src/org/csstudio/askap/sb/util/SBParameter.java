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

import java.util.logging.Logger;

/**
 * @author wu049
 * @created Jun 17, 2010
 *
 */
public class SBParameter {
	private static final Logger logger = Logger.getLogger(SBParameter.class.getName());
	
	private String name;
	private String description;
	private String type;
	private String unit;
		
	private boolean isUserParam = false;
	private Object value;
	private String strValue;
		
	private int size;
	private int step;
	
	private Number min;
	private Number max;
	
	private Object[] enumSet = null;

	private String param;

	private String enumStr;
	
	public SBParameter(String param, String name, String description, String type, String unit, 
			Number min, Number max, int size, int step, String enumStr, String strValue, boolean isUserParam) throws Exception {
		this.param = param;
		
		if (name==null || name.length()==0)
			this.name = param;
		else
			this.name = name;
		
		
		this.description = description;
		this.unit = unit;
		this.type = type;
		
		this.min = min;
		this.max = max;
		
		this.enumStr = enumStr;
		
		Object[] enumSet = null;
		if (enumStr!=null && enumStr.trim().length()>0) {
			if (type.indexOf("Vector")>0)
				enumSet = (Object[]) ParamDataModel.getTypedValue(enumStr, type);
			else
				enumSet = (Object[]) ParamDataModel.getTypedValue(enumStr, type+"Vector");
		}
		
		this.enumSet = enumSet;
		this.size = size;
		this.step = step;
		
		this.isUserParam = Boolean.valueOf(isUserParam);
		
		setValue(strValue);		
	}
	

	/**
	 * value is validated
	 * 
	 * @param o
	 * @throws Exception
	 */
	public void setValue(String strValue) throws Exception {
		Object o = ParamDataModel.getTypedValue(strValue, type);
		if (isValid(o)) {
			value = o;
			
			if (strValue==null)
				this.strValue = "";
			else
				this.strValue = strValue;
		}
	}
	
	private boolean isValid(Object obj) throws Exception {
		if (obj == null)
			throw new Exception("value is null");
		
		// check array first
		// check size then each individual element
		if (obj instanceof Object[]) {
			if (size>0) {
				if (((Object[]) obj).length!=size)
					throw new Exception("array size should be " + size);
			}
			for (Object o : (Object[]) obj) {						
				if (!isValid(o))
					throw new Exception("invalid array value ");
			}
			return true;
		}
		
		// check simple values
		// check again min and max values
		if (min!=null || max!=null) {
			if (obj instanceof Number) {
				double val = ((Number) obj).doubleValue();
				
				if (min!=null) {
					double l = ((Number) min).doubleValue();
					if (val<l)
						throw new Exception("value " + val + " should be >=" + l);
				}
				
				if (max!=null) {
					double u = ((Number) max).doubleValue();
					if (val>u)
						throw new Exception("value " + val + " should be <=" + u);
				}
			}
		}
		
		// then check enum : make sure the value is part of the enum
		if (enumSet!=null) {
			for (Object o : enumSet) {
				// found a match
				if (o.equals(obj))
					return true;
			}
			
			// got to here means we did not find match in enum
			throw new Exception("" + obj + " is not in given enum");
		}
		
		return true;
	}


	public Object getValue() {
		return value;
	}
	
	public String getStrValue() {
		return strValue;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean isUserParameter() {
		return isUserParam;
	}
	
	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the min
	 */
	public Number getMin() {
		return min;
	}

	/**
	 * @return the max
	 */
	public Number getMax() {
		return max;
	}


	/**
	 * @return the param
	 */
	public String getParam() {
		return param;
	}


	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	
	public String getEnumStr() {
		return enumStr;
	}


	/**
	 * @return
	 */
	public int getStep() {
		return step;
	}
}
