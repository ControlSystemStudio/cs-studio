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

import org.csstudio.scan.server.ScanServer;

/** Base for commands that supports XML read/write.
 * 
 *  @see XMLCommandReader for additional requirements
 * 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public abstract class BaseCommand implements ScanCommand
{
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    /** Write the command (and its sub-commands) in an XML format.
     * 
     *  <p>A command called AbcCommand should write itself as a tag "abc"
     *  so that the {@link XMLCommandReader} can later determine
     *  which class to use for reading the command back from XML.
     *  
     *  @param out {@link PrintStream}
     *  @param level Indentation level
     */
    abstract public void writeXML(PrintStream out, final int level);

    /** Write indentation
     *  @param out Where to print
     *  @param level Indentation level
     */
    protected void writeIndent(final PrintStream out, final int level)
    {
        for (int i=0; i<level; ++i)
            out.print("  ");
    }
}
