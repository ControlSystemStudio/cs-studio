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

import org.csstudio.ams.dbAccess.TObject;

/**
	iFilterConditionRef		NUMBER(11) NOT NULL,
	cKeyValue				VARCHAR2(16),
	sOperator				NUMBER(6)
 */
public class FilterConditionArrayStringTObject extends TObject
{
	private static final long serialVersionUID = 2007841622152640220L;
	
	private int 	filterConditionRef;
	private String 	keyValue;
	private short 	operator;

	public FilterConditionArrayStringTObject()
	{
		this.filterConditionRef = -1;
	}
	
	public FilterConditionArrayStringTObject(int filterConditionRef, String keyValue, short operator)
	{
		this.filterConditionRef = filterConditionRef;
		this.keyValue = keyValue;
		this.operator = operator;
	}

	////////// Getter- and Setter-Methods //////////
	
	public int getFilterConditionRef() {
		return filterConditionRef;
	}

	public void setFilterConditionRef(int filterConditionRef) {
		this.filterConditionRef = filterConditionRef;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public Short getOperator() {
		return operator;
	}

	public void setOperator(Short operator) {
		this.operator = operator;
	}
	
	public boolean equals(Object obj)
	{
		if(!(obj instanceof FilterConditionArrayStringTObject))
			return false;
		
		FilterConditionArrayStringTObject compare = (FilterConditionArrayStringTObject)obj;
	
		if(compare.getFilterConditionRef() != getFilterConditionRef())
			return false;
		if(!strEquals(compare.getKeyValue(), getKeyValue()))
			return false;
		if(compare.getOperator() != getOperator())
			return false;
		return true;
	}
}
