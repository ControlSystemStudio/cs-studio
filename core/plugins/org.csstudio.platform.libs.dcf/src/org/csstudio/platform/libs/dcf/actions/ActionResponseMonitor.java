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

import java.util.Date;

import org.csstudio.platform.libs.dcf.directory.ContactElement;

/**
 * Used to keep track of responses for monitoring objects.
 * @author avodovnik
 *
 */
public class ActionResponseMonitor {
	private final Object _response;
	private final Date _date;
	private final ActionRequestMonitor _parent;
	private final ContactElement _element;
	private String _actionName;
	public ActionResponseMonitor(Object response, ActionRequestMonitor parent, ContactElement element) {
		_response = response;
		_date = new Date();
		_parent = parent;
		_element = element;
	}
	
	public ActionResponseMonitor(Object response, ActionRequestMonitor parent, 
			ContactElement element, String actionName) {
		this(response, parent, element);
		_actionName = actionName;
	}
	
	public Object getResponse() {
		return _response;
	}
	
	public Date getDate() {
		return _date;
	}
	
	public ContactElement getOrigin() {
		return _element;
	}
	
	public ActionRequestMonitor getParent() {
		return _parent;
	}
	
	public String getActionName() {
		return _actionName;
	}
}
