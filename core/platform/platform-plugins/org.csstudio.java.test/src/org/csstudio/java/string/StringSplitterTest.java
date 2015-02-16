/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.java.string;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** JUnit test of {@link StringSplitter}
 *  @author Kay Kasemir
 *  @author Xihui Chen - Original Tests of <code>splitIgnoreInQuotes</code>
 */
@SuppressWarnings("nls")
public class StringSplitterTest
{
    /** No quotes at all */
    @Test
    public void testNoQuotes() throws Exception
    {
        final String result[] = StringSplitter.splitIgnoreInQuotes(
                "/tmp/demo a test", ' ', true);
        assertEquals(3, result.length);
        assertEquals("/tmp/demo", result[0]);
        assertEquals("a", result[1]);
        assertEquals("test", result[2]);
    }

    /** Quotes, extra white space that gets removed */
    @Test
    public void testQuotes() throws Exception
    {
        final String result[] = StringSplitter.splitIgnoreInQuotes(
                "/tmp/demo \"Hello Dolly\" \"this is a test\"   ", ' ', true);
        assertEquals(3, result.length);
        assertEquals("/tmp/demo", result[0]);
        assertEquals("Hello Dolly", result[1]);
        assertEquals("this is a test", result[2]);
    }

    /** Spaces within quotes that should remain */
    @Test
    public void testQuotedSpace() throws Exception
    {
        final String result[] = StringSplitter.splitIgnoreInQuotes(
                "  /tmp/demo \"Hello Dolly\" this    is   \" a test \"   ",
                ' ', true);
        assertEquals(5, result.length);
        assertEquals("/tmp/demo", result[0]);
        assertEquals("Hello Dolly", result[1]);
        assertEquals("this", result[2]);
        assertEquals("is", result[3]);
        assertEquals(" a test ", result[4]);
    }

    @Test
    public void testOddball() throws Exception
    {
        final String result[] = StringSplitter
                .splitIgnoreInQuotes(
                        "|||This is a|| || |\"complicated||test.\"|||Hello, \"fox|and\"dog|",
                        '|', false);
        assertEquals("This is a", result[0]);
        assertEquals("", result[1]);
        assertEquals("", result[2]);
        assertEquals("\"complicated||test.\"", result[3]);
        assertEquals("Hello, \"fox|and\"dog", result[4]);
    }

    @Test
    public void testLDAPExample() throws Exception
    {
        final String result[] = StringSplitter
                .splitIgnoreInQuotes(
                        "group.provider.url = \"ldap://localhost:389/ou=People,dc=test,dc=ics\"",
                        '=', true);
        assertEquals("group.provider.url", result[0]);
        assertEquals("ldap://localhost:389/ou=People,dc=test,dc=ics", result[1]);
    }

    /** Escaped quotes */
    @Test
    public void testSplitEscaped() throws Exception
    {
        final String result[] = StringSplitter
                .splitIgnoreInQuotes(
                        " First  \"The Second with escaped quote --> \\\" <--\" \"The Third\"   ",
                        ' ', true);
        assertEquals(3, result.length);
        assertEquals("First", result[0]);
        assertEquals("The Second with escaped quote --> \\\" <--", result[1]);
        assertEquals("The Third", result[2]);
    }

}
