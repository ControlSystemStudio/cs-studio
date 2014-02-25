/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.util.Executors;
import org.epics.util.time.TimeDuration;

/**
 * An expression used to set the final parameters on how the pv expression
 * should be read.
 * 
 * @param <T> the type of the expression
 * @author carcassi
 */
public class PVReaderConfiguration<T> extends CommonConfiguration {

    @Override
    public PVReaderConfiguration<T> from(DataSource dataSource) {
        super.from(dataSource);
        return this;
    }

    @Override
    public PVReaderConfiguration<T> notifyOn(Executor onThread) {
        super.notifyOn(onThread);
        return this;
    }

    /**
     * Sets a timeout for no values received.
     * <p>
     * For more details, consult {@link #timeout(org.epics.util.time.TimeDuration, java.lang.String) }.
     *
     * @param timeout the duration of the timeout; can't be null
     * @return this expression
     */
    @Override
    public PVReaderConfiguration<T> timeout(TimeDuration timeout) {
        super.timeout(timeout);
        return this;
    }

    /**
     * Sets a timeout for no values received with the given message.
     * <p>
     * If no value is received before the given time, a {@link TimeoutException}
     * is notified through the listener. Note the difference: the timeout is
     * not on the connection but on the value itself. This allows to use timeouts
     * when creating combined expressions that can produce data even if not
     * all elements have values. For single channels, this means that if the
     * channel is connected, but no value has been processed, a timeout
     * exception is still sent.
     *
     * @param timeout the duration of the timeout; can't be null
     * @param timeoutMessage the message for the reported timeout
     * @return this expression
     */
    @Override
    public PVReaderConfiguration<T> timeout(TimeDuration timeout, String timeoutMessage) {
        super.timeout(timeout, timeoutMessage);
        return this;
    }
    
    private DesiredRateExpression<T> aggregatedPVExpression;
    private ExceptionHandler exceptionHandler;
    private List<PVReaderListener<T>> readListeners = new ArrayList<>();

    PVReaderConfiguration(DesiredRateExpression<T> aggregatedPVExpression) {
        this.aggregatedPVExpression = aggregatedPVExpression;
    }
    
    /**
     * Adds a listener notified for any reader event (values, connection and errors).
     * <p>
     * Registering a listener here guarantees that no event is ever missed.
     * 
     * @param listener the listener to register
     * @return this expression
     */
    public PVReaderConfiguration<T> readListener(PVReaderListener<? super T> listener) {
        @SuppressWarnings("unchecked")
        PVReaderListener<T> convertedListener = (PVReaderListener<T>) listener;
        readListeners.add(convertedListener);
        return this;
    }

    /**
     * Forwards exception to the given exception handler. No thread switch
     * is done, so the handler is notified on the thread where the exception
     * was thrown.
     * <p>
     * Giving a custom exception handler will disable the default handler,
     * so {@link PVReader#lastException() } is no longer set and no notification
     * is done.
     *
     * @param exceptionHandler an exception handler
     * @return this
     */
    public PVReaderConfiguration<T> routeExceptionsTo(ExceptionHandler exceptionHandler) {
        if (this.exceptionHandler != null) {
            throw new IllegalArgumentException("Exception handler already set");
        }
        this.exceptionHandler = ExceptionHandler.safeHandler(exceptionHandler);
        return this;
    }
    
    /**
     * Sets the rate of scan of the expression and creates the actual {@link PVReader}
     * object that can be monitored through listeners.
     * 
     * @param rate the minimum time distance (i.e. the maximum rate) between two different notifications
     * @return the PVReader
     */
    public PVReader<T> maxRate(TimeDuration rate) {
        //int scanPeriodMs = (int) (period.getNanoSec() / 1000000);
        
        if (rate.getSec() < 0 && rate.getNanoSec() < 5000000) {
            throw new IllegalArgumentException("Current implementation limits the rate to >5ms or <200Hz (requested " + rate + "s)");
        }

        checkDataSourceAndThreadSwitch();

        // Create PVReader and connect
        PVReaderImpl<T> pv = new PVReaderImpl<T>(aggregatedPVExpression.getName(), Executors.localThread() == notificationExecutor);
        for (PVReaderListener<T> pVReaderListener : readListeners) {
            pv.addPVReaderListener(pVReaderListener);
        }
        ReadFunction<T> aggregatedFunction = aggregatedPVExpression.getFunction();
        
        PVReaderDirector<T> director = new PVReaderDirector<T>(pv, aggregatedFunction, PVManager.getReadScannerExecutorService(),
                notificationExecutor, dataSource, exceptionHandler);
        pv.setDirector(director);
        if (timeout != null) {
            if (timeoutMessage == null)
                timeoutMessage = "Read timeout";
            director.timeout(timeout, timeoutMessage);
        }
        director.connectExpression(aggregatedPVExpression);
        director.startScan(rate);

        return pv;
    }
}
