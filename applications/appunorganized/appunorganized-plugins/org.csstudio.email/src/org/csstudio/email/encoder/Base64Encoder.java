/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.email.encoder;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;

/** Encode (Buffered)Reader input as Base64 and write to PrintStream.
 *  <p>
 *  Base on code from http://www.wikihow.com/Encode-a-String-to-Base64-With-Java
 *  that did this by coying the whole input String several times, while this code
 *  handles streams, handling 3 chars at a time.
 *
 *  @author Kay Kasemir
 */
public class Base64Encoder
{
    /** Line width of output */
    final private static int LINE_WIDTH = 75;

    /** The ASCII used to encode 0...63 */
    private static final String base64code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"; //$NON-NLS-1$

    /** Where generated base64 output is written */
    final private Writer _out;

    /** Chars within a line to handle line breaks */
    private int charcount = 0;

    /** Initialize
     *  @param out Output stream for generated Bas64 text
     */
    public Base64Encoder(final Writer out)
    {
        this._out = out;
    }

    /** Encode input stream as Base64
     *  @param filename Name of file to encode
     *  @throws IOException on I/O error
     *  @throws FileNotFoundException a file not found error
     */
    public void encode(final String filename) throws FileNotFoundException, IOException
    {
        final BufferedInputStream input = new BufferedInputStream(new FileInputStream(filename));
        try
        {
            encode(input);
        }
        finally
        {
            input.close();
        }
    }

    /** Encode input stream as Base64
     *  @param input BufferedInputStream
     *  @throws IOException on I/O error
     */
    public void encode(final BufferedInputStream input) throws IOException
    {
        int pad = 0;

        // Read 3 input bytes...
        int ch1 = input.read();
        int ch2 = input.read();
        int ch3 = input.read();
        while (ch1 >= 0)
        {
            // Zero-pad missing bytes in the tiplet
            if (ch2 < 0)
            {
                ++pad;
                ch2 = 0;
            }
            if (ch3 < 0)
            {
                ++pad;
                ch3 = 0;
            }
            // Encode them as 4 ASCII bytes
            final int j = ((ch1 & 0xff) << 16)
            + ((ch2 & 0xff) << 8)
            +  (ch3 & 0xff);
            print(base64code.charAt((j >> 18) & 0x3f));
            print(base64code.charAt((j >> 12) & 0x3f));
            // .. replacing padded chars with "="
            print((pad == 2) ? '=' : base64code.charAt((j >> 6) & 0x3f));
            print((pad >= 1) ? '=' : base64code.charAt(j & 0x3f));
            ch1 = input.read();
            ch2 = input.read();
            ch3 = input.read();
        }
    }

    /** @param chr Char to append to output, handling line wraps 
     * @throws IOException */
    private void print(final char chr) throws IOException
    {
        _out.write(chr);
        if (++charcount <= LINE_WIDTH) {
            return;
        }
        _out.write("\n");
        charcount = 0;
    }
}
