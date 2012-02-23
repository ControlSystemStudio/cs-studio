/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Helper for creating a sequence of commands
 *
 *  <p>Used by the client to building a sequence of commands
 *  which is then submitted to the server for execution as a "scan".
 *
 *  <p>Allows both <code>List&lt;Command></code> and plain <code>Command[]</code>.
 *  For Java code collections like <code>List</code>
 *  are most convenient. Jython nicely converts existing collections
 *  into a python list, but it is harder to create collections
 *  from a python list, so arrays are also accepted.
 *
 *  @author Kay Kasemir
 */
public class CommandSequence
{
    final private List<ScanCommand> commands;

    /** Initialize empty command sequence
     *  to which commands can then be added.
     */
    public CommandSequence()
    {
        commands = new ArrayList<ScanCommand>();
    }

    /** Initialize with a given sequence of
     *  commands.
     *  The command sequence is then immutable.
     *  @param commands Sequence of commands
     */
    public CommandSequence(final ScanCommand[] commands)
    {
        this.commands = Arrays.asList(commands);
    }

    /** Assign consecutive addresses
     *  @param commands Commands where addresses need to be set
     */
    public static void setAddresses(final List<ScanCommand> commands)
    {
        int next = 0;
        for (ScanCommand command : commands)
            next = command.setAddress(next);
    }

    /** Add a command
     *  @param command {@link ScanCommand}
     */
    public void add(final ScanCommand command)
    {
        commands.add(command);
    }

    /** Add all commands from other sequence
     *  @param command_sequence {@link CommandSequence}
     */
    public void add(final CommandSequence command_sequence)
    {
        commands.addAll(command_sequence.commands);
    }

    /** Add a 'delay' command
     *  @param seconds Delay in seconds
     */
    public void delay(final double seconds)
    {
        add(new DelayCommand(seconds));
    }

    /** Add a 'log' command
     *  @param device_names List of device names to log
     */
    public void log(final String... device_names)
    {
        add(new LogCommand(device_names));
    }

    /** Add a 'loop' command
     *  @param device_name Name of device to update in loop
     *  @param start Initial loop value
     *  @param end Final loop value
     *  @param stepsize Loop value step size
     *  @param body Loop body
     */
    public void loop(final String device_name,
            final double start, final double end,
            final double stepsize,
            final ScanCommand... body)
    {
        add(new LoopCommand(device_name, start, end, stepsize, body));
    }

    /** Add a 'loop' command
     *  @param device_name Name of device to update in loop
     *  @param start Initial loop value
     *  @param end Final loop value
     *  @param stepsize Loop value step size
     *  @param body Loop body
     */
    public void loop(final String device_name,
            final double start, final double end,
            final double stepsize,
            final CommandSequence body)
    {
        loop(device_name, start, end, stepsize, body.getCommands());
    }

    /** Add a 'loop' command
     *  @param device_name Name of device to update in loop
     *  @param start Initial loop value
     *  @param end Final loop value
     *  @param stepsize Loop value step size
     *  @param body Loop body
     */
    public void loop(final String device_name,
            final double start, final double end,
            final double stepsize,
            final List<ScanCommand> body)
    {
        add(new LoopCommand(device_name, start, end, stepsize,
                            body.toArray(new ScanCommand[body.size()])));
    }


    /** Add a 'set' command that writes to a device
     *  @param device_name Name of device
     *  @param value Value to write to the device
     */
    public void set(final String device_name, Object value)
    {
        add(new SetCommand(device_name, value));
    }

    /** Add a 'wait' command that delays the scan until a device reaches a certain value
     *  @param device_name Name of device to check
     *  @param desired_value Desired value of the device
     *  @param tolerance Numeric tolerance when checking value
     */
    public void wait(final String device_name, final double desired_value,
         final double tolerance)
    {
        add(new WaitCommand(device_name, Comparison.EQUALS, desired_value, tolerance, 0.0));
    }

    // Note: This was called 'print' which causes warnings in a PyDev python
    //       editor because print is a reserved word in python
    /** Print current command sequence */
    public void dump()
    {
        try
        {
            XMLCommandWriter.write(System.out, getCommands());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /** @return List of commands in the sequence */
    public List<ScanCommand> getCommands()
    {
        setAddresses(commands);
        return commands;
    }

    /** @return Commands formatted as XML text
     *  @throws Exception on error
     */
    public String getXML() throws Exception
    {
        return XMLCommandWriter.toXMLString(getCommands());
    }
}
