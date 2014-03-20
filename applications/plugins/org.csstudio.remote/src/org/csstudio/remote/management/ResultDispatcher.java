/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.remote.management;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * <p>
 * Dispatches the results returned by management commands to receivers that can
 * process them.
 * </p>
 * 
 * <p>
 * When a result is dispatched, the dispatcher first tries to dispatch it to one
 * of the preset receivers. Thus, the preset receivers can be used by
 * administration tools to ensure that certain kinds of results (such as error
 * messages) are always dispatched to a known receiver provided by the
 * administration tool. If none of the preset receivers can handle the result,
 * the dispatcher will then look for a suitable receiver that was provided as an
 * extension; and finally, if none of the extensions can handle the result, will
 * dispatch it to a default receiver.
 * </p>
 * 
 * @author Joerg Rathlev
 */
public class ResultDispatcher {

	private static final String EXTENSION_POINT_ID = 
		"org.csstudio.remote.managementCommandResultReceivers";
	
	private final Map<String, IResultReceiver> _presetReceivers;
	private final Map<String, IResultReceiver> _extensionReceivers;
	private final IResultReceiver _defaultReceiver;

	/**
	 * Creates a new result dispatcher.
	 * 
	 * @param defaultReceiver
	 *            the default receiver. Results for which no preset of extension
	 *            receiver is found will be dispatched to the default receiver.
	 */
	public ResultDispatcher(IResultReceiver defaultReceiver) {
		if (defaultReceiver == null) {
			throw new NullPointerException("defaultReceiver must not be null");
		}
		
		_presetReceivers = new HashMap<String, IResultReceiver>();
		_extensionReceivers = new HashMap<String, IResultReceiver>();
		_defaultReceiver = defaultReceiver;
		readExtensionPoint();
	}

	/**
	 * Adds a preset receiver to this dispatcher.
	 * 
	 * @param resultType
	 *            the type of results that this dispatcher will dispatch to the
	 *            receiver.
	 * @param receiver
	 *            the receiver.
	 */
	public void addPresetReceiver(String resultType, IResultReceiver receiver) {
		if (resultType == null || receiver == null) {
			throw new NullPointerException("resultType and receiver must not be null");
		}
		
		_presetReceivers.put(resultType, receiver);
	}

	/**
	 * Dispatches a command result to a receiver.
	 * 
	 * @param result
	 *            a command result.
	 */
	public void dispatch(CommandResult result) {
		IResultReceiver receiver = findReceiver(result.getType());
		receiver.processResult(result);
	}

	/**
	 * Returns the receiver for the specified result type.
	 * 
	 * @param resultType
	 *            the result type.
	 * @return the receiver.
	 */
	private IResultReceiver findReceiver(String resultType) {
		IResultReceiver receiver;
		receiver = _presetReceivers.get(resultType);
		if (receiver == null) {
			receiver = _extensionReceivers.get(resultType);
			if (receiver == null) {
				receiver = _defaultReceiver;
			}
		}
		return receiver;
	}

	/**
	 * Reads the receivers registered via the
	 * <code>managementCommandResultReceivers</code> extension point and adds
	 * them to the extension receivers.
	 */
	private void readExtensionPoint() {
		IExtension[] extensions = Platform.getExtensionRegistry()
				.getExtensionPoint(EXTENSION_POINT_ID).getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] configElements =
				extension.getConfigurationElements();
			for (IConfigurationElement configElement : configElements) {
				if ("receiver".equals(configElement.getName())) {
					readReceiverContribution(configElement);
				}
			}
		}
	}

	/**
	 * Reads a single receiver configuration element.
	 * 
	 * @param configElement
	 *            the configuration element.
	 */
	private void readReceiverContribution(IConfigurationElement configElement) {
		try {
			String type = configElement.getAttribute("resultType");
			IResultReceiver receiver =
				(IResultReceiver) configElement.createExecutableExtension("class");
			_extensionReceivers.put(type, receiver);
		} catch (CoreException e) {
			// TODO: error handling
		}
	}

}
