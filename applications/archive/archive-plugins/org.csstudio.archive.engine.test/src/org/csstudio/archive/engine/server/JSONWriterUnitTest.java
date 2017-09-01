/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.server.json.JSONWriter;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of {@link JSONWriter}
 *  @author Dominic Oram
 */
public class JSONWriterUnitTest
{
    private StringWriter output;
    private JSONWriter writer;

    @Before
    public void setUp() throws Exception {
        HttpServletResponse mockResponse = createMock(HttpServletResponse.class);
        output = new StringWriter();
        mockResponse.setContentType("application/json");
        expectLastCall().times(1);
        expect(mockResponse.getWriter()).andStubReturn(new PrintWriter(output));
        replay(mockResponse);
        writer = new JSONWriter(mockResponse);
    }

    @Test
    public void testWhenWriterInitialisedThenOutputContainsObjectStart()
    {
        assertEquals("{", output.toString());
    }

    @Test
    public void testWhenObjectKeyWrittenThenOutputAsExpected()
    {
        writer.writeObjectKey("TEST");

        assertEquals("{\"TEST\":", output.toString());
    }

    @Test
    public void testWhenStringEntryWrittenThenOutputContainsQuotations()
    {
        writer.writeObjectEntry("KEY", "VAL");

        assertEquals("{\"KEY\":\"VAL\"", output.toString());
    }

    @Test
    public void testWhenIntegerEntryWrittenThenOutputContainsNoQuotations()
    {
        writer.writeObjectEntry("KEY", 123);

        assertEquals("{\"KEY\":123", output.toString());
    }

    @Test
    public void testWhenFloatEntryWrittenThenOutputContainsNoQuotations()
    {
        writer.writeObjectEntry("KEY", 1e3);

        assertEquals("{\"KEY\":1000.0", output.toString());
    }

    @Test
    public void testWhenBooleanEntryWrittenThenOutputContainsNoQuotations()
    {
        writer.writeObjectEntry("KEY", true);

        assertEquals("{\"KEY\":true", output.toString());
    }

    @Test
    public void testWhenTwoEntriesWrittenThenOutputContainsListSeperator()
    {
        writer.writeObjectEntry("KEY", true);
        writer.writeObjectEntry("KEY1", false);

        assertEquals("{\"KEY\":true,\"KEY1\":false", output.toString());
    }

    @Test
    public void testWhenSecondObjectWrittenThenOutputContainsCorrectListSeperators()
    {
        writer.writeObjectEntry("KEY", true);
        writer.writeObjectEntry("KEY1", false);
        writer.closeObject();
        writer.listSeperator();
        writer.openObject();
        writer.writeObjectEntry("KEY", true);

        assertEquals("{\"KEY\":true,\"KEY1\":false},{\"KEY\":true", output.toString());
    }
}
