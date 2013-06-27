/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv.pvmanager;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.newValuesOf;
import static org.epics.pvmanager.formula.ExpressionLanguage.channelFromFormula;
import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.util.time.TimeDuration.ofMillis;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.eclipse.osgi.util.NLS;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderConfiguration;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.vtype.VType;

/**
 * An implementation of {@link IPV} using PVManager.
 * 
 * @author Xihui Chen
 * 
 */
public class PVManagerPV implements IPV {

	private String name;
	private boolean valueBuffered;
	private Map<IPVListener, PVReaderListener<Object>> readListenerMap;
	private Map<IPVListener, PVWriterListener<Object>> writeListenerMap;
	private ExceptionHandler exceptionHandler;
	private volatile PVReader<?> pvReader;
	private volatile PVWriter<Object> pvWriter;
	private int maxUpdateRate;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	/**
	 * If the pv is created for read only.
	 */
	private boolean readOnly;
	private Executor notificationThread;
	private boolean isFormula;

	/**
	 * Construct a PVManger PV.
	 * 
	 * @param name
	 *            name of the PV. Must not be null.
	 * @param readOnly
	 *            true if the client doesn't need to write to the PV.
	 * @param maxUpdateRate
	 *            the maximum update rate in milliseconds.
	 * @param bufferAllValues
	 *            if all value on the PV should be buffered during two updates.
	 * @param notificationThread
	 *            the thread on which the read and write listener will be
	 *            notified. Must not be null.
	 * @param exceptionHandler
	 *            the handler to handle all exceptions happened in pv connection
	 *            layer. If this is null, pv read listener or pv write listener
	 *            will be notified on read or write exceptions respectively.
	 * 
	 */
	public PVManagerPV(final String name, final boolean readOnly, final int maxUpdateRate,
			final boolean bufferAllValues, final Executor notificationThread,
			final org.csstudio.simplepv.ExceptionHandler exceptionHandler) {

		this.name = name;
		this.valueBuffered = bufferAllValues;
		this.maxUpdateRate = maxUpdateRate;
		this.readOnly = readOnly;
		readListenerMap = new LinkedHashMap<IPVListener, PVReaderListener<Object>>(4);

		this.notificationThread = notificationThread;
		if (exceptionHandler != null) {
			this.exceptionHandler = new ExceptionHandler() {
				@Override
				public void handleException(Exception ex) {
					exceptionHandler.handleException(ex);
				}
			};
		}

		String singleChannel = channelFromFormula(name); // null means formula
		isFormula = singleChannel == null;
		if (isFormula)
			valueBuffered = false; // the value from a formula cannot be
									// buffered.
		else
			this.name = singleChannel;

		if (!readOnly && !isFormula) {
			writeListenerMap = new LinkedHashMap<>(4);
		}
	}

	@Override
	public synchronized void addPVListener(final IPVListener listener) {
		final PVReaderListener<Object> pvReaderListener = new PVReaderListener<Object>() {

			@Override
			public void pvChanged(PVReaderEvent<Object> event) {
				if (event != null) {
					if (event.isConnectionChanged())
						listener.connectionChanged(PVManagerPV.this);
					if (event.isExceptionChanged())
						listener.exceptionOccurred(PVManagerPV.this, event.getPvReader()
								.lastException());
				}
				if (event == null || event.isValueChanged())
					listener.valueChanged(PVManagerPV.this);
			}
		};
		readListenerMap.put(listener, pvReaderListener);
		if (pvReader != null) {
			// give an update on current value in PMPV thread.
			if (!pvReader.isClosed() && pvReader.isConnected() && !pvReader.isPaused()) {
				notificationThread.execute(new Runnable() {
					@Override
					public void run() {
						pvReaderListener.pvChanged(null);
					}
				});
			}
			pvReader.addPVReaderListener(pvReaderListener);
		}

		if (!readOnly && !isFormula) {
			final PVWriterListener<Object> pvWriterListener = new PVWriterListener<Object>() {

				@Override
				public void pvChanged(PVWriterEvent<Object> event) {
					if (event == null || event.isConnectionChanged())
						listener.writePermissionChanged(PVManagerPV.this);
					if (event != null) {
						if (event.isExceptionChanged())
							listener.exceptionOccurred(PVManagerPV.this, event.getPvWriter()
									.lastWriteException());
						if (event.isWriteFailed() || event.isWriteSucceeded()) {
							listener.writeFinished(PVManagerPV.this, event.isWriteSucceeded());
						}
					}
				}
			};

			writeListenerMap.put(listener, pvWriterListener);
			if (pvWriter != null) {
				if (!pvWriter.isClosed()) {
					notificationThread.execute(new Runnable() {

						@Override
						public void run() {
							pvWriterListener.pvChanged(null);
						}
					});
					pvWriter.addPVWriterListener(pvWriterListener);
				}
			}
		}
	}

