/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.concurrent.Executor;
import org.epics.pvmanager.expression.DesiredRateReadWriteExpression;
import org.epics.util.time.TimeDuration;

/**
 * Allows to configure the type of read/write PV to create.
 *
 * @param <R> the read payload
 * @param <W> the write payload
 * @author carcassi
 */
public class PVConfiguration<R, W> extends CommonConfiguration {
    
    private final PVReaderConfiguration<R> pvReaderConfiguration;
    private final PVWriterConfiguration<W> pvWriterConfiguration;

    PVConfiguration(DesiredRateReadWriteExpression<R, W> readWriteExpression) {
        pvReaderConfiguration = new PVReaderConfiguration<R>(readWriteExpression);
        pvWriterConfiguration = new PVWriterConfiguration<W>(readWriteExpression);
    }
    
    @Override
    public PVConfiguration<R, W> from(DataSource dataSource) {
        pvReaderConfiguration.from(dataSource);
        pvWriterConfiguration.from(dataSource);
        return this;
    }

    @Override
    public PVConfiguration<R, W> notifyOn(Executor onThread) {
        pvReaderConfiguration.notifyOn(onThread);
        pvWriterConfiguration.notifyOn(onThread);
        return this;
    }

    /**
     * Sets a timeout for both reader and writer.
     *
     * @param timeout duration of the timeout
     * @return this expression
     */
    @Override
    public PVConfiguration<R, W> timeout(TimeDuration timeout) {
        pvReaderConfiguration.timeout(timeout);
        pvWriterConfiguration.timeout(timeout);
        return this;
    }

    /**
     * Sets a timeout with the given message for both read and writer.
     *
     * @param timeout duration of the timeout
     * @param timeoutMessage message for the timeout
     * @return this expression
     */
    @Override
    public PVConfiguration<R, W>  timeout(TimeDuration timeout, String timeoutMessage) {
        pvReaderConfiguration.timeout(timeout, timeoutMessage);
        pvWriterConfiguration.timeout(timeout, timeoutMessage);
        return this;
    }

    /**
     * Specifies a timeout, with a different message for the read and the write.
     * 
     * @param timeout time before notification
     * @param readMessage exception message for the read timeout
     * @param writeMessage exception message for the write timeout
     * @return this
     */
    public PVConfiguration<R, W>  timeout(TimeDuration timeout, String readMessage, String writeMessage) {
        pvReaderConfiguration.timeout(timeout, readMessage);
        pvWriterConfiguration.timeout(timeout, writeMessage);
        return this;
    }
    
    /**
     * Adds a listener for the read events.
     *
     * @param listener the new listener
     * @return this expression
     */
    public PVConfiguration<R, W>  readListener(PVReaderListener<? super R> listener) {
        pvReaderConfiguration.readListener(listener);
        return this;
    }
    
    /**
     * Adds a listener for the write events.
     *
     * @param listener the new listener
     * @return this expression
     */
    public PVConfiguration<R, W>  writeListener(PVWriterListener<? extends W> listener) {
        pvWriterConfiguration.writeListener(listener);
        return this;
    }

    /**
     * Forwards exception to the given exception handler. No thread switch
     * is done, so the handler is notified on the thread where the exception
     * was thrown.
     * <p>
     * Giving a custom exception handler will disable the default handler,
     * so {@link PV#lastException() } and {@link PV#lastWriteException() }
     * is no longer set and no notification
     * is done.
     *
     * @param exceptionHandler an exception handler
     * @return this
     */
    public PVConfiguration<R, W> routeExceptionsTo(ExceptionHandler exceptionHandler) {
        pvReaderConfiguration.routeExceptionsTo(exceptionHandler);
        pvWriterConfiguration.routeExceptionsTo(exceptionHandler);
        return this;
    }
    
    /**
     * Creates the pv such that writes are synchronous and read notifications
     * comes at most at the rate specified.
     * 
     * @param period minimum time between read notifications
     * @return a new PV
     */
    public PV<R, W> synchWriteAndMaxReadRate(TimeDuration period) {
        PVReader<R> pvReader = pvReaderConfiguration.maxRate(period);
        PVWriter<W> pvWriter = pvWriterConfiguration.sync();
        return new PV<R, W>(pvReader, pvWriter);
    }
    
    /**
     * Creates the pv such that writes are asynchronous and read notifications
     * comes at most at the rate specified.
     * 
     * @param period minimum time between read notifications
     * @return a new PV
     */
    public PV<R, W> asynchWriteAndMaxReadRate(TimeDuration period) {
        PVReader<R> pvReader = pvReaderConfiguration.maxRate(period);
        PVWriter<W> pvWriter = pvWriterConfiguration.async();
        PV<R, W> pv = new PV<R, W>(pvReader, pvWriter);
        PVReaderImpl.implOf(pvReader).setReaderForNotification(pv);
        return pv;
    }
    
}
