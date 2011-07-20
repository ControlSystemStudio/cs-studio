
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
import org.csstudio.ams.filter.FilterConditionProcessVariable;

/**
 * Configuration set of a PV based filter.
 * 
 * @see FilterConditionProcessVariable
 */
public class FilterConditionProcessVariableTObject extends TObject {

	/**
	 * auto generated serial version id.
	 */
	private static final long serialVersionUID = 3310772683687316820L;

	/**
	 * Filter this condition is assigned to.
	 */
	private int filterConditionRef;

	/**
	 * The DAL-channel to specified process variable.
	 */
	private String processVariableChannelName;

	/**
	 * The suggested type of the channel value.
	 */
	private FilterConditionProcessVariable.SuggestedProcessVariableType suggestedType;

	/**
	 * the id for the chosen operation.
	 */
	private FilterConditionProcessVariable.Operator operator;

	/**
	 * the value to compare against.
	 */
	private Object compValue;

	/**
	 * Creates a new instance with id -1 and all values set to null.
	 */
	public FilterConditionProcessVariableTObject() {
		this.filterConditionRef = -1;
	}

	/**
	 * Creates a new instance with given configuration.
	 * 
	 * @param filterConditionRef ID of the filter condition, should be greater -1.
	 * @param processVariableChannelName
	 * @param operator
	 * @param suggestedType
	 * @param compValue
	 */
	public FilterConditionProcessVariableTObject(
			final int filterConditionRef,
			final String processVariableChannelName,
			final FilterConditionProcessVariable.Operator operator,
			final FilterConditionProcessVariable.SuggestedProcessVariableType suggestedType,
			final Object compValue) {
		this.filterConditionRef = filterConditionRef;
		this.suggestedType = suggestedType;
		this.operator = operator;
		this.compValue = compValue;
		this.processVariableChannelName = processVariableChannelName;
	}

	public int getFilterConditionRef() {
		return filterConditionRef;
	}

	public void setFilterConditionRef(int filterConditionRef) {
		this.filterConditionRef = filterConditionRef;
	}

	public String getProcessVariableChannelName() {
		return processVariableChannelName;
	}

	public void setProcessVariableChannelName(String processVariableChannelName) {
		this.processVariableChannelName = processVariableChannelName;
	}

	public FilterConditionProcessVariable.SuggestedProcessVariableType getSuggestedType() {
		return suggestedType;
	}

	public void setSuggestedType(
			FilterConditionProcessVariable.SuggestedProcessVariableType suggestedType) {
		this.suggestedType = suggestedType;
	}

	public FilterConditionProcessVariable.Operator getOperator() {
		return operator;
	}

	public void setOperator(FilterConditionProcessVariable.Operator operator) {
		this.operator = operator;
	}

	public Object getCompValue() {
		return compValue;
	}

	public void setCompValue(Object compValue) {
		this.compValue = compValue;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.processVariableChannelName == null) ? 0 : this.processVariableChannelName.hashCode());
		result = prime * result + this.filterConditionRef;
		result = prime
				* result
				+ ((this.compValue == null) ? 0 : this.compValue.hashCode());
		result = prime * result + ((this.operator == null) ? 0 : this.operator.asDatabaseId());
		result = prime * result + ((this.suggestedType == null) ? 0 : this.suggestedType.asDatabaseId());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} 
		if (obj == null) {
			return false;
		}
		if (obj instanceof FilterConditionProcessVariableTObject) {
			final FilterConditionProcessVariableTObject other = (FilterConditionProcessVariableTObject) obj;
			if (this.getFilterConditionRef()!=other.getFilterConditionRef()) {
				return false;
			}
			if (!this.getCompValue().equals(other.getCompValue())) {
				return false;
			}
			if (!this.getOperator().equals(other.getOperator())) {
				return false;
			}
			if (!this.getProcessVariableChannelName().equals(other.getProcessVariableChannelName())) {
				return false;
			}
			if (!this.getSuggestedType().equals(other.getSuggestedType())) {
				return false;
			}
		}
		return true;
	}
}
