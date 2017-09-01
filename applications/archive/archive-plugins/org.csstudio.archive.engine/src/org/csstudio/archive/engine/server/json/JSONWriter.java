/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.json;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/** Helper for creating JSON for a servlet response.
 *  @author Dominic Oram
 */

public class JSONWriter {
    /** Writer */
    final private PrintWriter json;

    boolean isFirstItem = true;

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
        isFirstItem = true;
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
        isFirstItem = true;
    }

    /** Closes a JSON list **/
    public void closeList()
    {
        print("]");
    }

    /** Add a string to the JSON object*/
    private void write(final String str) {
        print("\"" + str + "\"");
    }

    /** Add a key to the JSON object*/
    public void writeObjectKey(final String key) {
        write(key);
        print(":");
    }

    /** Add an entry to the JSON object*/
    private void writeObjectEntry(String key, Runnable writeValue) {
        if (!isFirstItem) {
            listSeperator();
        } else {
            isFirstItem = false;
        }
        writeObjectKey(key);
        writeValue.run();
    }

    /** Add a string entry to the JSON object*/
    public void writeObjectEntry(String key, String value) {
        writeObjectEntry(key, () -> write(value));
    }

    /** Add a number entry to the JSON object*/
    public void writeObjectEntry(String key, Number value) {
        writeObjectEntry(key, () -> print(value.toString()));
    }

    /** Add a boolean entry to the JSON object*/
    public void writeObjectEntry(String key, Boolean value) {
        writeObjectEntry(key, () -> print(value.toString()));
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
