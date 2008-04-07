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

import java.io.Serializable;

import org.csstudio.ams.dbAccess.ItemInterface;
import org.csstudio.ams.dbAccess.TObject;

/**
	iFilterConditionID		NUMBER(11) NOT NULL,
	iGroupRef				NUMBER(11) default -1 NOT NULL,
	cName					VARCHAR2(128),
	cDesc					VARCHAR2(256),
	iFilterConditionTypeRef NUMBER(11),
	PRIMARY KEY(iFilterConditionID)
*/
public class FilterConditionTObject extends TObject implements ItemInterface,Serializable
{
	private static final long serialVersionUID = 6712717253871210117L;
	
	private int 	filterConditionID;// PRIMARY KEY
	private int 	groupRef;
	private String 	name;
	private String 	desc;
	private int 	filterConditionTypeRef;
	
	public FilterConditionTObject()
	{
		this.filterConditionID = -1;
		this.groupRef = -1;
	}
	
	public FilterConditionTObject(int filterConditionID, int groupRef, String name, String desc, int filterConditionTypeRef)
	{
		this.filterConditionID = filterConditionID;
		this.groupRef = groupRef;
		this.name = name;
		this.desc = desc;
		this.filterConditionTypeRef = filterConditionTypeRef;
	}
	
	public FilterConditionKey getKey()
	{
		return new FilterConditionKey(filterConditionID, name, groupRef);
	}

	public int getID()
	{
		return filterConditionID;
	}
	
	////////// Getter- and Setter-Methods //////////
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getFilterConditionID() {
		return filterConditionID;
	}
	public void setFilterConditionID(int filterConditionID) {
		this.filterConditionID = filterConditionID;
	}

	public int getFilterConditionTypeRef() {
		return filterConditionTypeRef;
	}
	public void setFilterConditionTypeRef(int filterConditionTypeRef) {
		this.filterConditionTypeRef = filterConditionTypeRef;
	}

	public int getGroupRef() {
		return groupRef;
	}

	public void setGroupRef(int groupRef) {
		this.groupRef = groupRef;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean equals(Object obj)
	{
		if(obj instanceof FilterConditionTObject)
		{		
			FilterConditionTObject compare = (FilterConditionTObject)obj;
		
			if(compare.getFilterConditionID() != getFilterConditionID())
				return false;
			if(compare.getGroupRef() != getGroupRef())
				return false;
			if(!strEquals(compare.getName(), getName()))
				return false;
			if(!strEquals(compare.getDesc(), getDesc()))
				return false;
			if(compare.getFilterConditionTypeRef() != getFilterConditionTypeRef())
				return false;
		}
		else if(obj instanceof FilterConditionKey)
		{
			return ((FilterConditionKey)obj).filterConditionID == filterConditionID;
		}
		else
			return false;
		return true;	
	}
}
