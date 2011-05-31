package org.csstudio.platform.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Tests of <code>splitIgnoreInQuotes</code> by Xihui Chen
 */
@SuppressWarnings("nls")
public class StringUtilTest
{
    /** No quotes at all */
    @Test
    public void testSplit1() throws Exception {
        final String result[] = StringUtil.splitIgnoreInQuotes(
                                                               "/tmp/demo a test", ' ', true);
        assertEquals(3, result.length);
        assertEquals("/tmp/demo", result[0]);
        assertEquals("a", result[1]);
        assertEquals("test", result[2]);
    }

    /** Quotes, extra white space that gets removed */
    @Test
    public void testSplit2() throws Exception {
        final String result[] = StringUtil.splitIgnoreInQuotes(
                                                               "/tmp/demo \"Hello Dolly\" \"this is a test\"   ", ' ', true);
        assertEquals(3, result.length);
        assertEquals("/tmp/demo", result[0]);
        assertEquals("Hello Dolly", result[1]);
        assertEquals("this is a test", result[2]);
    }

    /** Spaces within quotes that should remain */
    @Test
    public void testSplit3() throws Exception {
        final String result[] = StringUtil.splitIgnoreInQuotes(
                                                               "  /tmp/demo \"Hello Dolly\" this    is   \" a test \"   ", ' ', true);
        assertEquals(5, result.length);
        assertEquals("/tmp/demo", result[0]);
        assertEquals("Hello Dolly", result[1]);
        assertEquals("this", result[2]);
        assertEquals("is", result[3]);
        assertEquals(" a test ", result[4]);
    }

    @Test
    public void testSplit4() throws Exception {
        final String result[] = StringUtil.splitIgnoreInQuotes(
                                                               "|||This is a|| || |\"complicated||test.\"|||Hello, \"fox|and\"dog|", '|', false);
        assertEquals("This is a", result[0]);
        assertEquals("", result[1]);
        assertEquals("", result[2]);
        assertEquals("\"complicated||test.\"", result[3]);
        assertEquals("Hello, \"fox|and\"dog", result[4]);
    }

    @Test
    public void testSplit5() throws Exception {
        final String result[] = StringUtil.splitIgnoreInQuotes(
                                                               "group.provider.url = \"ldap://localhost:389/ou=People,dc=test,dc=ics\"", '=', true);
        assertEquals("group.provider.url", result[0]);
        assertEquals("ldap://localhost:389/ou=People,dc=test,dc=ics", result[1]);
    }

    /** Escaped quotes */
    @Test
    public void testSplitEscaped() throws Exception {
        final String result[] = StringUtil.splitIgnoreInQuotes(
                                                               " First  \"The Second with escaped quote --> \\\" <--\" \"The Third\"   ", ' ', true);
        assertEquals(3, result.length);
        assertEquals("First", result[0]);
        assertEquals("The Second with escaped quote --> \\\" <--", result[1]);
        assertEquals("The Third", result[2]);
    }

    @Test
    public final void testPrintArrays() {
        assertNotNull(StringUtil.printArrays(new int[]{1,2,3}));
    }

    @Test
    public final void testCapitalize() {
        assertEquals("Test", StringUtil.capitalize("test"));
        assertEquals("Test", StringUtil.capitalize("Test"));
        assertEquals("TEST", StringUtil.capitalize("TEST"));
        assertEquals("", StringUtil.capitalize(""));
        assertNull(StringUtil.capitalize(null));
    }

    @Test
    public final void testHasLength() {
        assertTrue(StringUtil.hasLength("a"));
        assertTrue(StringUtil.hasLength(" "));
        assertFalse(StringUtil.hasLength(""));
        assertFalse(StringUtil.hasLength(null));
    }

    @Test
    public final void testIsBlank() {
    	assertFalse(StringUtil.isBlank("a"));
    	assertFalse(StringUtil.isBlank(" a  "));
    	assertTrue(StringUtil.isBlank(" "));
    	assertTrue(StringUtil.isBlank(""));
    	assertTrue(StringUtil.isBlank(null));
    }

