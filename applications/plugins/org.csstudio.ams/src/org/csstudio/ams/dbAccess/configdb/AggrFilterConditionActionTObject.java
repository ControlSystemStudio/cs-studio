
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
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("hiding")
public class AggrFilterConditionActionTObject implements Serializable {
	
    private static final long serialVersionUID = 5290953631399270451L;
	
	private FilterTObject filter = new FilterTObject();
	private List<FilterConditionKey> filterConditions = new ArrayList<FilterConditionKey>();
	private List<FilterActionTObject> filterActions = new ArrayList<FilterActionTObject>();

	
	public FilterKey getKey() {
		return new FilterKey(filter.getFilterID(), filter.getName(), filter.getGroupRef());
	}

	/**
	 * @return the filter
	 */
	public FilterTObject getFilter() {
		return filter;
	}


	/**
	 * @param filter the filter to set
	 */
	public void setFilter(FilterTObject filter) {
		this.filter = filter;
	}


	/**
	 * @return the filterActions
	 */
	public List<FilterActionTObject> getFilterActions() {
		return filterActions;
	}


	/**
	 * @param filterActions the filterActions to set
	 */
	public void setFilterActions(List<FilterActionTObject> filterActions) {
		this.filterActions = filterActions;
	}


	/**
	 * @return the filterConditions
	 */
	public List<FilterConditionKey> getFilterConditions() {
		return filterConditions;
	}


	/**
	 * @param filterConditions the filterConditions to set
	 */
	public void setFilterConditions(List<FilterConditionKey> filterConditions) {
		this.filterConditions = filterConditions;
	}
	
	public boolean isEquals(Object obj)
	{
		if(!(obj instanceof AggrFilterConditionActionTObject))
			return false;
		
		AggrFilterConditionActionTObject compare = (AggrFilterConditionActionTObject)obj;
		
		if(compare.getFilter() != null && !compare.getFilter().equals(getFilter()))
			return false;
		
		if(compare.getFilterConditions() == null && getFilterConditions() == null)
			;		
		else if(compare.getFilterConditions() != null && getFilterConditions() != null)
		{
			if(compare.getFilterConditions().size() != getFilterConditions().size())
				return false;
			
			for(int i = 0; i < compare.getFilterConditions().size(); i++)
			{
				if(!compare.getFilterConditions().get(i).equals(getFilterConditions().get(i)))
						return false;
			}
		}
		else
			return false;
		
		if(compare.getFilterActions() == null && getFilterActions() == null)
			;			
		else if(compare.getFilterActions() != null && getFilterActions() != null)
		{
			if(compare.getFilterActions().size() != getFilterActions().size())
				return false;
			
			for(int i = 0; i < compare.getFilterActions().size(); i++)
			{
				if(!compare.getFilterActions().get(i).equals(getFilterActions().get(i)))
						return false;
			}
		}
		else
			return false;

		return true;
	}
}
