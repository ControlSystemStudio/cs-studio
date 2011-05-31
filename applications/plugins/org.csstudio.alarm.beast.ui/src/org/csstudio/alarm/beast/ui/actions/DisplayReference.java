/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import org.csstudio.java.string.StringSplitter;

/** Helper for parsing a display reference
 *  <p>
 *  A display reference is
 *  <code>'filename'</code> or
 *  <code>'filename data'</code>.
 *  <p>
 *  Accepted as display reference file names:
 *  <ul>
 *  <li>Absolute path (within work space): <code>/some/path/file.abc</code>
 *  <li>Relative path: <code>../some/path/file.abc</code>
 *  <li>Specifically marked as operator interface file: <code>opi:../some/path/file.abc</code>
 *  </ul>
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DisplayReference
{
    final private boolean is_valid;

    private String filename, data;

    /** Initialize
     *  @param display_reference Text to parse
     *  @throws Exception on internal parser error
     */
    public DisplayReference(final String display_reference) throws Exception
    {
        // Expect <filename> <space> <data>
        final String sections[] = StringSplitter.splitIgnoreInQuotes(display_reference, ' ',  true);
        if (sections.length <= 0)
        {
            is_valid = false;
            return;
        }
        filename = sections[0];
        // Data is optional
        if (sections.length > 1)
            data = sections[1];
        // More than expected? No filename?
        if (sections.length > 2  ||
            filename.length() <= 0)
        {
            is_valid = false;
            return;
        }
        // Should be a file name within workspace
        if (filename.charAt(0) == '/'  ||
            filename.charAt(0) == '.')
        {
            is_valid = true;
            return;

        }
        // .. or specifically marked as opi:...
        if (filename.startsWith("opi:"))
        {
            filename = filename.substring(4);
            is_valid = true;
            return;
        }
        // Consider anything else wrong
        is_valid = false;
    }

    /** @return <code>true</code> if this is indeed a display reference,
     *          <code>false</code> if the parsed text does not match
     *          a valid display reference
     */
    public boolean isValid()
    {
        return is_valid;
    }

    public String getFilename()
    {
        return filename;
    }

    public String getData()
    {
        return data;
    }
}
