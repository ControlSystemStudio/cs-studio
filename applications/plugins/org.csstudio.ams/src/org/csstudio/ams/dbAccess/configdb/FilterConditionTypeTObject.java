/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.ams.dbAccess.configdb;

import org.csstudio.ams.dbAccess.ItemInterface;

/**
	iFilterConditionTypeID	NUMBER(11),
	cName					VARCHAR2(128),
	cClass					VARCHAR2(256),
	cClassUI				VARCHAR2(256),
	PRIMARY KEY(iFilterConditionTypeID) 
*/
public class FilterConditionTypeTObject implements ItemInterface
{
	private int 	filterConditionTypeID;// PRIMARY KEY
	private String 	name;
	private String 	className;
	private String 	classNameUI;

	//theres no key class until now, so put special key handling to here
	public FilterConditionTypeTObject()
	{
		this.filterConditionTypeID = -1;
	}
	
	public FilterConditionTypeTObject(int filterConditionID, String name, String className, String classNameUI)
	{
		this.filterConditionTypeID = filterConditionID;
		this.name = name;
		this.className = className;
		this.classNameUI = classNameUI;
	}	
	
	public String toString()
	{
		return name;
	}
	
	public int hashCode()
	{
		return (filterConditionTypeID + " " + name).hashCode();
	}
	
	public boolean equals(Object obj)
	{
		if(obj instanceof FilterConditionTypeTObject)
			return ((FilterConditionTypeTObject)obj).filterConditionTypeID == filterConditionTypeID;
		return false;
	}

	public int getID()
	{
		return filterConditionTypeID;
	}

	////////// Getter- and Setter-Methods //////////
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassNameUI() {
		return classNameUI;
	}
	public void setClassNameUI(String classNameUI) {
		this.classNameUI = classNameUI;
	}

	public int getFilterConditionTypeID() {
		return filterConditionTypeID;
	}
	public void setFilterConditionTypeID(int filterConditionTypeID) {
		this.filterConditionTypeID = filterConditionTypeID;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
