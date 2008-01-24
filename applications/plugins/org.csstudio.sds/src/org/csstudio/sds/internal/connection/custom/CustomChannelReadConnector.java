/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.sds.internal.connection.custom;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.internal.connection.Connector;
import org.epics.css.dal.context.ConnectionState;

/**
 * Implementation of a read connector that deals with {@link CustomChannel}
 * created by {@link SingleThreadChannelPool} or {@link MultiThreadChannelPool}.
 * 
 * @author swende
 * 
 */
public final class CustomChannelReadConnector extends Connector {

	/**
	 * The current channel.
	 */
	private CustomChannel _channel;

	/**
	 * Constructor.
	 * 
	 * @param processVariable
	 *            the process variable
	 */
	public CustomChannelReadConnector(final IProcessVariableAddress processVariable) {
		super(processVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doConnect() {
		_channel = MultiThreadChannelPool.getInstance().getChannel(
				getProcessVariable(), 10);
		_channel.addDynamicValueListener(getDynamicValueListener());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doDisconnect() {
		if (_channel != null) {
			_channel.removeDynamicValueListener(getDynamicValueListener());
			_channel = null;
			MultiThreadChannelPool.getInstance().destroyChannel(
					getProcessVariable());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doProcessManualValueChange(final Object newValue) {
		_channel.setValue(newValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleConnectionStateTransition(ConnectionState oldState,
			ConnectionState newState) {

	}
}
