/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.json;

import java.io.PrintWriter;

/** Representation of a JSON Object.
 *  @author Dominic Oram
 */

public class JSONObject extends JSONStructure{
    /** Creates the object based on a StringWriter.
    **/
    public JSONObject() {
        super();
    }

    /** Creates an object that writes to a provided PrintWriter.
     *  This is currently only used for the root object.
     *
     * @param buf The PrintWriter to write to.
     */
    protected JSONObject(final PrintWriter buf)
    {
        super(buf);
    }

    /** Opens a JSON object **/
    @Override
    protected void open()
    {
        print("{");
    }

    /** Add a generic entry to this JSON object.
     *
     *  @param key The key to add the entry under.
     *  @param value The value to add to the object.
     */
    private void writeObjectEntryToJson(String key, String value) {
        throwIfClosed();
        listSeperator();
        print(formatString(key));
        print(":");
        print(value);
    }

    /** Add a generic JSON structure entry to this JSON object
     *
     *  @param key The key to add the structure under.
     *  @param value The structure to add to the object.
     */
    public void writeObjectEntry(String key, JSONStructure value) {
        value.close();
        writeObjectEntryToJson(key, value.toString());
    }

    /** Add a string entry to this JSON object
     *
     *  @param key The key to add the string under.
     *  @param value The string to add to the object.
     */
    public void writeObjectEntry(String key, String value) {
        writeObjectEntryToJson(key, formatString(value));
    }

    /** Add a number entry to this JSON object
     *
     *  @param key The key to add the number under.
     *  @param value The number to add to the object.
     */
    public void writeObjectEntry(String key, Number value) {
        writeObjectEntryToJson(key, value.toString());
    }

    /** Add a boolean entry to this JSON object
     *
     *  @param key The key to add the boolean under.
     *  @param value The boolean to add to the object.
     */
    public void writeObjectEntry(String key, Boolean value) {
        writeObjectEntryToJson(key, value.toString());
    }

    @Override
    protected void printCloseChar() {
        print("}");

    }
}
