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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.platform.libs.dcf.directory.ContactElement;

/**
 * This class is to be instantiated for each action request
 * that is passed to the <sup>ActionExecutor</sup> class.
 * It provides a way of knowing if the action has recieved
 * a result, and if so, allows for a mean to access it.
 * 
 * @author avodovnik
 *
 */
public class ActionRequestMonitor {
	private final String _actionId;
	private final Object _parameter;
	private final ContactElement _target;
	private final Date _dateRecieved;
	private List<ActionResponseMonitor> _responses 
	= new CopyOnWriteArrayList<ActionResponseMonitor>();
	
	private List<IActionResponseReceived> _listeners = 
		new CopyOnWriteArrayList<IActionResponseReceived>();
	
	public ActionRequestMonitor(String actionId, 
			Object parameter,
			ContactElement target) {
		_actionId = actionId;
		_parameter = parameter;
		_target = target;
		_dateRecieved = new Date();
	}
	
	public String getActionId() {
		return _actionId;
	}
	
	public Object getParameter() {
		return _parameter;
	}
	
	public ContactElement getTarget() {
		return _target;
	}
	
	public Date getDate() {
		return _dateRecieved;
	}
	
	/**
	 * Called by the action executor when a response is recieved
	 * for this particular instance. Should <b>not</b> be called
	 * outside of this package.
	 * 
	 * @param response The response recieved.
	 * @param element 
	 */
	public void notifyResponseRecieved(Object response, ContactElement element) {
		this._responses.add(new ActionResponseMonitor(response, this, element));
		for(IActionResponseReceived listener : _listeners) {
			listener.notifyResponseReceived(response);
		}
	}
	
	public void addResponseRecievedListener(IActionResponseReceived listener) {
		_listeners.add(listener);
	}
	
	public void removeResponseRecievedListener(
			IActionResponseReceived listener) {
		_listeners.remove(listener);
	}
	
	public boolean hasFinished() {
		return (this._responses.size() > 0);
	}

	public Object[] getResponses() {
		return this._responses.toArray();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Request: " + this.getActionId() + " [" + this.getDate().toString() + "]";
	}
	
}
