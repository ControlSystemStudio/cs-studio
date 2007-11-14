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

@SuppressWarnings("unchecked")
class DalConnector implements DynamicValueListener, LinkListener {
	private IProcessVariableAddress _processVariableAddress;

	private DynamicValueProperty _dalProperty;

	private List<WeakReference<IProcessVariableValueListener>> _weakListenerReferences;

	public DalConnector() {
		_weakListenerReferences = new ArrayList<WeakReference<IProcessVariableValueListener>>();
	}

	public void addProcessVariableValueListener(
			IProcessVariableValueListener<Double> listener) {
		_weakListenerReferences
				.add(new WeakReference<IProcessVariableValueListener>(listener));
	}

	public boolean isDisposable() {
		// perform a cleanup first
		cleanupWeakReferences();

		// this connector can be disposed it there are not weak references left
		return _weakListenerReferences.isEmpty();
	}

	public void setProcessVariableAddress(
			IProcessVariableAddress processVariableAddress) {
		_processVariableAddress = processVariableAddress;
	}

	public IProcessVariableAddress getProcessVariableAddress() {
		return _processVariableAddress;
	}

	public void setDalProperty(DynamicValueProperty dalProperty) {
		_dalProperty = dalProperty;
	}

	public DynamicValueProperty getDalProperty() {
		return _dalProperty;
	}

	public void conditionChange(DynamicValueEvent event) {
	}

	public void errorResponse(DynamicValueEvent event) {

	}

	public void timelagStarts(DynamicValueEvent event) {

	}

	public void timelagStops(DynamicValueEvent event) {

	}

	public void timeoutStarts(DynamicValueEvent event) {

	}

	public void timeoutStops(DynamicValueEvent event) {

	}

	public void valueChanged(final DynamicValueEvent event) {
		execute(new IRunnable() {
			public void doRun(IProcessVariableValueListener simpleDalListener) {
				simpleDalListener.valueChanged(event.getValue());
			}
		});
	}

	public void valueUpdated(final DynamicValueEvent event) {
		execute(new IRunnable() {
			public void doRun(IProcessVariableValueListener simpleDalListener) {
				simpleDalListener.valueChanged(event.getValue());
			}
		});
	}

	public void connected(final ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	public void connectionFailed(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	public void connectionLost(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	public void destroyed(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	public void disconnected(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	public void resumed(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	public void suspended(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

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

	interface IRunnable {
		void doRun(IProcessVariableValueListener simpleDalListener);
	}

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

}
