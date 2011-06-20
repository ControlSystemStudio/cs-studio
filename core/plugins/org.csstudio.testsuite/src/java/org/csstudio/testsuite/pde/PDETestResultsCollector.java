/*
 * Copyright © 2008, Brian Joyce
 * By Brian Joyce, Duolog Technologies Ltd., Galway, Ireland
 * June 13, 2008
 * 
 * http://www.eclipse.org/articles/Article-PDEJUnitAntAutomation/index.html#PDETestListener
 */
package org.csstudio.testsuite.pde;


import javax.annotation.Nonnull;

import org.eclipse.jdt.internal.junit.model.ITestRunListener2;
import org.eclipse.jdt.internal.junit.model.RemoteTestRunnerClient;

/**
 * TODO (bknerr) : 
 * 
 * @author bknerr
 * @since 16.06.2011
 */
@SuppressWarnings("restriction")
public final class PDETestResultsCollector {
    
    // TODO (bknerr) : static ??? doesnt make sense
    private static PDETestListener PDE_TEST_LISTENER;

    private String _suiteName;

    private PDETestResultsCollector(@Nonnull final String suite) {
        _suiteName = suite;
    }

    private void run(int port) throws InterruptedException {
        PDE_TEST_LISTENER = new PDETestListener(this, _suiteName);
        new RemoteTestRunnerClient().startListening(new ITestRunListener2[] {PDE_TEST_LISTENER}, port);
        System.out.println("Listening on port " + port + " for test suite " + _suiteName + " results ...");
        synchronized (this) {
            wait();
        }
    }

    public static void main(@Nonnull final String[] args) {
        if (args.length != 2) {
            System.out.println("usage: PDETestResultsCollector <test suite name> <port number>");
            System.exit(0);
        }

        try {
            new PDETestResultsCollector(args[0]).run(Integer.parseInt(args[1]));
        } catch (Throwable th) {
            th.printStackTrace();
        }

        if (PDE_TEST_LISTENER != null && PDE_TEST_LISTENER.failed()) {
            System.exit(1);
        }
    }
}

