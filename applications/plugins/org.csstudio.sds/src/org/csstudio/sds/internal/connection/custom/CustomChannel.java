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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.impl.SimplePropertyImpl;

/**
 * A simple double property for simulations that will be run without using the
 * DAL library.
 * 
 * @author Sven Wende, Alexander Will
 * @version $Revision$
 */
public final class CustomChannel {

	/**
	 * Listeners that are attached to this property.
	 */
	private List<DynamicValueListener> _listeners;

	/**
	 * The property value.
	 */
	private Object _value;

	/**
	 * The "virtual" name of an associated channel.
	 */
	private IProcessVariableAddress _processVariable;

	/**
	 * Standard constructor.
	 * 
	 * @param processVariable
	 *            A channel name.
	 */
	public CustomChannel(final IProcessVariableAddress processVariable) {
		_processVariable = processVariable;
		_listeners = new ArrayList<DynamicValueListener>();
	}

	/**
	 * Add a property change listener.
	 * 
	 * @param listener
	 *            A property change listener.
	 */
	public void addDynamicValueListener(final DynamicValueListener listener) {
		synchronized (_listeners) {
			_listeners.add(listener);
			// if there is already a value, initially fire the listener.
			if (_value != null) {
				DynamicValueEvent event = createDynamicValueEvent(_value);
				listener.valueChanged(event);
			}
		}
	}

	/**
	 * Remove a property change listener.
	 * 
	 * @param listener
	 *            A property change listener.
	 */
	public void removeDynamicValueListener(final DynamicValueListener listener) {
		synchronized (_listeners) {
			_listeners.remove(listener);
		}
	}

	/**
	 * Notify all registered property change listeners.
	 * 
	 * @param newValue
	 *            The new value of the property.
	 * @param propertyName
	 *            ID of the property that has changed.
	 * @param oldValue
	 *            The old value of the property.
	 */
	@SuppressWarnings("unchecked")
	protected synchronized void firePropertyChangeEvent(
			final String propertyName, final Object oldValue,
			final Object newValue) {

		List<DynamicValueListener> copy = new ArrayList<DynamicValueListener>();
		copy.addAll(_listeners);

		DynamicValueEvent event = createDynamicValueEvent(_value);

		for (DynamicValueListener listener : copy) {
			listener.valueChanged(event);
		}
	}

	/**
	 * Creates a {@link DynamicValueEvent}.
	 * 
	 * @param value
	 *            the new value
	 * @return a {@link DynamicValueEvent}
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	private DynamicValueEvent createDynamicValueEvent(final Object value) {
		SimplePropertyImpl sp = new SimplePropertyImpl(Object.class, "name");
		DynamicValueEvent event = new DynamicValueEvent(sp, sp, _value, null,
				new Timestamp(), "");

		return event;
	}

	/**
	 * Set the property value.
	 * 
	 * @param value
	 *            The property value.
	 */
	public synchronized void setValue(final Object value) {
		Object oldValue = _value;
		_value = value;
		firePropertyChangeEvent(_processVariable.getProperty(), oldValue,
				_value);
	}

	/**
	 * Definition of listeners that react on changes of model properties.
	 * 
	 * @author Sven Wende, Alexander Will
	 * @version $Revision$
	 * 
	 */
	public interface ISimpleChangeListener {
		/**
		 * React on the change of a model property.
		 * 
		 * @param event
		 *            The change event.
		 */
		void simpleChangeOccured(PropertyChangeEvent event);
	}
}
