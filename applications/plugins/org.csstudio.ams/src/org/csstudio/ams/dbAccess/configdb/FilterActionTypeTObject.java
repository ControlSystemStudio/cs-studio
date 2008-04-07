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
	iFilterActionTypeID		NUMBER(11) NOT NULL,
	cName					VARCHAR2(128),
	iTopicRef				NUMBER(11),
	PRIMARY KEY(iFilterActionTypeID)
 */
public class FilterActionTypeTObject implements ItemInterface
{
	private int 	filterActionTypeID;// PRIMARY KEY
	private String 	name;
	private int 	topicRef;
	
	public FilterActionTypeTObject()
	{
		this.filterActionTypeID = -1;
	}
	
	public FilterActionTypeTObject(int filterActionTypeID, String name, int topicRef)
	{
		this.filterActionTypeID = filterActionTypeID;
		this.name = name;
		this.topicRef = topicRef;
	}

	public int getID()
	{
		return filterActionTypeID;
	}

	////////// Getter- and Setter-Methods //////////

	public int getFilterActionTypeID() {
		return filterActionTypeID;
	}

	public void setFilterActionTypeID(int filterActionTypeID) {
		this.filterActionTypeID = filterActionTypeID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTopicRef() {
		return topicRef;
	}

	public void setTopicRef(int topicRef) {
		this.topicRef = topicRef;
	}
}
