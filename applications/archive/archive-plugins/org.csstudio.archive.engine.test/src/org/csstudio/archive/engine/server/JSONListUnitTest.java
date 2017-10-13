/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server;

import static org.junit.Assert.assertEquals;

import org.csstudio.archive.engine.server.json.JSONList;
import org.csstudio.archive.engine.server.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/** JUnit test of {@link JSONList}
 *  @author Dominic Oram
 */
public class JSONListUnitTest
{
    private JSONList list;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        list = new JSONList();
    }

    @Test
    public void testWhenWriterInitialisedThenOutputContainsListStart()
    {
        assertEquals("[", list.toString());
    }

    @Test
    public void testWhenObjectWrittenToListThenOutputContainsClosedObject()
    {
        list.addObjectToList(new JSONObject());

        assertEquals("[{}", list.toString());
    }

    @Test
    public void testWhenTwoObjectsWrittenToListThenOutputContainsTwoClosedObject()
    {
        list.addObjectToList(new JSONObject());
        list.addObjectToList(new JSONObject());

        assertEquals("[{},{}", list.toString());
    }

    @Test
    public void testWhenListIsClosedThenWritingAgainThrowsException()
    {
        list.close();

        exception.expect(IllegalStateException.class);
        list.addObjectToList(new JSONObject());
    }

    @Test
    public void testWhenObjectIsClosedThenClosingAgainThrowsException()
    {
        list.close();

        exception.expect(IllegalStateException.class);
        list.close();
    }
}
