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

import java.io.PrintStream;
import java.io.Serializable;

/** Information about a command in a scan
 *
 *  <p>Used by the client to describe commands to the server.
 *
 *  <p>Also used by the server as the base class for
 *  most command implementations.
 *  Needs to be <code>Serializable</code> to allow RMI
 *  to pass the data from the client to the server.
 *  By sharing the <code>...Command</code> classes between
 *  the server and client we avoid problems with RMI
 *  having to dynamically create the types across JVMs.
 *
 *  <p>Originally just called 'Command', but that clashed
 *  with the PyDev Command class that's used inside the
 *  PyDev shell executing in the Eclipse Command view.
 *  The easiest way around that issue seemed a rename
 *  of the basic scan command.
 *
 *  @author Kay Kasemir
 */
public interface ScanCommand extends Serializable
{
    /** Print the command (and its sub-commands)
     *  @param out {@link PrintStream}
     */
    public void print(PrintStream out);

    /** @return One-line text representation of the command */
	@Override
    public String toString();
}
