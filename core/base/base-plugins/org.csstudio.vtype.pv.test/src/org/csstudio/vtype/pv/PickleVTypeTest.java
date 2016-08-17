/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import java.util.List;
import org.csstudio.vtype.pv.mqtt.VTypePickle;
import org.junit.Test;
import static org.junit.Assert.fail;

/** JUnit tests
 *  @author Megan Grodowitz
 */
public class PickleVTypeTest
{


    @Test
    public void testListSplit() throws Exception
    {
        List<String> items = VTypePickle.splitStringList("3.14");
        System.out.println(items);

        items = VTypePickle.splitStringList(" 1,  2,   3  ");
        System.out.println(items);
        assertThat(items.size(), equalTo(3));
        assertThat(items.get(0), equalTo("1"));
        assertThat(items.get(1), equalTo("2"));
        assertThat(items.get(2), equalTo("3"));

        items = VTypePickle.splitStringList("[ 1,  2,   3  ]");
        System.out.println(items);
        assertThat(items.size(), equalTo(3));
        assertThat(items.get(0), equalTo("1"));
        assertThat(items.get(1), equalTo("2"));
        assertThat(items.get(2), equalTo("3"));

        items = VTypePickle.splitStringList("[ \\n, \\b, \\r, \\t, \\f, \\', \\\", \\\\ ]");
        System.out.println(items);
        assertThat(items.size(), equalTo(8));

        items = VTypePickle.splitStringList("\"A\", \"2 Apples\"");
        System.out.println(items);
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(1), equalTo("\"2 Apples\""));

        items = VTypePickle.splitStringList("[\"A\", \" Apples, 2\"]");
        System.out.println(items);
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(1), equalTo("\" Apples, 2\""));

        items = VTypePickle.splitStringList("\"Text with \\\"Quote\\\"\", \"Text\"");
        System.out.println(items);
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0), equalTo("\"Text with \\\"Quote\\\"\""));
        assertThat(items.get(1), equalTo("\"Text\""));
    }

    @Test
    public void testListSplitErrors() throws Exception
    {
        List<String> items = VTypePickle.splitStringList("3.14");
        System.out.println(items);

        try {
            items = VTypePickle.splitStringList("[ 1,  2,   3  ");
            fail("Did not catch missing closing bracket");
        }
        catch (Exception ex)
        {
            System.out.println("Successful error catch of no closing bracket: " + ex.getMessage());
        }

        try {
            items = VTypePickle.splitStringList("[ 1,  2\\,   3  ]");
            fail("Did not catch bad escape sequence");
        }
        catch (Exception ex)
        {
            System.out.println("Successful error catch bad escape sequence: " + ex.getMessage());
        }

        try {
            items = VTypePickle.splitStringList(" 1,  2,   3  \\");
            fail("Did not catch bad escape sequence");
        }
        catch (Exception ex)
        {
            System.out.println("Successful error catch trailing escape char: " + ex.getMessage());
        }

        try {
            items = VTypePickle.splitStringList("[\"A\", \" Apples, 2]");
            fail("Did not catch missing closing quote");
        }
        catch (Exception ex)
        {
            System.out.println("Successful error catch of no closing quote: " + ex.getMessage());
        }

    }
}
