/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carcassi
 */
class Scanner {

    private static final Logger log = Logger.getLogger(Scanner.class.getName());
    private static Timer timer = new Timer("PV Monitor Scanner", true);

    static void scan(final Notifier notifier, long periodInMs) {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (notifier.isActive()) {
                    notifier.notifyPv();
                } else {
                    cancel();
                    timer.purge();
                    log.log(Level.FINE, "Stopped scanning {0}", notifier);
                }
            }
        }, 0, periodInMs);
        log.log(Level.FINE, "Scanning {0} every {1} ms", new Object[]{notifier, periodInMs});
    }
}
