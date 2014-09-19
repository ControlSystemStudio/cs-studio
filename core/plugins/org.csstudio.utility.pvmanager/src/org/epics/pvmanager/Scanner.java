/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager;

/**
 * Implements the strategy for event generation and event response.
 * <p>
 * An event should be always triggered in the following cases:
 * <ul>
 *   <li>If the expression changes even without external events (e.g. time average),
 *       then the calculation needs to happen at the scan rate</li>
 *   <li>Every time a channel connection or value changes</li>
 *   <li>Every time the expression dynamically changes (e.g. dynamic map, indirect pv)</li>
 * <ul>
 *
 * @author carcassi
 */
interface Scanner {
    
    /**
     * Starts the scanning. From this moment on, the pv may get notified.
     */
    void start();
    
    /**
     * Pause the scanning. Events will be collected and delayed until a resume.
     */
    void pause();
    
    /**
     * Resumes the scanning. If events were collected during the pause,
     * they will be sent right away.
     */
    void resume();
    
    /**
     * Stops the scanning. From this moment on, the pv will no longer be
     * notified. Can't be restarted.
     */
    void stop();
    
    /**
     * Called when a new notification reaches a collector.
     */
    void collectorChange();
    
    /**
     * Called after a pv is notified.
     */
    void notifiedPv();
}
