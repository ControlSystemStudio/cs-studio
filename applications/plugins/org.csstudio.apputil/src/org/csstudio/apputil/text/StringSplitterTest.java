package org.csstudio.apputil.text;

import org.junit.Test;

import junit.framework.TestCase;

public class StringSplitterTest extends TestCase {

    /** No quotes at all */
    @Test
    public void test1() throws Exception
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
    public void test2() throws Exception
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
    public void test3() throws Exception
    {
        final String result[] = StringSplitter.splitIgnoreInQuotes(
                "  /tmp/demo \"Hello Dolly\" this    is   \" a test \"   ", ' ', true);
        assertEquals(5, result.length);
        assertEquals("/tmp/demo", result[0]);
        assertEquals("Hello Dolly", result[1]);
        assertEquals("this", result[2]);
        assertEquals("is", result[3]);
        assertEquals(" a test ", result[4]);
    }
    
    @Test
    public void test4() throws Exception
    {
    	final String result[] = StringSplitter.splitIgnoreInQuotes(
    			"|||This is a|| || |\"complicated||test.\"|||Hello, \"fox|and\"dog|", '|', false);
    	assertEquals("This is a", result[0]);
    	assertEquals(" ", result[1]);
    	assertEquals(" ", result[2]);    	
    	assertEquals("\"complicated||test.\"", result[3]);    	
    	assertEquals("Hello, \"fox|and\"dog", result[4]);
    }
    
    @Test
    public void test5() throws Exception
    {
    	final String result[] = StringSplitter.splitIgnoreInQuotes(
    			"group.provider.url=\"ldap://localhost:389/ou=People,dc=test,dc=ics\"", '=', true);
    	assertEquals("group.provider.url", result[0]);
    	assertEquals("ldap://localhost:389/ou=People,dc=test,dc=ics", result[1]);
    }
}
