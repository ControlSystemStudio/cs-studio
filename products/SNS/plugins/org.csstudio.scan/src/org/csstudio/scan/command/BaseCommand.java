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

/** Base for a command that prints itself
 *  with indentation levels
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BaseCommand implements ScanCommand
{
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    /** {@inheritDoc} */
    @Override
    public void dump(final PrintStream out)
    {
        printIndented(out, 0);
    }

    /** Print command with indentation
     *  @param out Where to print
     *  @param level Indentation level
     */
    protected void printIndented(final PrintStream out, final int level)
    {
        for (int i=0; i<level; ++i)
            out.print("  ");
        out.println(toString());
    }
}