	private void checkIfPVStarted() {
		if (pvReader == null)
			throw new IllegalStateException(NLS.bind("PVManagerPV {0} is not started yet.", name));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VType> getAllBufferedValues() throws Exception {
		checkIfPVStarted();
		Object obj = pvReader.getValue();
		if (obj != null) {
			if (!valueBuffered) {
				if (obj instanceof VType)
					return Arrays.asList((VType) obj);
			} else {
				if (obj instanceof List<?> && ((List<?>) obj).size() > 0) {
					// Assume it is returning a VType List. If it is not, the
					// client needs to handle it.
					return (List<VType>) obj;
				}
			}
			throw new Exception("Unknown data type returned from PVManager.");
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	// This method should not be synchronized because it may cause deadlock.
	public VType getValue() throws Exception {
		checkIfPVStarted();
		Object obj = pvReader.getValue();
		if (obj != null) {
			if (!valueBuffered) {
				if (obj instanceof VType)
					return (VType) obj;
			} else {
				if (obj instanceof List<?> && ((List<?>) obj).size() > 0) {
					Object lastValue = ((List<?>) obj).get(((List<?>) obj).size() - 1);
					if (lastValue instanceof VType)
						return (VType) lastValue;
				}
			}
			throw new Exception("Unknown data type returned from PVManager.");
		}
		return null;
	}

	/**
	 * This method must be called in notification thread, because PVManager
	 * requires that creating PVReader, adding listeners must be done in the
	 * notification thread and must be in the same runnable to make sure no
	 * updates are missed.
	 */
	private synchronized void internalStart() {
		if (valueBuffered) {
			PVReaderConfiguration<List<Object>> pvReaderConfiguration = PVManager.read(
					newValuesOf(channel(name))).notifyOn(notificationThread);
			if (exceptionHandler != null) {
				pvReaderConfiguration = pvReaderConfiguration.routeExceptionsTo(exceptionHandler);
			}
			pvReader = pvReaderConfiguration.maxRate(ofMillis(maxUpdateRate));
		} else {
			if (isFormula) {
				PVReaderConfiguration<?> pvReaderConfiguration = PVManager.read(formula(name))
						.notifyOn(notificationThread);
				if (exceptionHandler != null) {
					pvReaderConfiguration = pvReaderConfiguration
							.routeExceptionsTo(exceptionHandler);
				}
				pvReader = pvReaderConfiguration.maxRate(ofMillis(maxUpdateRate));

			} else {
				PVReaderConfiguration<?> pvReaderConfiguration = PVManager.read(channel(name))
						.notifyOn(notificationThread);
				if (exceptionHandler != null) {
					pvReaderConfiguration = pvReaderConfiguration
							.routeExceptionsTo(exceptionHandler);
				}
				pvReader = pvReaderConfiguration.maxRate(ofMillis(maxUpdateRate));
			}
		}
		for (PVReaderListener<Object> pvReaderListener : readListenerMap.values())
			pvReader.addPVReaderListener(pvReaderListener);

		// only create writer if it is not a formula and not created for read
		// only
		if (!readOnly && !isFormula) {
			pvWriter = PVManager.write(channel(name)).notifyOn(notificationThread).async();
			for (PVWriterListener<Object> pvWriterListener : writeListenerMap.values()) {
				pvWriter.addPVWriterListener(pvWriterListener);
			}
		}

	}

	@Override
	public boolean isBufferingValues() {
		return valueBuffered;
	}

	@Override
	public boolean isConnected() {
		if (pvReader == null)
			return false;
		// TODO: This is not fully implemented since PVmanager doesn't provide a
		// clear connection definition yet.
		return pvReader.isConnected();
	}

	@Override
	public boolean isPaused() {
		if (pvReader != null && !pvReader.isClosed())
			return pvReader.isPaused();
		return false;
	}

	@Override
	public boolean isStarted() {
		return startFlag.get();
	}

	@Override
	public boolean isWriteAllowed() {
		if (pvWriter == null)
			return false;
		return pvWriter.isWriteConnected();
	}

	@Override
	public synchronized void removePVListener(IPVListener listener) {
		if (readListenerMap.containsKey(listener)) {
			if (pvReader != null)
				pvReader.removePVReaderListener(readListenerMap.get(listener));
			readListenerMap.remove(listener);
		}
		if (writeListenerMap != null && writeListenerMap.containsKey(listener)) {
			if (pvWriter != null)
				pvWriter.removePVWriterListener(writeListenerMap.get(listener));
			writeListenerMap.remove(listener);
		}
	}

	@Override
	public void setPaused(boolean paused) {
		if (pvReader != null)
			pvReader.setPaused(paused);
	}

	@Override
	public void setValue(Object new_value) throws Exception {
		if (readOnly)
			throw new Exception(NLS.bind("The PV {0} was created for read only.", getName()));
		if (isFormula)
			throw new Exception(NLS.bind("The PV {0} is a formula which is not allowed to write.",
					getName()));
		if (pvWriter == null || pvWriter.isClosed())
			throw new Exception(NLS.bind("The PV {0} is not started yet.", getName()));
		pvWriter.write(new_value);
	}

	@Override
	public void start() throws Exception {
		if (!startFlag.getAndSet(true)) {
			notificationThread.execute(new Runnable() {

				@Override
				public void run() {
					internalStart();
				}
			});
		} else if (pvReader != null)
			pvReader.setPaused(false);
	}

	@Override
	public void stop() {
		if (pvReader != null)
			pvReader.close();
		if (pvWriter != null)
			pvWriter.close();
		pvReader = null;
		pvWriter = null;
		startFlag.set(false);
	}

}
