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

import java.util.Comparator;
import java.util.Date;
import java.util.Map;


/**
 * @author wu049
 * @created Sep 2, 2010
 *
 */
public class SchedulingBlock {
	
	String aliasName;
	Long id;
	SBState state;
	Map<String, String> parameterMap = null;
	
	Date lastExecutedDate = null;
	long lastExecutionDuration = 0;

	int majorVersion;
	String templateName = "";
	String executedVersion = "";
	
	Date errorTimeStamp = null;
	String errorMessage = "";
	
	public enum SBState {
	    DRAFT,	    
	    SUBMITTED,    
	    SCHEDULED,	    
	    EXECUTING,	    
	    POSTPROCESSING,	    
	    PENDINGTRANSFER,	    
	    COMPLETED,	    
	    ERRORED;
	}
	
	
	static public class SBComparator implements Comparator<SchedulingBlock> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(SchedulingBlock o1, SchedulingBlock o2) {
			return o1.aliasName.compareTo(o2.aliasName);
		}
		
	}

	/**
	 * @return the aliasName
	 */
	public String getAliasName() {
		return aliasName;
	}

	/**
	 * @param aliasName the aliasName to set
	 */
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the state
	 */
	public SBState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(SBState state) {
		this.state = state;
	}	
	
	public String getDisplayName() {
		if (aliasName!=null)
			return aliasName + " (" + state + ")";
		
		return "";	
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

	/**
	 * @return the lastExecutedDate
	 */
	public Date getLastExecutedDate() {
		return lastExecutedDate;
	}

	/**
	 * @param lastExecutedDate the lastExecutedDate to set
	 */
	public void setLastExecutedDate(Date lastExecutedDate) {
		this.lastExecutedDate = lastExecutedDate;
	}

	/**
	 * @return the lastExecutionDuration
	 */
	public long getLastExecutionDuration() {
		return lastExecutionDuration;
	}

	/**
	 * @param lastExecutionDuration the lastExecutionDuration to set
	 */
	public void setLastExecutionDuration(long lastExecutionDuration) {
		this.lastExecutionDuration = lastExecutionDuration;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * @return the errorTimeStamp
	 */
	public Date getErrorTimeStamp() {
		return errorTimeStamp;
	}

	/**
	 * @param errorTimeStamp the errorTimeStamp to set
	 */
	public void setErrorTimeStamp(Date errorTimeStamp) {
		this.errorTimeStamp = errorTimeStamp;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the executedVersion
	 */
	public String getExecutedVersion() {
		if (executedVersion==null || executedVersion.trim().length()==0)
			return "" + majorVersion;
		
		return executedVersion;
	}

	/**
	 * @param executedVersion the executedVersion to set
	 */
	public void setExecutedVersion(String executedVersion) {
		this.executedVersion = executedVersion;
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SchedulingBlock) {
			SchedulingBlock sb = (SchedulingBlock) obj;
			
			if (sb.id!=null && sb.id.equals(this.id))
				return true;
		}
		
		return super.equals(obj);
	}
}
