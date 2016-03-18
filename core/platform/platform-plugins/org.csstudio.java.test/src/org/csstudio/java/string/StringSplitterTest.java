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

    /** Quotes, with tabs treated as space, that get removed */
    @Test
    public void tabsHandledAsSingleSpaces() throws Exception
    {
        final String result[] = StringSplitter.splitIgnoreInQuotes(
                "/tmp/demo\t\"Hello Dolly\" \"this\tis a test\"\t", ' ', true);
        assertEquals(3, result.length);
        assertEquals("/tmp/demo", result[0]);
        assertEquals("Hello Dolly", result[1]);
        assertEquals("this is a test", result[2]);
    }

    @Test
    public void splitOnTabsDoesNotSplitOnSpaces() throws Exception
    {
        final String result[] = StringSplitter.splitIgnoreInQuotes(
                "/tmp/demo\tHello Dolly\t\"this\tis a test\"", '\t', true);
        assertEquals(3, result.length);
        assertEquals("/tmp/demo", result[0]);
        assertEquals("Hello Dolly", result[1]);
        assertEquals("this\tis a test", result[2]);
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

    @Test
    public void doesNotSplitOnEscapedQuote() throws Exception
    {
        final String result[] = StringSplitter
                .splitIgnoreInQuotes("before\\\"after", ' ', true);
        assertEquals(1, result.length);
    }

    @Test
    public void doesNotSplitOnEscapedSingleQuote() throws Exception
    {
        final String result[] = StringSplitter
                .splitIgnoreInQuotes("before\'after", ' ', true);
        assertEquals(1, result.length);
    }


    @Test
    public void doesNotSplitOnEscapedQuotedQuote() throws Exception
    {
        final String result[] = StringSplitter
                .splitIgnoreInQuotes("be\"fo re\\\"af te\"r", ' ', true);

        assertEquals(1, result.length);
    }


    @Test
    public void doesNotSplitOnEscapedQuotedSingleQuote() throws Exception
    {
        final String result[] = StringSplitter
                .splitIgnoreInQuotes("be'fo re\\'af te'r", ' ', true);

        assertEquals(1, result.length);
    }

    @Test
    public void xtermWithSingleQuotedArgs() throws Exception
    {
        final String command = "xterm -T 'Console CS-DI-IOC-05' -e 'console CS-DI-IOC-05'";
        final String result[] = StringSplitter.splitIgnoreInQuotes(command, ' ', true);
        assertEquals(5,  result.length);
    }

    @Test
    public void spaceSeparatedStringWithCommaSplit() throws Exception
    {
        final String command = "one two three, four five";
        final String result[] = StringSplitter.splitIgnoreInQuotes(command, ',', true);

        assertEquals(2,  result.length);
        assertEquals("one two three", result[0]);
        assertEquals("four five", result[1]);
    }

    @Test
    public void xtermWithEscapedDoubleQuotedArgs() throws Exception
    {
        final String command = "xterm -T \"Console CS-DI-IOC-05\" -e \"console CS-DI-IOC-05\"";
        final String result[] = StringSplitter.splitIgnoreInQuotes(command, ' ', true);

        assertEquals(5,  result.length);
    }

    @Test
    public void removeQuotesReturnsUnquotedStringUnchanged()
    {
        assertEquals("noQuotes", StringSplitter.removeQuotes("noQuotes"));
    }
    @Test
    public void removeQuotesReturnsSingleQuotedStringStripped()
    {
        assertEquals("singleQuotes", StringSplitter.removeQuotes("'singleQuotes'"));
    }
    @Test
    public void removeQuotesReturnsDoubleQuotedStringStripped()
    {
        assertEquals("doubleQuotes",
                StringSplitter.removeQuotes("\"doubleQuotes\""));
    }

    @Test
    public void substituteDoesNotChangeDoubleQuotes()
    {
        assertEquals("my doublequote \"",
                StringSplitter.substituteEscapedQuotes("my doublequote \""));
    }

    @Test
    public void substituteReplacesEscapedDoubleQuotes()
    {
        assertEquals("my escaped " + StringSplitter.SUBSTITUTE_QUOTE + " doublequote",
                StringSplitter.substituteEscapedQuotes("my escaped \\\" doublequote"));
    }


    @Test
    public void substituteRevertLeavesEscapedDoubleQuotesUnchanged()
    {

        assertEquals("my escaped \\\" doublequote",
                StringSplitter.revertQuoteSubsitutions(
                        StringSplitter.substituteEscapedQuotes("my escaped \\\" doublequote")));
    }

    @Test
    public void substituteDoesNotChangeSingleQuotes()
    {
        assertEquals("my singlequote '",
                StringSplitter.substituteEscapedQuotes("my singlequote '"));
    }

    @Test
    public void substituteReplacesEscapedSingleQuotes()
    {
        assertEquals("my escaped " + StringSplitter.SUBSTITUTE_SINGLE_QUOTE + " singlequote",
                StringSplitter.substituteEscapedQuotes("my escaped \\\' singlequote"));
    }

    @Test
    public void substituteRevertLeavesEscapedSingleQuotesUnchanged()
    {
        assertEquals("my escaped \\\' singlequote",
                StringSplitter.revertQuoteSubsitutions(
                        StringSplitter.substituteEscapedQuotes("my escaped \\\' singlequote")));
    }
}
