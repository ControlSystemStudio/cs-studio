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
 package org.csstudio.platform.libs.dcf.actions.internal;

import org.csstudio.platform.libs.dcf.actions.IAction;
import org.csstudio.platform.libs.dcf.actions.SimpleActionRequest;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class SimpleActionProxy extends ActionProxyBase {

	private static final String ATT_CLASS = "class";
	private static final String ATT_ID = "id";
	private static final String ATT_NAME = "name";
	private static final String ATT_VISIBLE = "visible";
	
	private final IConfigurationElement _configElement;
	private IAction _action;
	private final String _id;
	private final String _name;
	private final String  _visible;
	
	
	public SimpleActionProxy(IConfigurationElement configElement) {
		this._configElement = configElement;
		getAttribute(configElement, ATT_CLASS, null);
		_id = getAttribute(configElement, ATT_ID, null);
		_name = getAttribute(configElement, ATT_NAME, _id);
		_visible = getAttribute(configElement, ATT_VISIBLE, "true");
	}

	public Object run(Object param) {
		return getAction().run(param);
	}
	
	public String getName() {
		return _name;
	}
	
	public String getId() {
		return _id;
	}

	private IAction getAction() {
		try {
			if(_action == null)
				_action = (IAction)this._configElement.createExecutableExtension(ATT_CLASS);
			
		} catch (CoreException e) {
			e.printStackTrace();
			_action = null;
		}
		
		return _action;
	}

	@Override
	public Class getType() {
		return SimpleActionRequest.class;
	}
	
	public boolean isVisible() {
		return !_visible.equals("false");
	}
}
