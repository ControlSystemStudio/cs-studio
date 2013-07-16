/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server.httpd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Helper for streams
 *  @author Kay Kasemir
 */
public class StreamHelper
{
    /** Copy from one stream to another
     *  @param input Where to read
     *  @param output Where to write
     *  @throws IOException on error
     */
    public static void copy(final InputStream input,
            final OutputStream output) throws IOException
    {
        final byte[] buf = new byte[2048];
        int len;
        while ((len = input.read(buf)) >= 0)
            output.write(buf, 0, len);
    }
}
