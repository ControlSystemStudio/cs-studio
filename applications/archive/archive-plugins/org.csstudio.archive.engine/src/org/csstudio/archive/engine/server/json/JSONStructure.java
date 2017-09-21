/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.json;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang3.StringEscapeUtils;

/** Represents a generic JSON structure i.e. a list or an object
 *  @author Dominic Oram
 */

public abstract class JSONStructure {
    /** Writer */
    protected PrintWriter buf;
    final private StringWriter out = new StringWriter();

    protected boolean isFirstItem = true;

    /**
     * Creates a structure that ouputs the JSON to a StringWriter.
     */
    public JSONStructure()
    {
        this.buf = new PrintWriter(out);
        open();
    }

    /**
     * Formats a string for JSON.
     * @param str The string to format.
     * @return A correctly formatted string.
     */
    protected String formatString(final String str) {
        return "\"" + StringEscapeUtils.escapeJava(str) + "\"";
    }

    /** Add a list seperator to the JSON object. */
    public void listSeperator() {
        if (!isFirstItem) {
            buf.print(",");
        } else {
            isFirstItem = false;
        }
    }

    protected abstract void open();
    public abstract void close();

    @Override
    public String toString() {
        return out.toString();
    }
}