    @Test
    public final void testToSeparatedString() {
        final List<String> l = new ArrayList<String>();
        assertEquals("", StringUtil.toSeparatedString(l, ","));
        l.add("a");
        assertEquals("a", StringUtil.toSeparatedString(l, ","));
        l.add("b");
        assertEquals("a,b", StringUtil.toSeparatedString(l, ","));
        l.add("c");
        assertEquals("a;b;c", StringUtil.toSeparatedString(l, ";"));
    }

    @Test
    public final void testTrimNull() {
        assertEquals("", StringUtil.trimNull(null));
        assertEquals("", StringUtil.trimNull(""));
        assertEquals("a", StringUtil.trimNull("a"));
        assertEquals("a ", StringUtil.trimNull("a "));
        assertEquals(" a ", StringUtil.trimNull(" a "));
    }

    @Test
    public final void testJoin() {
        assertNull(StringUtil.join((Object[]) null, null));
        assertNull(StringUtil.join((Object[]) null, ""));
        assertNull(StringUtil.join((Object[]) null, "xxx"));
        assertEquals("", StringUtil.join(new String[0], null));
        assertEquals("", StringUtil.join(new String[0], ""));
        assertEquals("", StringUtil.join(new String[0], "abc"));
        assertEquals("", StringUtil.join(new String[]{""}, "abc"));
        assertEquals("abc", StringUtil.join(new String[]{null, null}, "abc"));
        assertEquals("abc", StringUtil.join(new String[]{"", null}, "abc"));
        assertEquals("abc", StringUtil.join(new String[]{null, ""}, "abc"));
        assertEquals("abcabc", StringUtil.join(new String[]{null, "", null}, "abc"));

        assertEquals("|x|y|z||&|3|", StringUtil.join(new String[]{null, "x", "y", "z", null, "&", "3", ""}, "|"));

        assertEquals("1|2|", StringUtil.join(new Integer[]{1, 2, null}, "|"));
        assertEquals("[true, false]|[1.0, 2.0]", StringUtil.join(new ArrayList<?>[]{ new ArrayList<Object>(Arrays.<Boolean>asList(Boolean.TRUE, Boolean.FALSE)),
                                                             new ArrayList<Object>(Arrays.<Double>asList(Double.valueOf(1.0), Double.valueOf(2.0)))},
                                             "|"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public final void testJoin2() {

        final BasicAttributes attrs = new BasicAttributes();
        String joined;
        try {
            attrs.put("ID0", null);
            final NamingEnumeration<String> vals0 = (NamingEnumeration<String>) attrs.get("ID0").getAll();
            joined = StringUtil.join(vals0, null);
            Assert.assertNull(joined);


            attrs.put("ID1", "");
            final NamingEnumeration<String> vals1_1 = (NamingEnumeration<String>) attrs.get("ID1").getAll();
            joined = StringUtil.join(vals1_1, null);
            Assert.assertEquals("", joined);

            final NamingEnumeration<String> vals1_2 = (NamingEnumeration<String>) attrs.get("ID1").getAll();
            joined = StringUtil.join(vals1_2, "");
            Assert.assertEquals("", joined);

            final NamingEnumeration<String> vals1_3 = (NamingEnumeration<String>) attrs.get("ID1").getAll();
            joined = StringUtil.join(vals1_3, "xxx");
            Assert.assertEquals("", joined);


            final Attribute attr = new BasicAttribute("ID2", true);
            attr.add(0, "x");
            attr.add(1, "y");
            final NamingEnumeration<String> vals2_1 = (NamingEnumeration<String>) attr.getAll();
            joined = StringUtil.join(vals2_1, "||");
            Assert.assertEquals("x||y", joined);
            final NamingEnumeration<String> vals2_2 = (NamingEnumeration<String>) attr.getAll();
            joined = StringUtil.join(vals2_2, null);
            Assert.assertEquals("xy", joined);

        } catch (final NamingException e) {
            Assert.fail("Naming exception for: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
}
