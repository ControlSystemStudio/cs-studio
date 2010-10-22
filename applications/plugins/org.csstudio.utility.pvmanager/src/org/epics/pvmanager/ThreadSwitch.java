/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Embeds the logic to post events on a separate thread so that PVManager
 * can appropriately redirect the notifications.
 *
 * @author carcassi
 */
public abstract class ThreadSwitch {

    private static final Logger log = Logger.getLogger(ThreadSwitch.class.getName());

    /**
     * Tells the PV manager to notify on the Swing Event Dispatch Thread using
     * SwingUtilities.invokeLater().
     * @return an object that posts events on the EDT
     */
    public static ThreadSwitch onSwingEDT() {
        return ThreadSwitch.SWING;
    }

    /**
     * Tells the PV manager to notify on the timer thread.
     * @return an object that runs tasks on the timer thread
     */
    public static ThreadSwitch onTimerThread() {
        return ThreadSwitch.TIMER;
    }

    /**
     * Post the given task to the notification thread.
     *
     * @param run a new task
     */
    public abstract void post(Runnable run);

    private static ThreadSwitch SWING = new ThreadSwitch() {

        @Override
        public void post(Runnable task) {
            SwingUtilities.invokeLater(task);
        }
    };

    private static ThreadSwitch TIMER = new ThreadSwitch() {

        @Override
        public void post(Runnable task) {
            try {
                task.run();
            } catch (Exception ex) {
                log.log(Level.WARNING, "Exception on the timer thread caused by a ValueListener", ex);
            } catch (AssertionError ex) {
                log.log(Level.WARNING, "Assertion failed on the timer thread", ex);
            }
        }
    };

}
