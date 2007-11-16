package org.csstudio.platform.internal.simpledal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
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
class DalConnector implements DynamicValueListener, LinkListener {
	/**
	 * The process variable pointer for the channel, this connector is connected
	 * to.
	 */
	private IProcessVariableAddress _processVariableAddress;

	/**
	 * The DAL property, this connector is connected to.
	 */
	private DynamicValueProperty _dalProperty;

	/**
	 * A list of value listeners to which control system events are forwarded.
	 */
	private List<WeakReference<IProcessVariableValueListener>> _weakListenerReferences;

	/**
	 * Constructor.
	 */
	public DalConnector(IProcessVariableAddress pvAddress) {
		assert pvAddress != null;
		_processVariableAddress = pvAddress;
		_weakListenerReferences = new ArrayList<WeakReference<IProcessVariableValueListener>>();
	}

	/**
	 * Adds a value listener.
	 * 
	 * @param listener
	 *            a value listener
	 */
	public void addProcessVariableValueListener(
			IProcessVariableValueListener listener) {

		_weakListenerReferences
				.add(new WeakReference<IProcessVariableValueListener>(listener));

		// send initial connection state
		org.epics.css.dal.context.ConnectionState connectionState = _dalProperty
				.getConnectionState();
		if (connectionState != null) {
			listener.connectionStateChanged(ConnectionState
					.translate(connectionState));
		}

		// send initial value
		Object latestReceivedValue = _dalProperty.getLatestReceivedValue();
		if (latestReceivedValue != null) {
			listener.valueChanged(latestReceivedValue);
		}
	}

	/**
	 * Removes a value listener
	 * 
	 * @param listener
	 *            the value listener
	 */
	public void removeProcessVariableValueListener(
			IProcessVariableValueListener listener) {
		synchronized (_weakListenerReferences) {
			WeakReference<IProcessVariableValueListener> toRemove = null;
			for (WeakReference<IProcessVariableValueListener> ref : _weakListenerReferences) {
				if (ref.get() == listener) {
					toRemove = ref;
				}
			}

			if (toRemove != null) {
				_weakListenerReferences.remove(toRemove);
			}
		}
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
	 * Returns the process variable address.
	 * 
	 * @return the process variable address
	 */
	public IProcessVariableAddress getProcessVariableAddress() {
		return _processVariableAddress;
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
	}

	/**
	 * {@inheritDoc}
	 */
	public void errorResponse(DynamicValueEvent event) {

	}

	/**
	 * {@inheritDoc}
	 */
	public void timelagStarts(DynamicValueEvent event) {

	}

	/**
	 * {@inheritDoc}
	 */
	public void timelagStops(DynamicValueEvent event) {

	}

	/**
	 * {@inheritDoc}
	 */
	public void timeoutStarts(DynamicValueEvent event) {

	}

	/**
	 * {@inheritDoc}
	 */
	public void timeoutStops(DynamicValueEvent event) {

	}

	/**
	 * {@inheritDoc}
	 */
	public void valueChanged(final DynamicValueEvent event) {
		execute(new IRunnable() {
			public void doRun(IProcessVariableValueListener simpleDalListener) {
				simpleDalListener.valueChanged(event.getValue());
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public void valueUpdated(final DynamicValueEvent event) {
		execute(new IRunnable() {
			public void doRun(IProcessVariableValueListener simpleDalListener) {
				simpleDalListener.valueChanged(event.getValue());
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public void connected(final ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public void connectionFailed(ConnectionEvent e) {
		forwardConnectionEvent(e);
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
	 * Forward the specified connection event to the value listeners.
	 * 
	 * @param event
	 *            the DAL connection event
	 */
	private void forwardConnectionEvent(final ConnectionEvent event) {
		execute(new IRunnable() {
			public void doRun(IProcessVariableValueListener simpleDalListener) {
				org.epics.css.dal.context.ConnectionState state = event
						.getState();

				if (state != null) {
					simpleDalListener.connectionStateChanged(ConnectionState
							.translate(state));
				}
			}
		});
	}

	/**
	 * Executes the specified runnable for all existing value listeners.
	 * 
	 * Only for valid listeners that still live in the JVM, the hook method
	 * {@link IRunnable#doRun(IProcessVariableValueListener)} is called.
	 * 
	 * @param runnable
	 *            the runnable
	 */
	private void execute(IRunnable runnable) {
		Iterator<WeakReference<IProcessVariableValueListener>> it = _weakListenerReferences
				.iterator();

		while (it.hasNext()) {
			WeakReference<IProcessVariableValueListener> wr = it.next();

			IProcessVariableValueListener listener = wr.get();

			if (listener != null) {
				runnable.doRun(listener);
			} else {
				// TODO: CALLBACK
				// _weakListenerReferences.remove(wr);
			}
		}
	}

	/**
	 * Removes weak references for value listeners that have been garbage
	 * collected.
	 */
	private void cleanupWeakReferences() {
		synchronized (_weakListenerReferences) {
			List<WeakReference> deletionCandidates = new ArrayList<WeakReference>();
			Iterator<WeakReference<IProcessVariableValueListener>> it = _weakListenerReferences
					.iterator();

			while (it.hasNext()) {
				WeakReference ref = it.next();

				if (ref.get() == null) {
					deletionCandidates.add(ref);
				}
			}

			for (WeakReference wr : deletionCandidates) {
				_weakListenerReferences.remove(wr);
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
	interface IRunnable {
		/**
		 * Hook which is called only for valid value listeners, which still live
		 * in the JVM and have not already been garbage collected.
		 * 
		 * @param valueListeners
		 *            a value listener instance (we ensure, that this is not
		 *            null)
		 */
		void doRun(IProcessVariableValueListener valueListeners);
	}
}
