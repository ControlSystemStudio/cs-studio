/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

/** Placeholder for to-do items
 *
 *  @author Kay Kasemir
 */
public class TODO
{
    /**
     * ======== Misc Issues
     *
     * TODO Alarm Tree on Linux has problem: When adding new entries
     * at bottom, the tree widget doesn't "grow" as needed,
     * so the new items are not accessible.
     *
     * ======== Support for multiple alarm setups
     *
     * Idea: Secondary control rooms, not always manned, have their own alarm setup.
     * Could also be used to simply split the alarm configuration into "plant subsystems".
     * These are individual setups with their own config, alarm server, GUI.
     * Usually an operator looks at them.
     * But not always.
     * In that case, if there are alarms un-acknowledged for some time,
     * send alarms to a "main" control room.
     *
     * Done: (New) "Global" Model; Global Alarm Table displays these, fetching guidance etc. in background thread
     *
     * TODO Alarm RDB includes PV.GLOBAL_DELAY (INT) for finer configuration
     *
     * TODO Alarm Server uses PV.GLOBAL_DELAY instead of command-line switch
     *
     * Done: Alarm Server updates PV.GLOBAL_ALARM to persist state
     *
     * Done: Global Alarm Table initializes properly:
     *      1) Subscribe to GLOBAL_ALARMSERVER, buffering messages
     *      2) Read all PV with GLOBAL_ALARM set
     *      3) Apply updates from buffered JMS info
     *
     * TODO Alarm Tree supports editing of ALARM_TREE.GLOBAL_DELAY
     *      (Could be done earlier)
     *
     * TODO Combine Global Alarm Table with original Alarm Table
     *      (Nice; not essential)
     *
     * TODO Phone Dialer monitors GLOBAL_ALARMSERVER
     *      Should work for any *_ALARMSERVER, but then immediate, not using GLOBAL_DELAY
     */
}
