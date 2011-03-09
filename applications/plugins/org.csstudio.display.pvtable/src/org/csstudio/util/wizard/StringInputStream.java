/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
