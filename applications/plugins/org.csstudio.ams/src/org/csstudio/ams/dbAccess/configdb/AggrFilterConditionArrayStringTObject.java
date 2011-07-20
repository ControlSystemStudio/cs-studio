
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
import java.util.List;

@SuppressWarnings("hiding")
public class AggrFilterConditionArrayStringTObject implements Serializable {
	
    private static final long serialVersionUID = -6244399728102486674L;
	
	private FilterConditionArrayStringTObject arrayString;
	private List<FilterConditionArrayStringValuesTObject> arrayStringValues;

	public AggrFilterConditionArrayStringTObject(FilterConditionArrayStringTObject arrayString,
	                    List<FilterConditionArrayStringValuesTObject> arrayStringValues) {
		
	    this.arrayString = arrayString;
		this.arrayStringValues = arrayStringValues;
	}
	
	////////// Getter- and Setter-Methods //////////

	public FilterConditionArrayStringTObject getArrayString() {
		return arrayString;
	}

	public void setArrayString(FilterConditionArrayStringTObject arrayString) {
		this.arrayString = arrayString;
	}

	public List<FilterConditionArrayStringValuesTObject> getArrayStringValues() {
		return arrayStringValues;
	}

	public void setArrayStringValues(
			List<FilterConditionArrayStringValuesTObject> arrayStringValues) {
		this.arrayStringValues = arrayStringValues;
	}
	
	public boolean isEquals(Object obj) {
		
	    if(!(obj instanceof AggrFilterConditionArrayStringTObject))
			return false;
		
		AggrFilterConditionArrayStringTObject compare = (AggrFilterConditionArrayStringTObject)obj;
		
		if(compare.getArrayString() != null && !compare.getArrayString().equals(getArrayString()))
			return false;
		
		if(compare.getArrayStringValues() == null && getArrayStringValues() == null)
			return true;
		
		if(compare.getArrayStringValues() != null && getArrayStringValues() != null)
		{
			if(compare.getArrayStringValues().size() != getArrayStringValues().size())
				return false;
			
			for(int i = 0; i < compare.getArrayStringValues().size(); i++)
			{
				if(!compare.getArrayStringValues().get(i).equals(getArrayStringValues().get(i)))
						return false;
			}
		}
		else
			return false;

		return true;
	}
}
