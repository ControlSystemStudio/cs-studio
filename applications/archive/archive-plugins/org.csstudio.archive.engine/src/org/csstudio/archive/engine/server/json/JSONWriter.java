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

import org.apache.commons.lang3.StringEscapeUtils;

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
        json.print("{");
        isFirstItem = true;
    }

    /** Closes a JSON object **/
    public void closeObject()
    {
        json.print("}");
    }

    /** Opens a JSON list **/
    public void openList()
    {
        json.print("[");
    }

    /** Closes a JSON list **/
    public void closeList()
    {
        json.print("]");
    }

    /** Add a string to the JSON object*/
    private String formatString(final String str) {
        return "\"" + StringEscapeUtils.escapeJava(str) + "\"";
    }

    /** Add a key to the JSON object*/
    public void writeObjectKey(final String key) {
        json.print(formatString(key));
        json.print(":");
    }

    /** Add an entry to the JSON object*/
    private void writeObjectEntryToJson(String key, String value) {
        if (!isFirstItem) {
            listSeperator();
        } else {
            isFirstItem = false;
        }
        writeObjectKey(key);
        json.print(value);
    }

    /** Add a string entry to the JSON object */
    public void writeObjectEntry(String key, String value) {
        writeObjectEntryToJson(key, formatString(value));
    }

    /** Add a number entry to the JSON object*/
    public void writeObjectEntry(String key, Number value) {
        writeObjectEntryToJson(key, value.toString());
    }

    /** Add a boolean entry to the JSON object*/
    public void writeObjectEntry(String key, Boolean value) {
        writeObjectEntryToJson(key, value.toString());
    }

    /** Add a list seperator to the JSON object*/
    public void listSeperator() {
        json.print(",");
    }
}
