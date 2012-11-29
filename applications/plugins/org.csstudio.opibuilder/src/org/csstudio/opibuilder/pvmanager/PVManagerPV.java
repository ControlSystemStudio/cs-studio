package org.csstudio.opibuilder.pvmanager;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.swtThread;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.newValuesOf;
import static org.epics.util.time.TimeDuration.ofMillis;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.swt.widgets.Display;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
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
	private Map<PVListener, PVReaderListener<Object>> listenerMap;
	private ExceptionHandler exceptionHandler = new ExceptionHandler() {
		@Override
		public void handleException(Exception ex) {
			ErrorHandlerUtil.handleError("Error from PVManager: ", ex);
		}
	};
	private PVReader<?> pvReader;
	private PVWriter<Object> pvWriter;
	private int updateDuration;

	/**Construct a PVManger PV.
	 * @param name name of the pv.
	 * @param bufferAllValues true if all values should be buffered.
	 * @param updateDuration the least update duration.
	 */
	public PVManagerPV(String name, boolean bufferAllValues, int updateDuration) {
		this.name = name;
		this.valueBuffered = bufferAllValues;
		this.updateDuration = updateDuration;
		listenerMap = new HashMap<PVListener, PVReaderListener<Object>>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IValue getValue(double timeout_seconds) throws Exception {
		checkIfPVStarted();
		return new PMObjectValue(pvReader.getValue(), valueBuffered);
	}

	/** Listener should not be added after pv started. It will miss the previous value.
	 */
	@Override
	public synchronized void addListener(final PVListener listener) {
		if(pvReader !=null)
			throw new IllegalStateException("The PVManagerPV is alread started. " +
					"Listener must be added before it is stared.");
		PVReaderListener<Object> pvReaderListener = new PVReaderListener<Object>() {
	
			@Override
			public void pvChanged(PVReaderEvent<Object> event) {
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
	}

	@Override
	public synchronized void removeListener(PVListener listener) {
		if (!listenerMap.containsKey(listener))
			return;
		if(pvReader != null)
			pvReader.removePVReaderListener(listenerMap.get(listener));
		listenerMap.remove(listener);
	}

	/*This method must be called in swt thread.
	 * (non-Javadoc)
	 * @see org.csstudio.utility.pv.PV#start()
	 */
	@Override
	public synchronized void start() throws Exception {
		if(Display.getCurrent() == null)
			throw new IllegalThreadStateException(
					"PVManagerPV.start() must be called in swt thread");
		if(pvReader == null){
			if (valueBuffered) {
				pvReader = PVManager.read(newValuesOf(channel(name)))
						.notifyOn(swtThread())
						.routeExceptionsTo(exceptionHandler)
						.maxRate(ofMillis(updateDuration));
			} else {
				pvReader = PVManager.read(channel(name)).notifyOn(swtThread())
						.routeExceptionsTo(exceptionHandler)
						.maxRate(ofMillis(updateDuration));
			}
			for(PVReaderListener<Object> pvReaderListener : listenerMap.values())
				pvReader.addPVReaderListener(pvReaderListener);
			pvWriter = PVManager.write(channel(name))
					.routeExceptionsTo(exceptionHandler).async();
		}else		
			pvReader.setPaused(false);
	}

	public boolean isValueBuffered() {
		return valueBuffered;
	}
	
	@Override
	public synchronized boolean isRunning() {
		if(pvReader == null) 
			return false;
		return !pvReader.isClosed() && !pvReader.isPaused();
	}

	@Override
	public synchronized boolean isConnected() {
		if(pvReader == null) 
			return false;
		return pvReader.isConnected() && pvReader.getValue() != null;
	}

	@Override
	public synchronized boolean isWriteAllowed() {
		if(pvWriter == null) 
			return false;
		return pvWriter.isWriteConnected();
	}

	
	@Override
	public synchronized String getStateInfo() {
		checkIfPVStarted();
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
	public synchronized void stop() {
		if(pvReader != null)
			pvReader.close();
		if(pvWriter != null)
			pvWriter.close();
	}

	@Override
	public synchronized IValue getValue() {
		checkIfPVStarted();
		return new PMObjectValue(pvReader.getValue(), valueBuffered);
	}

	@Override
	public void setValue(Object new_value) throws Exception {
		checkIfPVStarted();
		pvWriter.write(new_value);
	}
	
	private void checkIfPVStarted(){
		if(pvReader == null || pvWriter == null)
			throw new IllegalStateException("PVManagerPV is not started yet.");
	}

}
