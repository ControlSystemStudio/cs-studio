/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Embeds the logic to post events on a separate thread so that PVManager
 * can appropriately redirect the notifications.
 * 
 * TODO: should probably use the Executor interface
 *
 * @author carcassi
 */
public abstract class ThreadSwitch {

    private static final Logger log = Logger.getLogger(ThreadSwitch.class.getName());

    /**
     * Executes tasks on the Swing Event Dispatch Thread using
     * SwingUtilities.invokeLater().
     * 
     * @return an executor that posts events on the EDT
     */
    public static Executor onSwingEDT() {
        return ThreadSwitch.SWING_EXECUTOR;
    }

    /**
     * Executes tasks on the current thread.
     * 
     * @return an object that runs tasks on the current thread
     */
    public static Executor onDefaultThread() {
        return ThreadSwitch.CURRENT_EXECUTOR;
    }

    private static Executor SWING_EXECUTOR = new Executor() {

        @Override
        public void execute(Runnable command) {
            SwingUtilities.invokeLater(command);
        }
    };

    private static Executor CURRENT_EXECUTOR = new Executor() {

        @Override
        public void execute(Runnable command) {
            try {
                command.run();
            } catch (Exception ex) {
                log.log(Level.WARNING, "Exception on the timer thread caused by a ValueListener", ex);
            } catch (AssertionError ex) {
                log.log(Level.WARNING, "Assertion failed on the timer thread", ex);
            }
        }
    };

}
