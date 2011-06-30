
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
import org.csstudio.ams.dbAccess.TObject;

/**
	iFilterID			NUMBER(11),
	iGroupRef			NUMBER(11) default -1 NOT NULL,
	cName				VARCHAR2(128),
	cDefaultMessage		VARCHAR2(1024),
	PRIMARY KEY (iFilterID)
*/
@SuppressWarnings("hiding")
public class FilterTObject extends TObject implements ItemInterface {
	
    private static final long serialVersionUID = -1306344725040262239L;
	
	private int 	filterID;// PRIMARY KEY
	private int 	groupRef;
	private String 	name;
	private String 	defaultMessage;

	public FilterTObject() {
		this.filterID = -1;
		this.groupRef = -1;
	}
	
	public FilterTObject(int filterID, int groupRef, String name, String defaultMessage) {
		this.filterID = filterID;
		this.groupRef = groupRef;
		this.name = name;
		this.defaultMessage = defaultMessage;
	}
	
	public FilterKey getKey() {
		return new FilterKey(filterID, name,groupRef);
	}

	@Override
    public int getID() {
		return filterID;
	}

	////////// Getter- and Setter-Methods //////////
	
	public String getDefaultMessage() {
		return defaultMessage;
	}
	
	public void setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}

	public int getFilterID() {
		return filterID;
	}
	
	public void setFilterID(int filterID) {
		this.filterID = filterID;
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
	
	@Override
    public boolean equals(Object obj) {
		
	    if(!(obj instanceof FilterTObject))
			return false;
		
		FilterTObject compare = (FilterTObject)obj;
	
		if(compare.getFilterID() != getFilterID())
			return false;
		if(compare.getGroupRef() != getGroupRef())
			return false;
		if(!strEquals(compare.getName(), getName()))
			return false;
		if(!strEquals(compare.getDefaultMessage(), getDefaultMessage()))
			return false;

		return true;
	}
}
