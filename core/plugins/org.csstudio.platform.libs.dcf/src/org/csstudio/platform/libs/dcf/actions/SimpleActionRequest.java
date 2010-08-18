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
 package org.csstudio.platform.libs.dcf.actions;

import java.io.Serializable;

import org.csstudio.platform.libs.dcf.actions.internal.ActionEnumerator;

/**
 * 
 * @author avodovnik
 *
 */
public class SimpleActionRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7719962935719573761L;
	
	private String _actionId;
	private Object _parameter;
	
	public SimpleActionRequest(String actionId, Object parameter) {
		_actionId = actionId;
		_parameter = parameter;
	}
	
	public String getActionId() {
		return _actionId;
	}
	
	public Object getParameter() {
		return _parameter;
	}
	
	public void setParameter(Object param){
		_parameter = param;
	}
	
	/**
	 * Calls the underlying action implementation
	 * retrieved from the enumeration and returns
	 * the direct return value from the action.
	 * @return
	 */
	public Object execute() {
		IAction action = ActionEnumerator.getAction(this.getActionId());
		if(action == null)
			return new Exception("No action");
		return action.run(this.getParameter());
	}
	
}
