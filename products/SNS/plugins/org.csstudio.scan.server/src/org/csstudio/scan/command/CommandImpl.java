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

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.server.Scan;
import org.csstudio.scan.server.ScanContext;

/** A command that can be executed
 *
 *  <p>Most commands will perform one unit of work,
 *  for example set a PV.
 *  A loop on the other hand will perform one unit of work per loop
 *  iteration.
 *
 *  <p>The {@link Scan} queries each command for the number of work
 *  units that it will perform, and the command must then update
 *  the {@link ScanContext} with the number of performed work
 *  units.
 *
 *  <p>Work units are just a guess to provide a progress indication.
 *  If a command cannot estimate the number of work units
 *  because it will dynamically perform more or less work,
 *  it should return 1.
 *  Likewise, a delay of 1 second or 1 minute would each be one
 *  work unit, even though their duration differs a lot.
 *  
 *  <p>Most commands are implemented by extending the underlying
 *  {@link ScanCommand} that describes the command, simply
 *  providing the <code>execute()</code> method to perform
 *  the actual work.
 *  This, however, is not as easy for commands that themselve hold
 *  a 'body' of commands, for example the loop:
 *  The basic LoopCommand has a body of scan commands, but the 
 *  implementation needs a body of executable commands.
 *   
 *  @author Kay Kasemir
 */
public interface CommandImpl extends ScanCommand
{
    /** Most commands will perform one unit of work,
     *  for example set a PV.
     *  A loop on the other hand will perform one unit of work per loop
     *  iteration.
     *
     *  @return Number of work units that this command performs */
    public int getWorkUnits();

	/** Execute the command
	 *
	 *  <p>Should update the performed work units on the {@link ScanContext}
	 *
	 *  @param context {@link ScanContext}
	 *  @throws Exception on error
	 *
	 *  @see ScanContext#workPerformed(int)
	 */
    public void execute(ScanContext context) throws Exception;
}
