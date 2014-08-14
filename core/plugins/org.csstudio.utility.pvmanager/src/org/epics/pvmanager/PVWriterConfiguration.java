/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import org.epics.pvmanager.expression.WriteExpression;
import org.epics.pvmanager.util.Executors;
import org.epics.util.time.TimeDuration;

/**
 * An expression used to set the final parameters on how the pv expression
 * should be written.
 * 
 * @param <T> the type of the expression
 * @author carcassi
 */
public class PVWriterConfiguration<T> extends CommonConfiguration {

    @Override
    public PVWriterConfiguration<T> from(DataSource dataSource) {
        super.from(dataSource);
        return this;
    }

    @Override
    public PVWriterConfiguration<T> notifyOn(Executor onThread) {
        super.notifyOn(onThread);
        return this;
    }

    /**
     * Sets a timeout for write operation.
     * <p>
     * For more details, consult {@link #timeout(org.epics.util.time.TimeDuration, java.lang.String) }.
     *
     * @param timeout the duration of the timeout; can't be null
     * @return this expression
     */
    @Override
    public PVWriterConfiguration<T> timeout(TimeDuration timeout) {
        super.timeout(timeout);
        return this;
    }

    /**
     * Sets a timeout for write operations.
     * <p>
     * When a write operation lasts longer than the given timeout, a notification
     * is sent with a {@link TimeoutException}. Note that, in the current implementation,
     * the write is not cancelled and may still trigger a second notification.
     * With a synch write, the method returns at the timeout expiration with
     * the exception.
     *
     * @param timeout the duration of the timeout; can't be null
     * @param timeoutMessage the message for the reported timeout
     * @return this expression
     */
    @Override
    public PVWriterConfiguration<T> timeout(TimeDuration timeout, String timeoutMessage) {
        super.timeout(timeout, timeoutMessage);
        return this;
    }
    
    private final WriteExpression<T> writeExpression;
    private ExceptionHandler exceptionHandler;
    private final List<PVWriterListener<T>> writeListeners = new ArrayList<>();
    
    PVWriterImpl<T> pvWriter;
    WriteFunction<T> writeFunction;

    PVWriterConfiguration(WriteExpression<T> writeExpression) {
        this.writeExpression = writeExpression;
    }
    
    /**
     * Adds a listener notified for any writer event (write result, connection and errors).
     * <p>
     * Registering a listener here guarantees that no event is ever missed.
     * 
     * @param listener the listener to register
     * @return this expression
     */
    public PVWriterConfiguration<T> writeListener(PVWriterListener<? extends T> listener) {
        @SuppressWarnings("unchecked")
        PVWriterListener<T> convertedListener = (PVWriterListener<T>) listener;
        writeListeners.add(convertedListener);
        return this;
    }

    /**
     * Forwards exception to the given exception handler. No thread switch
     * is done, so the handler is notified on the thread where the exception
     * was thrown.
     *
     * @param exceptionHandler an exception handler
     * @return this
     */
    public PVWriterConfiguration<T> routeExceptionsTo(ExceptionHandler exceptionHandler) {
        if (this.exceptionHandler != null) {
            throw new IllegalArgumentException("Exception handler already set");
        }
        this.exceptionHandler = ExceptionHandler.safeHandler(exceptionHandler);
        return this;
    }

    private PVWriter<T> create(boolean syncWrite) {
        validateWriterConfiguration();
        checkDataSourceAndThreadSwitch();

        preparePvWriter(syncWrite);
        PVWriterDirector<T> writerDirector = prepareDirector(this);
        prepareDecoupler(writerDirector, this);
        
        return pvWriter;
    }

    private void validateWriterConfiguration() {
        if (timeoutMessage == null)
            timeoutMessage = "Write timeout";
    }
    
    void preparePvWriter(boolean syncWrite) {
        // Create PVReader and connect
        pvWriter = new PVWriterImpl<>(syncWrite, Executors.localThread() == notificationExecutor);
        for (PVWriterListener<T> pVWriterListener : writeListeners) {
            pvWriter.addPVWriterListener(pVWriterListener);
        }
        writeFunction = writeExpression.getWriteFunction();
    }
    
    static <T> PVWriterDirector<T> prepareDirector(PVWriterConfiguration<T> writerConfiguration) {
        PVWriterDirector<T> writerDirector = new PVWriterDirector<T>(writerConfiguration.pvWriter,
                writerConfiguration.writeFunction, writerConfiguration.dataSource, PVManager.getAsyncWriteExecutor(),
                writerConfiguration.notificationExecutor, PVManager.getReadScannerExecutorService(),
                writerConfiguration.timeout, writerConfiguration.timeoutMessage, writerConfiguration.exceptionHandler);
        writerDirector.connectExpression(writerConfiguration.writeExpression);
        writerConfiguration.pvWriter.setWriteDirector(writerDirector);
        return writerDirector;
    }
    
    static <T> void prepareDecoupler(PVWriterDirector<T> director, PVWriterConfiguration<T> writerConfiguration) {
        ScannerParameters scannerParameters = new ScannerParameters()
                .writerDirector(director)
                .scannerExecutor(PVManager.getReadScannerExecutorService())
                .maxDuration(TimeDuration.ofMillis(100));
        scannerParameters.type(ScannerParameters.Type.PASSIVE);
        SourceDesiredRateDecoupler rateDecoupler = scannerParameters.build();
        
        director.setScanner(rateDecoupler);
        rateDecoupler.start();
    }
    
    /**
     * Creates a new PVWriter where the {@link PVWriter#write(java.lang.Object) }
     * method is synchronous (i.e. blocking).
     * 
     * @return a new PVWriter
     */
    public PVWriter<T> sync() {
        return create(true);
    }

    /**
     * Creates a new PVWriter where the {@link PVWriter#write(java.lang.Object) }
     * method is asynchronous (i.e. non-blocking).
     * 
     * @return a new PVWriter
     */
    public PVWriter<T> async() {
        return create(false);
    }
}
