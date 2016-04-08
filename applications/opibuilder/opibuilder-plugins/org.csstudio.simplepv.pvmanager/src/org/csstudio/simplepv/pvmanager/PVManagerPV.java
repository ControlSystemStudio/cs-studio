/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv.pvmanager;

import static org.diirt.datasource.ExpressionLanguage.channel;
import static org.diirt.datasource.ExpressionLanguage.newValuesOf;
import static org.diirt.datasource.formula.ExpressionLanguage.channelFromFormula;
import static org.diirt.datasource.formula.ExpressionLanguage.formula;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.diirt.datasource.ExceptionHandler;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVReader;
import org.diirt.datasource.PVReaderConfiguration;
import org.diirt.datasource.PVReaderEvent;
import org.diirt.datasource.PVReaderListener;
import org.diirt.datasource.PVWriter;
import org.diirt.datasource.PVWriterConfiguration;
import org.diirt.datasource.PVWriterEvent;
import org.diirt.datasource.PVWriterListener;
import org.diirt.util.time.TimeDuration;
import org.diirt.vtype.VType;
import org.eclipse.osgi.util.NLS;

/**
 * An implementation of {@link IPV} using PVManager.
 *
 * @author Xihui Chen
 *
 */
public class PVManagerPV implements IPV {

    //Keep the exception handler separate from the PVManagerPV, to avoid
    //memory leaks in case the client doesn't close this PV.
    private static class ExHandler extends ExceptionHandler {
        private final org.csstudio.simplepv.ExceptionHandler exceptionHandler;
        ExHandler(org.csstudio.simplepv.ExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
        }
        @Override
        public void handleException(Exception ex) {
            exceptionHandler.handleException(ex);
        }
    }

    private String name;
    private boolean valueBuffered;
    private List<IPVListener> listeners;
    private ExceptionHandler exceptionHandler;
    private volatile PVReader<?> pvReader;
    private volatile PVWriter<Object> pvWriter;
    private int minUpdatePeriod;
    //If start() has been called.
    private AtomicBoolean startFlag = new AtomicBoolean(false);
    //If the PV is during start
    private AtomicBoolean starting = new AtomicBoolean(false);
    /**
     * If the pv is created for read only.
     */
    private boolean readOnly;
    private Executor notificationThread;
    private boolean isFormula;
    private static boolean debug = false;
    private static AtomicInteger counter = new AtomicInteger(0);


