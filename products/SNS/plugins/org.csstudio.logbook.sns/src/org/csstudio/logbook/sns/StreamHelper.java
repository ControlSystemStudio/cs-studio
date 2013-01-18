/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Stream helper
 *  @author Kay Kasemir
 */
public class StreamHelper
{
    /** Copy one stream into another
     *  @param in Stream to read
     *  @param out Stream to write
     *  @throws IOException on error
     */
    public static void copy(final InputStream in, final OutputStream out) throws IOException
    {
        final byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) >= 0)
            out.write(buf, 0, len);
    }
}
