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
     * TODO Test overall performance, find bugs
     * 
     * TODO Alarm Tree on Linux has problem: When adding new entries
     * at bottom, the tree widget doesn't "grow" as needed,
     * so the new items are not accessible.
     * 
     * TODO Outside of alarms, but anyway: 'Rename' in navigator doesn't work on OS X.
     *      Debugging RenameResourceAction shows that it tries to to an
     *      'inline' rename, but at least on OS X that doesn't seem to work.
     *      Fine on Linux, so why bother?
     * 
     * TODO Sounds beyond annunciation of new, annunicated alarms?
     * Have each GUI beep (Display.getDefault().beep()) for new alarms,
     * independent of annunciation?
     * Or leave that all to the server and the JMS TALK queue?
     * 
     * Annunciate more than once, until acknowledged?
     * Careful: First priority should be to _reduce_ the noise, not add to it
     * 
     * Periodically annunciate "There are N active alarms" as a reminder?
     * 
     * TODO Support for action when alarm not ack'ed within some time.
     * Several Ideas:
     * 1) Check in alarm server, send 'PANIC' message to JMS. Then add JMS
     * listener that sends SMS message (how?where?) in response to 'PANIC'. Or
     * JMS listener that performs control system action (emergency shutdown) in
     * response to 'PANIC'.
     * Pro: Handled in server even if no GUI runs.
     * Contra: Needs separate tool
     * 2) Handle in server, and server directly executes some specially marked
     * commands.
     * Pro: Handled in server even if no GUI runs.
     * Contra: Now server needs to know about commands and execute external
     * programs. Better keep the server simple & stable.
     * 3) Check in GUI: Periodically check the duration of active alarms.
     * Those that are appropriately configured can then blink or run commands
     * automatically.
     * Pro: GUI has all the info, already runs commands.
     * Contra: Requires GUI to run.
     * 4) Separate RDB tool, using idea of JMS 'stale' report, runs periodically
     * as service/daemon, checks active alarms, runs commands.
     * Pro: No changes to server nor GUI. Very flexible 'tool' approach.
     * Contra: Need to check if that tool is running. How to configure?
     * 
     * TODO Use "message" instead of "status" in SQL?
     *      Or go back to "status" everywhere, since it's too late now?
     * 
     * TODO Support "Shelving" of alarms?
     *      Replace "disable" by "shelf"?
     *      Use JMS report of disabled alarms, sorted by date,
     *      to see what should be re-enabled or re-engineered?
     * 
     * TODO AlarmTable and maybe also -Tree need "freeze" option: When many new
     * alarms arrive, operators may want to 'freeze' the alarm display. Idea:
     * Ignore JMS inputs (queue them up, similar to what's done during startup,
     * but still allow 'acknowledge'). Needs clear optical indication so that
     * it's not left frozen.
     * 
     * TODO Re-think command execution.
     * Waits a little while for success or error, then leaves
     * long running command alone.
     * Should errors go to console instead of dialog?
     * Should also display later errors?
     * Should all output of command go to console?
     * Most of that means: CommandExecutor needs to run until command
     * finishes. What if e.g. EDM was launched and runs for days?
     * 
     * TODO Configurable Alarm Server annunciation level: For example,
     * annunciate only 'MAJOR' & 'INVALID' alarms, suppress 'MINOR'. Only offer
     * suppression for 'minor'? With automatic timeout?
     * 
     * TODO Support client-side filters for "Vacuum" or "RF" users.
     * a. Use filter on path name like /%/Vacuum/%?
     * b. On PV name?
     * c. Add additional grouping to alarm tree hierarchy?
     * Leaning towards a.
     * 
     * TODO Show limits (or other PV config)?
     * List configuration by subsystem with limits (Maybe better
     * implemented in JSP) This allows experts to review current setup.
     * 
     * Try to show current limits when configuring alarm: ConfigDialog
     * should connect to live PV and display limits.
     * Sounds great, but not really possible:
     * Will only work for fully configured analog PVs.
     * 
     * I think there's no good way to do that. CSS, however, does allow
     * to get the PV into Probe or EPICS PV Tree or PV Utility and that
     * way those in the know have some help in figuring out what's happening
     * under the hood.
     * 
     * Otherwise, link to "rationalization" info (wiki) as related display?
     */
}