    /**
     * Construct a PVManger PV.
     *
     * @param name
     *            name of the PV. Must not be null.
     * @param readOnly
     *            true if the client doesn't need to write to the PV.
     * @param minUpdatePeriodInMs
     *            the minimum update period in millisecond. Must be large than 1ms.
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
    public PVManagerPV(final String name, final boolean readOnly,
            final long minUpdatePeriodInMs,    final boolean bufferAllValues, final Executor notificationThread,
            final org.csstudio.simplepv.ExceptionHandler exceptionHandler) {

        this.name = name;
        this.valueBuffered = bufferAllValues;
        this.minUpdatePeriod = (int) minUpdatePeriodInMs;

        this.readOnly = readOnly;
        listeners = new CopyOnWriteArrayList<>();

        this.notificationThread = notificationThread;
        if (exceptionHandler != null) {
            this.exceptionHandler = new ExHandler(exceptionHandler);
        }

        String singleChannel = channelFromFormula(name); // null means formula
        isFormula = singleChannel == null;
        if (isFormula)
            valueBuffered = false; // the value from a formula cannot be
                                    // buffered.
        else
            this.name = singleChannel;

    }

    @Override
    public synchronized void addListener(final IPVListener listener) {
        listeners.add(listener);
        if (pvReader != null) {
            // give an update on current value in notification thread.
            if (!pvReader.isClosed() && pvReader.isConnected() && !pvReader.isPaused()) {
                notificationThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        //allows later added listener also get connectionChanged
                        //and valueChanged event.
                        listener.connectionChanged(PVManagerPV.this);
                        listener.valueChanged(PVManagerPV.this);
                    }
                });
            }
        }

        if (!readOnly && !isFormula && pvWriter != null && !pvWriter.isClosed()) {
            notificationThread.execute(new Runnable() {
                @Override
                public void run() {
                    listener.writePermissionChanged(PVManagerPV.this);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VType> getAllBufferedValues(){
        if(pvReader == null)
            return null;
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
            return null;
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    // This method should not be synchronized because it may cause deadlock.
    public VType getValue() {
        if(pvReader == null)
            return null;
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
            return null;
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
        if(debug){
            System.out.println("Start: " + counter.incrementAndGet());
        }
        final PVReaderListener<Object> pvReaderListener = new PVReaderListener<Object>() {

            @Override
            public void pvChanged(PVReaderEvent<Object> event) {
                for(IPVListener l : listeners){
                    if (event != null) {
                        if (event.isConnectionChanged())
                            l.connectionChanged(PVManagerPV.this);
                        if (event.isExceptionChanged())
                            l.exceptionOccurred(PVManagerPV.this, event.getPvReader()
                                    .lastException());
                    }
                    if (event == null || event.isValueChanged())
                        l.valueChanged(PVManagerPV.this);
                }
            }
        };
        if (valueBuffered) {
            PVReaderConfiguration<List<VType>> pvReaderConfiguration = PVManager.read(
                    newValuesOf(channel(name, VType.class, VType.class))).notifyOn(notificationThread);
            if (exceptionHandler != null) {
                pvReaderConfiguration = pvReaderConfiguration.routeExceptionsTo(exceptionHandler);
            }
            pvReader = pvReaderConfiguration.readListener(pvReaderListener).maxRate(Duration.ofMillis(minUpdatePeriod));
        } else {
            if (isFormula) {
                PVReaderConfiguration<VType> pvReaderConfiguration = PVManager.read(formula(name, VType.class))
                        .notifyOn(notificationThread);
                if (exceptionHandler != null) {
                    pvReaderConfiguration = pvReaderConfiguration
                            .routeExceptionsTo(exceptionHandler);
                }
                pvReader = pvReaderConfiguration.readListener(pvReaderListener).maxRate(Duration.ofMillis(minUpdatePeriod));

            } else {
                PVReaderConfiguration<?> pvReaderConfiguration = PVManager.read(channel(name, VType.class, VType.class))
                        .notifyOn(notificationThread);
                if (exceptionHandler != null) {
                    pvReaderConfiguration = pvReaderConfiguration
                            .routeExceptionsTo(exceptionHandler);
                }
                pvReader = pvReaderConfiguration.readListener(pvReaderListener).maxRate(Duration.ofMillis(minUpdatePeriod));
            }
        }

        // only create writer if it is not a formula and not created for read
        // only
        if (!readOnly && !isFormula) {
            final PVWriterListener<Object> pvWriterListener = new PVWriterListener<Object>() {

                @Override
                public void pvChanged(PVWriterEvent<Object> event) {
                    for(IPVListener l : listeners){
                        if (event == null || event.isConnectionChanged())
                            l.writePermissionChanged(PVManagerPV.this);
                        if (event != null) {
                            if (event.isExceptionChanged())
                                l.exceptionOccurred(PVManagerPV.this, event.getPvWriter()
                                        .lastWriteException());
                            if (event.isWriteFailed() || event.isWriteSucceeded()) {
                                l.writeFinished(PVManagerPV.this, event.isWriteSucceeded());
                            }
                        }
                    }
                }
            };
            PVWriterConfiguration<Object> writerConfiguration = PVManager.write(channel(name));
            //TODO: PVManager could throw unnecessary exception when data source is read only
            //See: https://github.com/ControlSystemStudio/cs-studio/issues/66
            //Need to enable following line when above issue is fixed.
            //        if(exceptionHandler != null)
            //                writerConfiguration.routeExceptionsTo(exceptionHandler);
            pvWriter = writerConfiguration.writeListener(pvWriterListener).notifyOn(notificationThread).async();

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
    public synchronized void removeListener(IPVListener listener) {
        listeners.remove(listener);
    }

    public static void setDebug(boolean debug) {
        PVManagerPV.debug = debug;
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
            throw new Exception(NLS.bind("The PV {0} is not started yet or has been closed.", getName()));
        pvWriter.write(new_value);
    }

    @Override
    public void start() throws Exception {
        if (!startFlag.getAndSet(true)) {
            starting.set(true);
            notificationThread.execute(new Runnable() {
                @Override
                public void run() {
                    internalStart();
                    starting.set(false);
                }
            });
        }else
            throw new IllegalStateException(
                    NLS.bind("PV {0} has already been started.", getName()));
    }

    @Override
    public void stop() {
        if(!startFlag.get()){
            Activator.getLogger().log(Level.WARNING,
                    NLS.bind("PV {0} has already been stopped or was not started yet.", getName()));
            return;
        }
        if(starting.get()){
            notificationThread.execute(new Runnable() {

                @Override
                public void run() {
                    stop();
                }
            });
            return;
        };
        if (pvReader != null){
            pvReader.close();
            if(debug){
                System.out.println("Stop: " + counter.decrementAndGet());
            }
        }
        if (pvWriter != null)
            pvWriter.close();
        pvReader = null;
        pvWriter = null;
        exceptionHandler = null;
        startFlag.set(false);
    }

    @Override
    public boolean setValue(Object value, int timeout) throws Exception {
        final AtomicBoolean result=new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(1);
        PVWriter<Object> pvWriter = PVManager.write(channel(name))
                .timeout(TimeDuration.ofSeconds(timeout)).writeListener(
                        new PVWriterListener<Object>() {
                            @Override
                            public void pvChanged(PVWriterEvent<Object> event) {
                                latch.countDown();
                                if(event.isWriteFailed()){
                                    result.set(false);
                                }
                                if(event.isWriteSucceeded())
                                    result.set(true);
                            }
                        }).sync();
        try {
            if(latch.await(timeout, TimeUnit.MILLISECONDS))
                pvWriter.write(value);
            else
                throw new Exception(NLS.bind("Failed to connect to the PV in {0} milliseconds.", timeout));
        }finally{
            pvWriter.close();
        }
        return result.get();
    }

    @Override
    public String toString() {
        return getName();
    }
}
