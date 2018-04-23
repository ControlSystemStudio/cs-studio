/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server;

import static org.junit.Assert.assertEquals;

import org.csstudio.archive.engine.server.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/** JUnit test of {@link JSONObject}
 *  @author Dominic Oram
 */
public class JSONObjectUnitTest
{
    private JSONObject object;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        object = new JSONObject();
    }

    @Test
    public void testWhenWriterInitialisedThenOutputContainsObjectStart()
    {
        assertEquals("{", object.toString());
    }

    @Test
    public void testWhenStringEntryWrittenThenOutputContainsQuotations()
    {
        object.writeObjectEntry("KEY", "VAL");

        assertEquals("{\"KEY\":\"VAL\"", object.toString());
    }

    @Test
    public void testWhenStringEntryThatContainsQuotationsWrittenThenQuotationsAreEscaped()
    {
        object.writeObjectEntry("KEY", "VAL \" ");

        assertEquals("{\"KEY\":\"VAL \\\" \"", object.toString());
    }

    @Test
    public void testWhenStringEntryThatContainsSlashesWrittenThenSlashesAreEscaped()
    {
        object.writeObjectEntry("KEY", "VAL \\ ");

        assertEquals("{\"KEY\":\"VAL \\\\ \"", object.toString());
    }

    @Test
    public void testWhenIntegerEntryWrittenThenOutputContainsNoQuotations()
    {
        object.writeObjectEntry("KEY", 123);

        assertEquals("{\"KEY\":123", object.toString());
    }

    @Test
    public void testWhenFloatEntryWrittenThenOutputContainsNoQuotations()
    {
        object.writeObjectEntry("KEY", 1e3);

        assertEquals("{\"KEY\":1000.0", object.toString());
    }

    @Test
    public void testWhenBooleanEntryWrittenThenOutputContainsNoQuotations()
    {
        object.writeObjectEntry("KEY", true);

        assertEquals("{\"KEY\":true", object.toString());
    }

    @Test
    public void testWhenTwoEntriesWrittenThenOutputContainsListSeperator()
    {
        object.writeObjectEntry("KEY", true);
        object.writeObjectEntry("KEY1", false);

        assertEquals("{\"KEY\":true,\"KEY1\":false", object.toString());
    }

    @Test
    public void testWhenObjectIsClosedThenWritingAgainThrowsException()
    {
        object.close();

        exception.expect(IllegalStateException.class);
        object.writeObjectEntry("TEST", true);
    }

    @Test
    public void testWhenObjectIsClosedThenClosingAgainThrowsException()
    {
        object.close();

        exception.expect(IllegalStateException.class);
        object.close();
    }
}
