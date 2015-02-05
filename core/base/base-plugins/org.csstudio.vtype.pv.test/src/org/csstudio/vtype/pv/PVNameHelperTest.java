/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.csstudio.vtype.pv.pva.PVNameHelper;
import org.junit.Test;

/** JUnit test of {@link PVNameHelper}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVNameHelperTest
{
    @Test
    public void testPlainName() throws Exception
    {
        final String pv = "channel_name";
        final PVNameHelper parser = PVNameHelper.forName(pv);
        System.out.println(pv + " -> " + parser);
        assertThat(parser.getChannel(), equalTo("channel_name"));
        assertThat(parser.getReadRequest(), equalTo("field()"));
        assertThat(parser.getWriteRequest(), equalTo("field(value)"));
    }

    @Test
    public void testRequest() throws Exception
    {
        final String pv = "channel_name?request=field(some.structure.element)";
        final PVNameHelper parser = PVNameHelper.forName(pv);
        System.out.println(pv + " -> " + parser);
        assertThat(parser.getChannel(), equalTo("channel_name"));
        assertThat(parser.getReadRequest(), equalTo("field(some.structure.element)"));
        assertThat(parser.getWriteRequest(), equalTo("field(some.structure.element.value)"));

        try
        {
            PVNameHelper.forName("channel_name?unsupported=42");
            fail("Should only support '?request..' query");
        }
        catch (Exception ex)
        {
            assertThat(ex.getMessage(), containsString("unsupported"));
        }
    }

    @Test
    public void testPath() throws Exception
    {
        String pv = "channel_name/some/structure.element";
        PVNameHelper parser = PVNameHelper.forName(pv);
        System.out.println(pv + " -> " + parser);
        assertThat(parser.getChannel(), equalTo("channel_name"));
        assertThat(parser.getReadRequest(), equalTo("field(some.structure.element)"));
        assertThat(parser.getWriteRequest(), equalTo("field(some.structure.element.value)"));

        pv = "channel_name/";
        parser = PVNameHelper.forName("channel_name/");
        System.out.println(pv + " -> " + parser);
        assertThat(parser.getChannel(), equalTo("channel_name"));
        assertThat(parser.getReadRequest(), equalTo("field()"));
        assertThat(parser.getWriteRequest(), equalTo("field(value)"));
    }
}
