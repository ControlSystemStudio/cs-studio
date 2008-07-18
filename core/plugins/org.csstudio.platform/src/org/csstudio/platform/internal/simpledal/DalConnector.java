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
 package org.csstudio.platform.internal.simpledal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.csstudio.platform.internal.simpledal.converters.ConverterUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ValueType;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.LinkListener;

/**
 * DAL Connectors are connected to the control system via the DAL API.
 * 
 * All events received from DAL are forwarded to
 * {@link IProcessVariableValueListener}큦 which abstract from DAL.
 * 
 * For convinience the {@link IProcessVariableValueListener}큦 are only weakly
 * referenced. The connector tracks for {@link IProcessVariableValueListener}큦
 * that have been garbage collected and removes those references from its
 * internal list. This way {@link IProcessVariableValueListener}큦 must not be
 * disposed explicitly.
 * 
 * @author Sven Wende
 * 
 */
@SuppressWarnings("unchecked")
class DalConnector extends AbstractConnector implements DynamicValueListener,
		LinkListener, ResponseListener {
	/**
	 * The DAL property, this connector is connected to.
	 */
	private DynamicValueProperty _dalProperty;

	/**
	 * Constructor.
	 */
	public DalConnector(IProcessVariableAddress pvAddress, ValueType valueType) {
		super(pvAddress, valueType);
	}

	/**
	 * Sets the DAL property, this connector is connected to.
	 * 
	 * @param dalProperty
	 *            the DAL property
	 */
	public void setDalProperty(DynamicValueProperty dalProperty) {
		_dalProperty = dalProperty;
	}

	/**
	 * Returns the DAL property, this connector is connected to.
	 * 
	 * @return
	 */
	public DynamicValueProperty getDalProperty() {
		return _dalProperty;
	}

	/**
	 * {@inheritDoc}
	 */
	public void conditionChange(DynamicValueEvent event) {
		//FIXME: forward condition changes
	}

	/**
	 * {@inheritDoc}
	 */
	public void errorResponse(DynamicValueEvent event) {
		//FIXME: forward condition changes
	}

	/**
	 * {@inheritDoc}
	 */
	public void timelagStarts(DynamicValueEvent event) {
		//FIXME: forward condition changes
	}

	/**
	 * {@inheritDoc}
	 */
	public void timelagStops(DynamicValueEvent event) {
		//FIXME: forward condition changes
	}

	/**
	 * {@inheritDoc}
	 */
	public void timeoutStarts(DynamicValueEvent event) {
		//FIXME: forward condition changes
	}

	/**
	 * {@inheritDoc}
	 */
	public void timeoutStops(DynamicValueEvent event) {
		//FIXME: forward condition changes
	}

	/**
	 * {@inheritDoc}
	 */
	public void valueChanged(final DynamicValueEvent event) {
		doForwardValue(event.getValue(), event.getTimestamp());
	}

	/**
	 * {@inheritDoc}
	 */
	public void valueUpdated(final DynamicValueEvent event) {
		doForwardValue(event.getValue(), event.getTimestamp());
	}

	/**
	 * {@inheritDoc}
	 */
	public void connected(final ConnectionEvent e) {
		doForwardConnectionStateChange(ConnectionState.translate(e.getState()));
	}

	/**
	 * {@inheritDoc}
	 */
	public void connectionFailed(ConnectionEvent e) {
		doForwardConnectionStateChange(ConnectionState.translate(e.getState()));
	}

	/**
	 * {@inheritDoc}
	 */
	public void connectionLost(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroyed(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public void disconnected(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public void resumed(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public void suspended(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseError(ResponseEvent event) {
		Exception e = event.getResponse().getError();
		doForwardError(e != null ? e.getMessage() : "Unknown error!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseReceived(ResponseEvent event) {
		IProcessVariableAddress pv = getProcessVariableAddress();
		String idTag = event.getResponse().getIdTag().toString();
		
		// Important: We need to check, that we forward only the right events because all Characteristics  are queried using the same DAL Property instance
		boolean forward = false;

		if (pv.isCharacteristic()) {
			forward = pv.getCharacteristic().equals(idTag);
		} else {
			forward = "value".equals(idTag);
		}

		if (forward) {
			//jhatje 18.0.7.2008, add timestamp of the event
			doForwardValue(event.getResponse().getValue(), event.getResponse().getTimestamp());
		}
	}

	private void forwardConnectionEvent(ConnectionEvent e) {
		doForwardConnectionStateChange(ConnectionState.translate(e.getState()));
	}
}
