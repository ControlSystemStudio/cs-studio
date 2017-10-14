/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.json;

/** Representation of a JSON list.
 *  Note that currently only a list of objects is supported.
 *  @author Dominic Oram
 */

public class JSONList extends JSONStructure {
    /** Opens a JSON list **/
    @Override
    protected void open()
    {
        print("[");
    }

    /** Adds a JSON object to the list.
     *
     * @param object The object to add.
     */
    public void addObjectToList(JSONObject object) {
        object.close();
        listSeperator();
        print(object.toString());
    }

    @Override
    protected void printCloseChar() {
        print("]");
    }
}
