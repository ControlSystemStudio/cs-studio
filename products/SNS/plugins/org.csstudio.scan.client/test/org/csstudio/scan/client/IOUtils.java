/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/** I/O utils, using some method names from Apache commons-io
 *  in case that's used later.
 *  @author Kay Kasemir
 */
public class IOUtils
{
    /** Read text from stream
     *  @param stream {@link InputStream} to read
     *  @return Text read from the stream
     *  @throws Exception on error
     */
    public static String toString(final InputStream stream) throws Exception
    {
        return new String(toByteArray(stream));
    }

    /** Read bytes from stream
     *  @param stream {@link InputStream} to read
     *  @return bytes read from stream
     *  @throws Exception on error
     */
    private static byte[] toByteArray(final InputStream stream) throws Exception
    {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final byte[] buf = new byte[16384];

        int count = stream.read(buf);
        while (count != -1)
        {
            result.write(buf, 0, count);
            count = stream.read(buf);
        }
        result.flush();
        stream.close();
        
        return result.toByteArray();
    }
}
