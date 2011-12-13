/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.command;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

/** Persist {@link ScanCommand}s as XML to stream.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLCommandWriter
{
    final private PrintStream out;

    /** Initialize
     *  @param out Where to write the commands
     */
    public XMLCommandWriter(final OutputStream out)
    {
        this.out = new PrintStream(new BufferedOutputStream(out));
    }

    /** @param commands Commands to write as XML to output stream 
     *  @throws Exception on error
     */
    public void writeXML(final List<ScanCommand> commands) throws Exception
    {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<commands>");
        for (ScanCommand command : commands)
            command.writeXML(out, 1);
        out.println("</commands>");
        out.flush();
    }
}
