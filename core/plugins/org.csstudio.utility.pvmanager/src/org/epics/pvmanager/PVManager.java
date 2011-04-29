/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * Manages the PV creation and scanning.
 *
 * @author carcassi
 */
public class PVManager {

    private static volatile ThreadSwitch defaultOnThread = ThreadSwitch.onTimerThread();
    private static volatile DataSource defaultDataSource = null;

    /**
     * Changes the default thread on which notifications are going to be posted.
     *
     * @param threadSwitch the new target thread
     */
    public static void setDefaultThread(ThreadSwitch threadSwitch) {
        defaultOnThread = threadSwitch;
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
     * The current data source.
     * 
     * @return a data source or null if it was not set
     */
    public static DataSource getDefaultDataSource() {
        return defaultDataSource;
    }

    /**
     * Reads the given expression. Will return the average of the values collected
     * at the scan rate.
     *
     * @param <T> type of the pv value
     * @param pvExpression the expression to read
     * @return a pv manager expression
     */
    public static <T> PVManagerExpression<T> read(SourceRateExpression<T> pvExpression) {
        return new PVManagerExpression<T>(ExpressionLanguage.latestValueOf(pvExpression));
    }

    /**
     * Reads the given expression.
     *
     * @param <T> type of the pv value
     * @param pvExpression the expression to read
     * @return a pv manager expression
     */
    public static <T> PVManagerExpression<T> read(DesiredRateExpression<T> pvExpression) {
        return new PVManagerExpression<T>(pvExpression);
    }

    /**
     * An expression used to set the final parameters on how the pv expression
     * should be monitored.
     * @param <T> the type of the expression
     */
    public static class PVManagerExpression<T>  {

        private DesiredRateExpression<T> aggregatedPVExpression;
        private ExceptionHandler exceptionHandler;
        // Initialize to defaults
        private ThreadSwitch onThread;
        private DataSource source;

        private PVManagerExpression(DesiredRateExpression<T> aggregatedPVExpression) {
            this.aggregatedPVExpression = aggregatedPVExpression;
        }

        /**
         * Forwards exception to the given exception handler. No thread switch
         * is done, so the handler is notified on the thread where the exception
         * was thrown.
         * <p>
         * Giving a custom exception handler will disable the default handler,
         * so {@link PV#lastException() } is no longer set and no notification
         * is done.
         *
         * @param exceptionHandler an exception handler
         * @return this
         */
        public PVManagerExpression<T> routeExceptionsTo(ExceptionHandler exceptionHandler) {
            if (this.exceptionHandler != null)
                throw new IllegalArgumentException("Exception handler already set");
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        /**
         * Defines which DataSource should be used to read the data.
         *
         * @param dataSource a connection manager
         * @return this
         */
        public PVManagerExpression<T> from(DataSource dataSource) {
            if (dataSource == null)
                throw new IllegalArgumentException("dataSource can't be null");
            source = dataSource;
            return this;
        }

        /**
         * Defines on which thread the PVManager should notify the client.
         *
         * @param onThread the thread on which to notify
         * @return this
         */
        public PVManagerExpression<T> andNotify(ThreadSwitch onThread) {
            if (this.onThread == null)  {
                this.onThread = onThread;
            } else {
                throw new IllegalStateException("Already set what thread to notify");
            }
            return this;
        }

        /**
         * Sets the rate of scan of the expression and creates the actual {@link PV}
         * object that can be monitored through listeners.
         * @param rate rate in Hz; should be between 1 and 50
         * @return the PV
         */
        public PV<T> atHz(double rate) {
            long scanPeriodMs = (long) (1000.0 * (1.0 / rate));

            // Get defaults
            if (source == null)
                source = defaultDataSource;
            if (onThread == null)
                onThread = defaultOnThread;

            // Check that a data source has been specified
            if (source == null) {
                throw new IllegalStateException("You need to specify a source either " +
                        "using PVManager.setDefaultDataSource or by using " +
                        "read(...).from(dataSource).");
            }

            // Check that thread switch has been specified
            if (onThread == null) {
                throw new IllegalStateException("You need to specify a thread either " +
                        "using PVManager.setDefaultThreadSwitch or by using " +
                        "read(...).andNotify(threadSwitch).");
            }

            // Create PV and connect
            PV<T> pv = PV.createPv(aggregatedPVExpression.getDefaultName());
            DataRecipe dataRecipe = aggregatedPVExpression.getDataRecipe();
            if (exceptionHandler == null) {
                dataRecipe = dataRecipe.withExceptionHandler(new DefaultExceptionHandler(pv, onThread));
            } else {
                dataRecipe = dataRecipe.withExceptionHandler(exceptionHandler);
            }
            Function<T> aggregatedFunction = aggregatedPVExpression.getFunction();
            Notifier<T> notifier = new Notifier<T>(pv, aggregatedFunction, onThread, dataRecipe.getExceptionHandler());
            Scanner.scan(notifier, scanPeriodMs);
            try {
                source.connect(dataRecipe);
            } catch (RuntimeException ex) {
                dataRecipe.getExceptionHandler().handleException(ex);
            }
            PVRecipe recipe = new PVRecipe(dataRecipe, source, notifier);
            notifier.setPvRecipe(recipe);
            return pv;
        }
    }
}
