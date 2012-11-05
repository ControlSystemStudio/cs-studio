/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.expression.WriteExpressionImpl;
import org.epics.pvmanager.expression.WriteExpression;
import java.util.concurrent.Executor;
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

    @Override
    public PVWriterConfiguration<T> timeout(TimeDuration timeout) {
        super.timeout(timeout);
        return this;
    }

    @Override
    public PVWriterConfiguration<T> timeout(TimeDuration timeout, String timeoutMessage) {
        super.timeout(timeout, timeoutMessage);
        return this;
    }
    
    private WriteExpression<T> writeExpression;
    private ExceptionHandler exceptionHandler;
    private List<PVWriterListener> writeListeners = new ArrayList<PVWriterListener>();

    PVWriterConfiguration(WriteExpression<T> writeExpression) {
        this.writeExpression = writeExpression;
    }
    
    /**
     * 
     * @param listeners
     * @return 
     */
    public PVWriterConfiguration<T> listeners(PVWriterListener... listeners) {
        for (PVWriterListener pVWriterListener : listeners) {
            writeListeners.add(pVWriterListener);
        }
        return this;
    }

    /**
     * Forwards exception to the given exception handler. No thread switch
     * is done, so the handler is notified on the thread where the exception
     * was thrown.
     * <p>
     * Giving a custom exception handler will disable the default handler,
     * so {@link PVWriter#lastWriteException() } is no longer set and no notification
     * is done.
     *
     * @param exceptionHandler an exception handler
     * @return this
     */
    public PVWriterConfiguration<T> routeExceptionsTo(ExceptionHandler exceptionHandler) {
        if (this.exceptionHandler != null) {
            throw new IllegalArgumentException("Exception handler already set");
        }
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    private PVWriter<T> create(boolean syncWrite) {
        checkDataSourceAndThreadSwitch();

        // Create PVReader and connect
        PVWriterImpl<T> pvWriter = new PVWriterImpl<T>(syncWrite, Executors.localThread() == notificationExecutor);
        for (PVWriterListener pVWriterListener : writeListeners) {
            pvWriter.addPVWriterListener(pVWriterListener);
        }
        if (exceptionHandler == null) {
            exceptionHandler = ExceptionHandler.createDefaultExceptionHandler(pvWriter, notificationExecutor);
        }
        WriteBuffer writeBuffer = writeExpression.createWriteBuffer().exceptionHandler(exceptionHandler).build();
        WriteFunction<T> writeFunction =writeExpression.getWriteFunction();

        try {
            if (timeoutMessage == null)
                timeoutMessage = "Write timeout";
            pvWriter.setWriteDirector(new WriteDirector<T>(writeFunction, writeBuffer, source, PVManager.getAsyncWriteExecutor(), exceptionHandler,
                    timeout, timeoutMessage));
        } catch (Exception ex) {
            exceptionHandler.handleException(ex);
        }
        
        WriteNotifier<T> notifier = new WriteNotifier<T>(pvWriter, new LastValueAggregator<Boolean>(writeBuffer.getConnectionCollector()), 
                PVManager.getReadScannerExecutorService(), notificationExecutor, exceptionHandler);
        notifier.startScan(TimeDuration.ofMillis(100));
        return pvWriter;
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
