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

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.libs.dcf.actions.IAction;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

public class ActionEnumerator {
	private static Map<String, ActionProxyBase> cachedActions;

	/**
	 * Retruns the actions by parsing the extension point.
	 * 
	 * @return An array of actions.
	 */
	public static ActionProxyBase[] getActions() {
		// see if the items are cached
		if (cachedActions == null) {
			cachedActions = new HashMap<String, ActionProxyBase>();
			// ok, get the extension
			IExtension[] extensions = Platform.getExtensionRegistry()
					.getExtensionPoint("org.csstudio.platform.libs.dcf.action")
					.getExtensions();
	
			// define an array to hold the configuration elements
			IConfigurationElement[] configElements;
			// define a variable to hold the parsed actions
			ActionProxyBase action;
	
			for (IExtension extension : extensions) {
				// load the config elements
				configElements = extension.getConfigurationElements();
	
				for (IConfigurationElement configElement : configElements) {
					// get the action proxy
					action = parseAction(configElement);
					if (action != null)
						// add the action to the cache
						cachedActions.put(action.getId(), action);
				}
			}
		}

//		 returned the items already cached
		return cachedActions.values().toArray(
				new ActionProxyBase[cachedActions.size()]);
	}

	private static final String TAG_SIMPLEACTION = "simpleAction";
	private static final String TAG_ENUMACTION = "enumAction";
	private static final String TAG_PARAMACTION = "parametrizedAction";
	private static final String TAG_FILEACTION = "fileTransferAction";
	private static final String TAG_DYNAMICACTION = "dynamicValuesAction";
	
	private static ActionProxyBase parseAction(IConfigurationElement configElement) {
		// check which config element did we get
		// and create the corresponding proxy
		if(TAG_ENUMACTION.equals(configElement.getName())) {
			return new EnumeratedActionProxy(configElement);
		} else if(TAG_SIMPLEACTION.equals(configElement.getName())) {
			return new SimpleActionProxy(configElement);
		} else if(TAG_PARAMACTION.equals(configElement.getName())) {
			return new ParametrizedActionProxy(configElement);
		} else if(TAG_DYNAMICACTION.equals(configElement.getName())) {
			return new DynamicParamValueActionProxy(configElement);
		}else if(TAG_FILEACTION.equals(configElement.getName())) {
			return new FileTransferActionProxy(configElement);
		}
		return null;
	}
	
	public static IAction getAction(String id) {
		getActions(); // this ensures the actions are cached
		return cachedActions.get(id);
	}
}
