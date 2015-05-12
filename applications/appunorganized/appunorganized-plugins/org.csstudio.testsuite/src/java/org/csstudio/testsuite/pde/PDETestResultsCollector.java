/*
 * Copyright © 2008, Brian Joyce
 * By Brian Joyce, Duolog Technologies Ltd., Galway, Ireland
 * June 13, 2008
 *
 * http://www.eclipse.org/articles/Article-PDEJUnitAntAutomation/index.html#PDETestListener
 */
package org.csstudio.testsuite.pde;



import org.eclipse.jdt.internal.junit.model.ITestRunListener2;
import org.eclipse.jdt.internal.junit.model.RemoteTestRunnerClient;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 16.06.2011
 */
//CHECKSTYLE OFF: |
@SuppressWarnings("all")
public final class PDETestResultsCollector {
    //CHECKSTYLE:OFF
    private PDETestListener _listener;

    private final String _suiteName;

    private PDETestResultsCollector(final String suite) {
        _suiteName = suite;
    }

    private void run(final int port) throws InterruptedException {
        _listener = new PDETestListener(this, _suiteName);
        new RemoteTestRunnerClient().startListening(new ITestRunListener2[] {_listener}, port);
        System.out.println("Listening on port " + port + " for test suite " + _suiteName + " results ...");
        synchronized (this) {
            wait();
        }
    }

    private PDETestListener getListener() {
        return _listener;
    }


    public static void main(final String[] args) {
        if (args.length != 2) {
            System.out.println("usage: PDETestResultsCollector <test suite name> <port number>");
            System.exit(0);
        }

        PDETestResultsCollector collector = null;
        PDETestListener listener = null;
        try {
            collector = new PDETestResultsCollector(args[0]);
            listener = collector.getListener();

            collector.run(Integer.parseInt(args[1]));

        } catch (final Throwable th) {
            th.printStackTrace();
        }

        if (listener != null && listener.failed()) {
            System.exit(1);
        }
    }
}
//CHECKSTYLE ON: |
