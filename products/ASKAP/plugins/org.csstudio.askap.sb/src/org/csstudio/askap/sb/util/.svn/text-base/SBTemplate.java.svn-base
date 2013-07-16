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

import java.util.Map;

/**
 * @author wu049
 * @created Sep 2, 2010
 * 
 *
 */
public class SBTemplate {
	long id = 0;
	String name = null;
	int majorVersion = 0;
	int minorVersion = 0;
	
	String pythonScript = "";
	Map<String, String> parameterMap = null;
		
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public String getVersion() {
		return "" + majorVersion + "." + minorVersion;
	}
	
	/**
	 * @return the majorVersion
	 */
	public int getMajorVersion() {
		return majorVersion;
	}
	/**
	 * @param majorVersion the majorVersion to set
	 */
	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}
	/**
	 * @return the minorVersion
	 */
	public int getMinorVersion() {
		return minorVersion;
	}
	/**
	 * @param minorVersion the minorVersion to set
	 */
	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}
	
	public String getDisplayName() {
		String displayName = "";
		
		if (name != null)
			displayName = name + "@v" + majorVersion + "." + minorVersion;
		
		return displayName;
	}
	/**
	 * @return the pythonScript
	 */
	public String getPythonScript() {
		return pythonScript;
	}
	/**
	 * @param pythonScript the pythonScript to set
	 */
	public void setPythonScript(String pythonScript) {
		this.pythonScript = pythonScript;
	}
	/**
	 * @return the parameterMap
	 */
	public Map<String, String> getParameterMap() {
		return parameterMap;
	}
	/**
	 * @param parameterMap the parameterMap to set
	 */
	public void setParameterMap(Map<String, String> parameterMap) {
		this.parameterMap = parameterMap;
	}
}
