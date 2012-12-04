package org.csstudio.opibuilder.pvmanager;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.newValuesOf;
import static org.epics.util.time.TimeDuration.ofMillis;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.util.DisplayUtils;
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
	
	final private static String KEY_SWT_THREAD = "org.csstudio.opibuilder.swt_thread"; //$NON-NLS-1$
	private static Executor SWT_THREAD;    
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
	private Display display;
	/**Construct a PVManger PV.
	 * @param name name of the pv.
	 * @param bufferAllValues true if all values should be buffered.
	 * @param updateDuration the least update duration.
	 * @param display display of the SWT thread. Can be null for non-rap application.
	 */
	public PVManagerPV(String name, boolean bufferAllValues, int updateDuration, Display display) {
		this.name = name;
		this.valueBuffered = bufferAllValues;
		this.updateDuration = updateDuration;
		listenerMap = new LinkedHashMap<PVListener, PVReaderListener<Object>>();
		this.display = display;
		if(display ==null)
			this.display = DisplayUtils.getDisplay();
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
		final PVReaderListener<Object> pvReaderListener = new PVReaderListener<Object>() {
	
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
		if(pvReader !=null){
			//give an update on current value in SWT thread.
			if(!pvReader.isClosed() && pvReader.isConnected() && !pvReader.isPaused()){
				if(Display.getCurrent() == null)
					DisplayUtils.getDisplay().asyncExec(new Runnable() {						
						@Override
						public void run() {
							pvReaderListener.pvChanged(null);
						}
					});
				else
					pvReaderListener.pvChanged(null);
			}
			
			pvReader.addPVReaderListener(pvReaderListener);			
		}
	}

	@Override
	public synchronized void removeListener(PVListener listener) {
		if (!listenerMap.containsKey(listener))
			return;
		if(pvReader != null)
			pvReader.removePVReaderListener(listenerMap.get(listener));
		listenerMap.remove(listener);
	}


	@Override
	public synchronized void start() throws Exception {		
		if (pvReader == null) {
			if (Display.getCurrent() != null)
				internalStart();
			else {
				if (display != null)
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							internalStart();
						}
					});
				else
					throw new RuntimeException("display is null. The PV must be " +
							"started in SWT thread. Please specify the display when create the PV.");

			}

		} else
	pvReader.setPaused(false);
	}

	/**
	 * This method must be called in swt thread, because PVManager requires
	 * that creating PVReader, adding listeners must be done in the notification
	 * thread and must be in the same runnable to make sure no updates are missed. 
	 */
	private void internalStart() {
		Executor swtThread = null;
		if(OPIBuilderPlugin.isRAP()){
			swtThread = (Executor) display.getData(KEY_SWT_THREAD);
			if(swtThread == null){
				swtThread = createSWTThread(display);
				display.setData(KEY_SWT_THREAD, swtThread);
			}
		}else {
			if(SWT_THREAD == null)
				SWT_THREAD = createSWTThread(DisplayUtils.getDisplay());
			swtThread = SWT_THREAD;
		}
		if (valueBuffered) {
			pvReader = PVManager.read(newValuesOf(channel(name)))
					.notifyOn(swtThread)
					.routeExceptionsTo(exceptionHandler)
					.maxRate(ofMillis(updateDuration));
		} else {
			pvReader = PVManager.read(channel(name)).notifyOn(swtThread)
					.routeExceptionsTo(exceptionHandler)
					.maxRate(ofMillis(updateDuration));
		}
		for(PVReaderListener<Object> pvReaderListener : listenerMap.values())
			pvReader.addPVReaderListener(pvReaderListener);
		pvWriter = PVManager.write(channel(name))
				.routeExceptionsTo(exceptionHandler).async();
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
		pvReader = null;
		pvWriter = null;
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
	

	private static Executor createSWTThread(final org.eclipse.swt.widgets.Display display) {
		return new Executor() {

	        @Override
	        public void execute(Runnable task) {
	            try {
	            	if (!display.isDisposed()) {
	            	    display.asyncExec(task);
	            	}
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	    };
	}

}
