package org.csstudio.util.wizard;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/** I can never remember how to get from a String to an InputStream. 
 *  <p>
 *  <code>StringBufferInputStream</code> looked OK, but it deprecated.
 *  @author Kay Kasemir
 */
public class StringInputStream extends InputStream
{
    private final StringReader reader;
    
    StringInputStream(final String text)
    {
        reader = new StringReader(text);
    }

    /* @see java.io.InputStream#read() */
    @Override
    public int read() throws IOException
    {
        return reader.read();
    }
}
