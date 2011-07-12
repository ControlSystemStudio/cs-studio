/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.concurrent.Executor;

/**
 *
 * @author carcassi
 */
class DefaultExceptionHandler extends ExceptionHandler {

    private final PV<?> pv;
    private final Executor notificationExecutor;

    DefaultExceptionHandler(PV<?> pv, Executor notificationExecutor) {
        this.pv = pv;
        this.notificationExecutor = notificationExecutor;
    }

    @Override
    public void handleException(final Exception ex) {
        notificationExecutor.execute(new Runnable() {

            @Override
            public void run() {
                pv.setLastException(ex);
                pv.firePvValueChanged();
            }
        });
    }

}
