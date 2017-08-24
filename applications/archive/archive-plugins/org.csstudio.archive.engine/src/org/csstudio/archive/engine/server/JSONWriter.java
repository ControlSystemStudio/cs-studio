/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/** Helper for creating JSON for a servlet response.
 *  @author Dominic Oram
 */

public class JSONWriter {
    /** Writer */
    final private PrintWriter json;

    boolean empty = true;

    public JSONWriter(final HttpServletResponse resp)
        throws Exception
    {
        resp.setContentType("application/json");
        json = resp.getWriter();
        openObject();
    }

    /** Opens a JSON object **/
    public void openObject()
    {
        print("{");
    }

    /** Closes a JSON object **/
    public void closeObject()
    {
        print("}");
    }

    /** Opens a JSON list **/
    public void openList()
    {
        print("[");
    }

    /** Closes a JSON list **/
    public void closeList()
    {
        print("]");
    }

    /** Add a string to the JSON object*/
    public void write(final String str) {
        print("\"" + str + "\"");
    }

    /** Add a number to the JSON object*/
    public void write(final Number num) {
        print(num.toString());
    }

    /** Add a number to the JSON object*/
    public void write(final Boolean bool) {
        if (bool) {
            print("true");
        } else {
            print("false");
        }
    }

    /** Add a key to the JSON object*/
    public void writeObjectKey(final String key) {
        write(key);
        print(":");
    }

    /** Add a list seperator to the JSON object*/
    public void listSeperator() {
        print(",");
    }

    /** Add text to the JSON object*/
    private void print(final String text)
    {
        json.print(text);
    }
}
