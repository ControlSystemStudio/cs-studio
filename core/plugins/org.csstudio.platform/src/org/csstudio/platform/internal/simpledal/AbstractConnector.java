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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.csstudio.platform.internal.simpledal.converters.ConverterUtil;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IConnectorStatistic;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ValueType;
import org.csstudio.platform.util.PerformanceUtil;
import org.eclipse.core.runtime.Platform;
import org.epics.css.dal.Timestamp;

/**
 * Base class for connectors.
 * 
 * For convinience the {@link IProcessVariableValueListener}�s are only weakly
 * referenced. The connector tracks for {@link IProcessVariableValueListener}�s
 * that have been garbage collected and removes those references from its
 * internal list. This way {@link IProcessVariableValueListener}�s must not be
 * disposed explicitly.
 * 
 * @author Sven Wende
 * 
 */
@SuppressWarnings("unchecked")
abstract class AbstractConnector implements IConnectorStatistic,
		IProcessVariableAdressProvider, IProcessVariable {
	
	class ListenerReference {
		
		public WeakReference<IProcessVariableValueListener> listener;
		public String characteristic=null;
		
		public ListenerReference(String characteristic, IProcessVariableValueListener<?> listener) {
			this.characteristic=characteristic;
			this.listener=new WeakReference<IProcessVariableValueListener>(listener);
		}
		
		public boolean isCharacteristic() {
			return characteristic!=null;
		}
		
		public IProcessVariableValueListener<?> getListener() {
			return listener.get();
		}
	}
	
	
	private Object _latestValue;

	private ConnectionState _latestConnectionState = ConnectionState.INITIAL;

	/**
	 * The process variable pointer for the channel this connector is connected
	 * to.
	 */
	private IProcessVariableAddress _processVariableAddress;

	/**
	 * A list of value listeners to which control system events are forwarded.
	 */
	protected List<ListenerReference> _weakListenerReferences;

	private String _latestError;

	private ValueType _valueType;

	/**
	 * Constructor.
	 */
	public AbstractConnector(IProcessVariableAddress pvAddress,
			ValueType valueType) {
		assert pvAddress != null;
		assert valueType != null;
		_processVariableAddress = pvAddress;
		_valueType = valueType;
		_weakListenerReferences = new ArrayList<ListenerReference>();
		PerformanceUtil.getInstance().constructorCalled(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getListenerCount() {
		return _weakListenerReferences.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public ConnectionState getLatestConnectionState() {
		return _latestConnectionState;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getLatestValue() {
		return _latestValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLatestError() {
		return _latestError;
	}

	/**
	 * Forwards the specified connection state.
	 * 
	 * @param connectionState
	 *            the connection state
	 */
	public void forwardConnectionState(ConnectionState connectionState) {
		doForwardConnectionStateChange(connectionState);
	}

	/**
	 * Forwards the specified value.
	 * 
	 * @param value
	 *            the value
	 */
	public void forwardValue(Object value) {
		doForwardValue(value);
	}

	/**
	 * Forwards the specified value.
	 * 
	 * @param value
	 *            the value
	 */
	public void forwardError(String error) {
		doForwardError(error);
		_latestError = error;
	}

	/**
	 * Adds a value listener.
	 * 
	 * @param listener
	 *            a value listener
	 */
	public void addProcessVariableValueListener(String charateristic, 
			IProcessVariableValueListener listener) {

		synchronized (_weakListenerReferences) {
			_weakListenerReferences
					.add(new ListenerReference(charateristic,listener));
		}

		// send initial connection state,
		if (_latestConnectionState != null) {
			listener.connectionStateChanged(_latestConnectionState);
		}

		// send initial value
		if (_latestValue != null && charateristic==null) {
			listener.valueChanged(_latestValue, null);
		}

		// FIXME: Was machen wir mit dem "latestError" ?? Ebenfalls initial
		// weiterleiten?
		if (_latestError != null) {
			listener.errorOccured(_latestError);
		}
	}

	/**
	 * Removes a value listener
	 * 
	 * @param listener
	 *            the value listener
	 */
	public boolean removeProcessVariableValueListener(
			IProcessVariableValueListener listener) {
		synchronized (_weakListenerReferences) {
			ListenerReference toRemove = null;
			for (ListenerReference ref : _weakListenerReferences) {
				IProcessVariableValueListener lr= ref.getListener(); 
				if (lr!=null) {
					toRemove = ref;
					break;
				}
			}

			if (toRemove != null) {
				_weakListenerReferences.remove(toRemove);
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines, whether this connector can get disposed. This is, when no
	 * value listeners for the connector live in the JVM anymore.
	 * 
	 * @return true, if this connector can be disposed, false otherwise
	 */
	public boolean isDisposable() {
		// perform a cleanup first
		cleanupWeakReferences();

		// this connector can be disposed it there are not weak references left
		return _weakListenerReferences.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public IProcessVariableAddress getProcessVariableAddress() {
		return _processVariableAddress;
	}

	/**
	 * {@inheritDoc}
	 */
	public ValueType getValueType() {
		return _valueType;
	}

	/**
	 * Forward the specified connection event to the value listeners.
	 * 
	 * @param event
	 *            the DAL connection event
	 */
	protected void doForwardConnectionStateChange(
			final ConnectionState connectionState) {
		if (connectionState != null) {
			// remember the latest state
			_latestConnectionState = connectionState;

			execute(new IInternalRunnable() {
				public void doRun(ListenerReference listener) {
					IProcessVariableValueListener l= listener.getListener();
					if (l!=null) {
						l.connectionStateChanged(connectionState);
					}
				}
			});
		}
	}

	/**
	 * Forward the current value.
	 * @param event
	 *            the DAL connection event
	 */
	protected void doForwardValue(final Object value) {
		doForwardValue(value, new Timestamp());
	}

	/**
	 * Forward the current value with its timestamp.
	 * (jhatje 18.07.2008, add timestamp)
	 * 
	 * @param timestamp 
	 * 			  the Timestamp of the latest event	
	 * @param event
	 *            the DAL connection event
	 */
	protected void doForwardValue(final Object value, final Timestamp timestamp) {
		if (value != null) {
			// memorize the latest value
			_latestValue = value;

			//System.out.println("UPDATE "+getName()+" "+value);
			execute(new IInternalRunnable() {
				public void doRun(ListenerReference listener) {
					if (!listener.isCharacteristic()) {
						IProcessVariableValueListener l= listener.getListener();
						if (l!=null) {
							l.valueChanged(ConverterUtil.convert(value,
									_valueType), timestamp);
						}
					}
				}
			});
		}
	}

	/**
	 * Forward the current characteristic value with its timestamp.
	 * 
	 */
	protected void doForwardValue(final Object value, final Timestamp timestamp, final String characteristic) {
		if (value != null && characteristic!=null) {
			execute(new IInternalRunnable() {
				public void doRun(ListenerReference listener) {
					if (characteristic.equals(listener.characteristic)) {
						IProcessVariableValueListener l= listener.getListener();
						if (l!=null) {
							l.valueChanged(value, timestamp);
						}
					}
				}
			});
		}
	}

	/**
	 * Forward the current value.
	 * 
	 * @param event
	 *            the DAL connection event
	 */
	protected void doForwardError(final String error) {
		execute(new IInternalRunnable() {
			public void doRun(ListenerReference listener) {
				IProcessVariableValueListener l= listener.getListener();
				if (l!=null) {
					l.errorOccured(error);
				}
			}
		});
	}

	/**
	 * Executes the specified runnable for all existing value listeners.
	 * 
	 * Only for valid listeners that still live in the JVM the hook method
	 * {@link IInternalRunnable#doRun(IProcessVariableValueListener)} is called.
	 * 
	 * @param runnable
	 *            the runnable
	 */
	private void execute(IInternalRunnable runnable) {
		synchronized (_weakListenerReferences) {
			Iterator<ListenerReference> it = _weakListenerReferences
					.iterator();

			while (it.hasNext()) {
				ListenerReference wr = it.next();

				IProcessVariableValueListener listener = wr.getListener();

				if (listener != null) {
					runnable.doRun(wr);
				}
			}
		}
	}

	/**
	 * Removes weak references for value listeners that have been garbage
	 * collected.
	 */
	private void cleanupWeakReferences() {
		synchronized (_weakListenerReferences) {
			List<ListenerReference> deletionCandidates = new ArrayList<ListenerReference>();
			Iterator<ListenerReference> it = _weakListenerReferences
					.iterator();

			while (it.hasNext()) {
				ListenerReference ref = it.next();

				if (ref.getListener() == null) {
					deletionCandidates.add(ref);
				}
			}

			for (ListenerReference wr : deletionCandidates) {
				boolean removed = _weakListenerReferences.remove(wr);
			}
		}
	}

	/**
	 * Runnable which is used to forward events to process variable value
	 * listeners.
	 * 
	 * @author Sven Wende
	 * 
	 */
	interface IInternalRunnable {
		/**
		 * Hook which is called only for valid value listeners, which still live
		 * in the JVM and have not already been garbage collected.
		 * 
		 * @param valueListeners
		 *            a value listener instance (we ensure, that this is not
		 *            null)
		 */
		void doRun(ListenerReference valueListeners);
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void finalize() throws Throwable {
		PerformanceUtil.getInstance().finalizedCalled(this);
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return _processVariableAddress.getProperty();
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public List<IProcessVariableAddress> getProcessVariableAdresses() {
		return Collections.singletonList(_processVariableAddress);
	}

	/**
	 * {@inheritDoc}
	 */
	public IProcessVariableAddress getPVAdress() {
		return _processVariableAddress;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return _processVariableAddress.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTypeId() {
		return IProcessVariable.TYPE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(Class adapterType) {
		return Platform.getAdapterManager().getAdapter(this, adapterType);
	}

}
