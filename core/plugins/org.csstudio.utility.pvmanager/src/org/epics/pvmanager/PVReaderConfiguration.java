/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import org.epics.pvmanager.expression.DesiredRateExpression;
import java.util.concurrent.Executor;
import org.epics.pvmanager.util.Executors;
import org.epics.util.time.TimeDuration;

/**
 * An expression used to set the final parameters on how the pv expression
 * should be monitored.
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

    @Override
    public PVReaderConfiguration<T> timeout(TimeDuration timeout) {
        super.timeout(timeout);
        return this;
    }

    @Override
    public PVReaderConfiguration<T> timeout(TimeDuration timeout, String timeoutMessage) {
        super.timeout(timeout, timeoutMessage);
        return this;
    }

    @Override
    @Deprecated
    public PVReaderConfiguration<T> timeout(org.epics.pvmanager.util.TimeDuration timeout) {
        super.timeout(timeout);
        return this;
    }

    @Override
    @Deprecated
    public PVReaderConfiguration<T> timeout(org.epics.pvmanager.util.TimeDuration timeout, String timeoutMessage) {
        super.timeout(timeout, timeoutMessage);
        return this;
    }
    
    
    
    private DesiredRateExpression<T> aggregatedPVExpression;
    private ExceptionHandler exceptionHandler;

    PVReaderConfiguration(DesiredRateExpression<T> aggregatedPVExpression) {
        this.aggregatedPVExpression = aggregatedPVExpression;
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
        DataRecipe dataRecipe = aggregatedPVExpression.getDataRecipe();
        if (exceptionHandler == null) {
            dataRecipe = dataRecipe.withExceptionHandler(ExceptionHandler.createDefaultExceptionHanderl(pv, notificationExecutor));
        } else {
            dataRecipe = dataRecipe.withExceptionHandler(exceptionHandler);
        }
        Function<T> aggregatedFunction = aggregatedPVExpression.getFunction();
        Function<Boolean> connFunction = new LastValueAggregator<Boolean>(dataRecipe.getConnectionCollector());
        Notifier<T> notifier = new Notifier<T>(pv, aggregatedFunction, connFunction, PVManager.getReadScannerExecutorService(), notificationExecutor, dataRecipe.getExceptionHandler());
        notifier.startScan(rate);
        if (timeout != null) {
            if (timeoutMessage == null)
                timeoutMessage = "Read timeout";
            notifier.timeout(timeout, timeoutMessage);
        }
        try {
            source.connect(dataRecipe);
        } catch (RuntimeException ex) {
            dataRecipe.getExceptionHandler().handleException(ex);
        }
        PVRecipe recipe = new PVRecipe(dataRecipe, source, notifier);
        notifier.setPvRecipe(recipe);
        return pv;
    }

    /**
     * Sets the rate of scan of the expression and creates the actual {@link PVReader}
     * object that can be monitored through listeners.
     * 
     * @param period the minimum time distance (i.e. the maximum rate) at which notifications should be sent
     * @return the PVReader
     */
    @Deprecated
    public PVReader<T> every(org.epics.pvmanager.util.TimeDuration period) {
        return maxRate(org.epics.pvmanager.util.TimeDuration.asTimeDuration(period));
    }
}
