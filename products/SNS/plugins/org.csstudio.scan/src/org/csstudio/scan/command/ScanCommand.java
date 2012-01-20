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

import org.csstudio.scan.server.ScanServer;

/** Description of a scan server command
 * 
 *  <p>Used by the client to describe commands to the server.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class ScanCommand implements Serializable
{
    // TODO Transfer commands as XML so Serializable is no longer necessary
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    // TODO public ScanCommandProperty[] getProperties();
    
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

    // TODO public static ScanCommand fromXML(final Element element) throws Exception
    // But can't be static
    
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
