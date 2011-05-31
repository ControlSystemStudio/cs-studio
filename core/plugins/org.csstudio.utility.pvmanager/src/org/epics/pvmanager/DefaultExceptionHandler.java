/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

/**
 *
 * @author carcassi
 */
class DefaultExceptionHandler extends ExceptionHandler {

    private final PV<?> pv;
    private final ThreadSwitch threadSwitch;

    DefaultExceptionHandler(PV<?> pv, ThreadSwitch threadSwitch) {
        this.pv = pv;
        this.threadSwitch = threadSwitch;
    }

    @Override
    public void handleException(final Exception ex) {
        threadSwitch.post(new Runnable() {

            @Override
            public void run() {
                pv.setLastException(ex);
                pv.firePvValueChanged();
            }
        });
    }

}
