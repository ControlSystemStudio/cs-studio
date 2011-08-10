
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
	iFilterConditionRef	NUMBER(11) NOT NULL,
	cStartKeyValue		VARCHAR2(16),
	sStartOperator		NUMBER(6),
	cStartCompValue		VARCHAR2(128),
	cConfirmKeyValue	VARCHAR2(16),
	sConfirmOperator	NUMBER(6),
	cConfirmCompValue	VARCHAR2(128),
	sTimePeriod			NUMBER(6),
	sTimeBehavior		NUMBER(6)
 */
public class FilterConditionTimeBasedTObject extends TObject
{
	private static final long serialVersionUID = 1900126496926324967L;
	
	private int 	filterConditionRef;
	private String 	startKeyValue;
	private short 	startOperator;
	private String 	startCompValue;
	private String 	confirmKeyValue;
	private short 	confirmOperator;
	private String 	confirmCompValue;
	private short	timePeriod;
	private short   timeBehavior;

	public FilterConditionTimeBasedTObject()
	{
		this.filterConditionRef = -1;
	}
	
	public FilterConditionTimeBasedTObject(int filterConditionRef, String startKeyValue, short startOperator, String startCompValue, String confirmKeyValue, short confirmOperator, String confirmCompValue, short timePeriod, short timeBehavior) 
	{
		super();
		this.filterConditionRef = filterConditionRef;
		this.startKeyValue = startKeyValue;
		this.startOperator = startOperator;
		this.startCompValue = startCompValue;
		this.confirmKeyValue = confirmKeyValue;
		this.confirmOperator = confirmOperator;
		this.confirmCompValue = confirmCompValue;
		this.timePeriod = timePeriod;
		this.timeBehavior = timeBehavior;
	}

	////////// Getter- and Setter-Methods //////////
	

	public String getConfirmCompValue() {
		return confirmCompValue;
	}

	public void setConfirmCompValue(String confirmCompValue) {
		this.confirmCompValue = confirmCompValue;
	}

	public String getConfirmKeyValue() {
		return confirmKeyValue;
	}

	public void setConfirmKeyValue(String confirmKeyValue) {
		this.confirmKeyValue = confirmKeyValue;
	}

	public short getConfirmOperator() {
		return confirmOperator;
	}

	public void setConfirmOperator(short confirmOperator) {
		this.confirmOperator = confirmOperator;
	}

	public int getFilterConditionRef() {
		return filterConditionRef;
	}

	public void setFilterConditionRef(int filterConditionRef) {
		this.filterConditionRef = filterConditionRef;
	}

	public String getStartCompValue() {
		return startCompValue;
	}

	public void setStartCompValue(String startCompValue) {
		this.startCompValue = startCompValue;
	}

	public String getStartKeyValue() {
		return startKeyValue;
	}

	public void setStartKeyValue(String startKeyValue) {
		this.startKeyValue = startKeyValue;
	}

	public short getStartOperator() {
		return startOperator;
	}

	public void setStartOperator(short startOperator) {
		this.startOperator = startOperator;
	}

	public short getTimeBehavior() {
		return timeBehavior;
	}

	public void setTimeBehavior(short timeBehavior) {
		this.timeBehavior = timeBehavior;
	}

	public short getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(short timePeriod) {
		this.timePeriod = timePeriod;
	}

    @Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		
		if (!(obj instanceof FilterConditionTimeBasedTObject))
			return false;
		
		final FilterConditionTimeBasedTObject other = (FilterConditionTimeBasedTObject) obj;
		
		if (confirmCompValue == null) 
		{
			if (other.confirmCompValue != null)
				return false;
		} 
		else if (!confirmCompValue.equals(other.confirmCompValue))
			return false;
		if (confirmKeyValue == null) 
		{
			if (other.confirmKeyValue != null)
				return false;
		} 
		else if (!confirmKeyValue.equals(other.confirmKeyValue))
			return false;
		if (confirmOperator != other.confirmOperator)
			return false;
		if (filterConditionRef != other.filterConditionRef)
			return false;
		if (startCompValue == null) 
		{
			if (other.startCompValue != null)
				return false;
		} else if (!startCompValue.equals(other.startCompValue))
			return false;
		if (startKeyValue == null) 
		{
			if (other.startKeyValue != null)
				return false;
		} else if (!startKeyValue.equals(other.startKeyValue))
			return false;
		if (startOperator != other.startOperator)
			return false;
		if (timeBehavior != other.timeBehavior)
			return false;
		if (timePeriod != other.timePeriod)
			return false;
		return true;
	}


}
