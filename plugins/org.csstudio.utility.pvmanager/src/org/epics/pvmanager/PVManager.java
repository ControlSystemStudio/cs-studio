/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateReadWriteExpression;
import org.epics.pvmanager.expression.SourceRateExpression;
import org.epics.pvmanager.expression.SourceRateReadWriteExpression;
import org.epics.pvmanager.expression.WriteExpression;

/**
 * Entry point for the library, manages the defaults and allows to create
 * {@link PVReader}, {@link PVWriter} and {@link PV } from an read or write expression.
 * <p>
 * <b>NotificationExecutor</b> - This is used for all notifications.
 * By default this uses {@link org.epics.pvmanager.util.Executors#localThread()} so that
 * the notification are done on whatever current thread needs to notify.
 * This means that new read notifications are run on threads managed by
 * the ReadScannerExecutorService, write notifications are run on threads
 * managed by the DataSource and exceptions notification are run on the thread
 * where the exception is done. This can be changed to make all notifications
 * routed to single threaded sub-systems, such as UI environments like SWING,
 * SWT or similar. This can be changed on a PV by PV basis.
 * <p>
 * <b>AsynchWriteExecutor</b> - This is used for asynchronous writes, to return
 * right away, and for running timeouts on each write.
 * By default this uses the internal PVManager work pool. The work
 * submitted here is the calculation of the corresponding {@link WriteExpression}
 * and submission to the {@link DataSource}. The DataSource itself typically
 * has asynchronous work, which is executed in the DataSource specific threads.
 * Changing this to {@link org.epics.pvmanager.util.Executors#localThread()} will make that preparation
 * task on the thread that calls {@link PVWriter#write(java.lang.Object) } but
 * it will not transform the call in a synchronous call.
 * <p>
 * <b>ReadScannerExecutorService</b> - This is used to run the periodic
 * scan for new values. By default this uses the internal PVManager work pool. The work
 * submitted here is the calculation of the corresponding {@link DesiredRateExpression}
 * and submission to the NotificationExecutor.
 *
 * @author carcassi
 */
public class PVManager {

    private static volatile Executor defaultNotificationExecutor = org.epics.pvmanager.util.Executors.localThread();
    private static volatile DataSource defaultDataSource = null;
    private static final ScheduledExecutorService workerPool = Executors.newScheduledThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors() - 1),
            org.epics.pvmanager.util.Executors.namedPool("PVMgr Worker "));
    private static ScheduledExecutorService readScannerExecutorService = workerPool;
    private static ScheduledExecutorService asyncWriteExecutor = workerPool;

    /**
     * Changes the default executor on which all notifications are going to be posted.
     *
     * @param notificationExecutor the new notification executor
     */
    public static void setDefaultNotificationExecutor(Executor notificationExecutor) {
        defaultNotificationExecutor = notificationExecutor;
    }

    /**
     * Returns the current default executor that will execute all notifications.
     * 
     * @return the default executor
     */
    public static Executor getDefaultNotificationExecutor() {
        return defaultNotificationExecutor;
    }

    /**
     * Changes the default source for data.
     *
     * @param dataSource the data source
     */
    public static void setDefaultDataSource(DataSource dataSource) {
        PVManager.defaultDataSource = dataSource;
    }

    /**
     * Returns the current default data source.
     * 
     * @return a data source or null if it was not set
     */
    public static DataSource getDefaultDataSource() {
        return defaultDataSource;
    }

    /**
     * Reads the given expression, and returns an object to configure the parameters
     * for the read. At each notification it will return the latest value,
     * even if more had been received from the last notification.
     *
     * @param <T> type of the read payload
     * @param pvExpression the expression to read
     * @return the read configuration
     */
    public static <T> PVReaderConfiguration<T> read(SourceRateExpression<T> pvExpression) {
        return new PVReaderConfiguration<T>(ExpressionLanguage.latestValueOf(pvExpression));
    }

    /**
     * Reads the given expression, and returns an object to configure the parameters
     * for the read.
     *
     * @param <T> type of the read payload
     * @param pvExpression the expression to read
     * @return the read configuration
     */
    public static <T> PVReaderConfiguration<T> read(DesiredRateExpression<T> pvExpression) {
        return new PVReaderConfiguration<T>(pvExpression);
    }
    
    /**
     * Writes the given expression, and returns an object to configure the parameters
     * for the write.
     *
     * @param <T> type of the write payload
     * @param writeExpression the expression to write
     * @return the write configuration
     */
    public static <T> PVWriterConfiguration<T> write(WriteExpression<T> writeExpression) {
        return new PVWriterConfiguration<T>(writeExpression);
    }
    
    /**
     * Both reads and writes the given expression, and returns an object to configure the parameters
     * for the both read and write. It's similar to use both {@link #read(org.epics.pvmanager.expression.SourceRateExpression) }
     * and {@link #write(org.epics.pvmanager.expression.WriteExpression) } at the same time.
     *
     * @param <R> type of the read payload
     * @param <W> type of the write payload
     * @param readWriteExpression the expression to read and write
     * @return the read and write configuration
     */
    public static <R, W> PVConfiguration<R, W> readAndWrite(SourceRateReadWriteExpression<R, W> readWriteExpression) {
        return readAndWrite(ExpressionLanguage.latestValueOf(readWriteExpression));
    }
    
    /**
     * Both reads and writes the given expression, and returns an object to configure the parameters
     * for the both read and write. It's similar to use both {@link #read(org.epics.pvmanager.expression.SourceRateExpression) }
     * and {@link #write(org.epics.pvmanager.expression.WriteExpression) } at the same time.
     *
     * @param <R> type of the read payload
     * @param <W> type of the write payload
     * @param readWriteExpression the expression to read and write
     * @return the read and write configuration
     */
    public static <R, W> PVConfiguration<R, W> readAndWrite(DesiredRateReadWriteExpression<R, W> readWriteExpression) {
        return new PVConfiguration<R, W>(readWriteExpression);
    }

    /**
     * Returns the current executor on which the asynchronous calls are executed.
     * 
     * @return the current executor
     */
    public static ScheduledExecutorService getAsyncWriteExecutor() {
        return asyncWriteExecutor;
    }

    /**
     * Changes the executor used for the asynchronous write calls.
     * 
     * @param asyncWriteExecutor the new executor
     */
    public static void setAsyncWriteExecutor(ScheduledExecutorService asyncWriteExecutor) {
        PVManager.asyncWriteExecutor = asyncWriteExecutor;
    }

    /**
     * Returns the executor service used to schedule and run the 
     * periodic reading scan for new values.
     * 
     * @return the service for the read operations
     */
    public static ScheduledExecutorService getReadScannerExecutorService() {
        return readScannerExecutorService;
    }

    /**
     * Changes the executor service to use for executing the periodic read
     * scan.
     * 
     * @param readScannerExecutorService  the new service for the read operations
     */
    public static void setReadScannerExecutorService(ScheduledExecutorService readScannerExecutorService) {
        PVManager.readScannerExecutorService = readScannerExecutorService;
    }
    
}
