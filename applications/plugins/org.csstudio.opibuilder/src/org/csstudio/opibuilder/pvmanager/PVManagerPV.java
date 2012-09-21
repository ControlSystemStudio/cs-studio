package org.csstudio.opibuilder.pvmanager;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.newValuesOf;
import static org.epics.util.time.TimeDuration.ofMillis;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriter;

/**A utility PV which uses PVManager as the connection layer. 
 * Type of the value returned by {@link #getValue()} is always {@link PMObjectValue}.
 * @author Xihui Chen
 *
 */
public class PVManagerPV implements PV {

	final private String name;
	final private boolean valueBuffered;
	private Map<PVListener, PVReaderListener> listenerMap;
	private ExceptionHandler exceptionHandler = new ExceptionHandler() {
		@Override
		public void handleException(Exception ex) {
			ErrorHandlerUtil.handleError("Error from PVManager: ", ex);
		}
	};
	private PVReader<?> pvReader;
	private PVWriter<Object> pvWriter;

	/**Construct a PVManger PV.
	 * @param name name of the pv.
	 * @param bufferAllValues true if all values should be buffered.
	 * @param updateDuration the least update duration.
	 */
	public PVManagerPV(String name, boolean bufferAllValues, int updateDuration) {
		this.name = name;
		this.valueBuffered = bufferAllValues;
		if (bufferAllValues) {
			pvReader = PVManager.read(newValuesOf(channel(name)))
					.routeExceptionsTo(exceptionHandler).maxRate(ofMillis(updateDuration));
		} else {
			pvReader = PVManager.read(channel(name))
					.routeExceptionsTo(exceptionHandler).maxRate(ofMillis(updateDuration));
		}
		pvWriter = PVManager.write(channel(name))
				.routeExceptionsTo(exceptionHandler).async();
		// pmPV = PVManager.readAndWrite(channel(name))
		// .routeExceptionsTo(exceptionHandler)
		// .asynchWriteAndMaxReadRate(ofMillis(50));
		listenerMap = new HashMap<PVListener, PVReaderListener>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IValue getValue(double timeout_seconds) throws Exception {
		return new PMObjectValue(pvReader.getValue(), valueBuffered);
	}

	@Override
	public void addListener(final PVListener listener) {
		PVReaderListener pvReaderListener = new PVReaderListener() {

			@Override
			public void pvChanged() {
				if (!pvReader.isConnected()) {
					listener.pvDisconnected(PVManagerPV.this);
					return;
				}
				Object newValue = pvReader.getValue();
				if (newValue == null) {
					return;
				}
				listener.pvValueUpdate(PVManagerPV.this);
			}
		};
		listenerMap.put(listener, pvReaderListener);
		pvReader.addPVReaderListener(pvReaderListener);
	}

	@Override
	public void removeListener(PVListener listener) {
		if (!listenerMap.containsKey(listener))
			return;
		pvReader.removePVReaderListener(listenerMap.get(listener));
		listenerMap.remove(listener);
	}

	@Override
	public void start() throws Exception {
		pvReader.setPaused(false);
	}

	public boolean isValueBuffered() {
		return valueBuffered;
	}
	
	@Override
	public boolean isRunning() {
		return !pvReader.isClosed() && !pvReader.isPaused();
	}

	@Override
	public boolean isConnected() {
		return pvReader.isConnected();
	}

	@Override
	public boolean isWriteAllowed() {
		// TODO implement this function after PVManager support this.
		return true;
	}

	
	@Override
	public String getStateInfo() {
		StringBuilder stateInfo = new StringBuilder();
		if (pvReader.isConnected()) {
			stateInfo.append("Connected");
			if (isRunning())
				stateInfo.append(" Running");
			if (pvReader.isPaused())
				stateInfo.append(" Paused");
			if (pvReader.isClosed())
				stateInfo.append(" Closed");
		} else
			stateInfo.append("Connecting");
		return stateInfo.toString();
	}

	/**
	 * Note that PVManagerPV.stop will remove all listeners and close
	 * connections. The PV cannot be accessed after stop. If you need the PV
	 * accessible later using {@link #start()}, you should call
	 * {@link #getPmPV()}.setPaused(true).
	 * 
	 * @see org.csstudio.utility.pv.PV#stop()
	 */
	@Override
	public void stop() {
		pvReader.close();
		pvWriter.close();
	}

	@Override
	public IValue getValue() {
		return new PMObjectValue(pvReader.getValue(), valueBuffered);
	}

	@Override
	public void setValue(Object new_value) throws Exception {
		pvWriter.write(new_value);
	}

}
