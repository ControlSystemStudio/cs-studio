/**
 * 
 */
package org.csstudio.utility.adlparser.fileParser;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * @author hammonds
 *
 */
public class FileLineTest  extends TestCase {
	String nullString = null;
	FileLine nullLine = new FileLine(nullString, 0);
	String testString = "test";
	String testString2 = "This Is A Test";
	FileLine testLine = new FileLine(testString, 1);

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.FileLine#FileLine(java.lang.String, int)}.
	 */
	@Test
	public void testFileLine() {
		assertTrue("Test Null line ", nullLine.getLine().equals(""));
		assertEquals("test Null line number", nullLine.getLineNumber(),0);
		assertTrue("Test test line ", testLine.getLine().equals(testString));
		assertEquals("test test line number", testLine.getLineNumber(),1);
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.FileLine#setLine(java.lang.String)}.
	 */
	@Test
	public void testSetLine() {
		nullLine.setLine(testString);
		assertTrue("Test Null line " + nullLine.getLine(), nullLine.getLine().equals(testString));
		testLine.setLine(nullString);
		assertTrue("Test test line " + testLine.getLine(), testLine.getLine().equals(""));
		
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.FileLine#argEquals(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testArgEquals() {
		assertTrue("test Empty strings ", FileLine.argEquals("", ""));
		assertFalse("test empty vs string with contents", FileLine.argEquals("", "this is a test"));
		assertTrue("test identical strings", FileLine.argEquals("this is a test", "this is a test"));
		assertTrue("test case changing", FileLine.argEquals("this is a test", "This Is A Test"));
		assertTrue("test case changing", FileLine.argEquals("this is a test", "This Is A Test"));
		assertTrue("test with surrounding whitespace", FileLine.argEquals(" this is a test ", "This Is A Test"));
		assertFalse("Whitespaced is only trimmed from arg", FileLine.argEquals("this is a test", " This Is A Test "));
		assertFalse("Whitespaced on both not equal since trimmed from arg", FileLine.argEquals(" this is a test ", " This Is A Test "));

	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.FileLine#getTrimmedValue(java.lang.String)}.
	 */
	@Test
	public void testGetTrimmedValue() {
		assertTrue("Test NormalString", FileLine.getTrimmedValue(testString2).equals(testString2));
		assertTrue("Test with quotes", FileLine.getTrimmedValue("\"" + testString2 + "\"").equals(testString2));
		assertTrue("Test with quotes & space", FileLine.getTrimmedValue("\" " + testString2 + " \"").equals(testString2));
		assertTrue("Test with quotes & space", FileLine.getTrimmedValue(" \" " + testString2 + " \" ").equals(testString2));
		assertFalse("Test Switching case", FileLine.getTrimmedValue(testString2.toLowerCase()).equals(testString2));
		assertFalse("Test Switching case", FileLine.getTrimmedValue(testString2.toUpperCase()).equals(testString2));
		
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.FileLine#getIntValue(java.lang.String)}.
	 */
	@Test
	public void testGetNormalIntValue() {
		assertEquals("simple positive Integer", FileLine.getIntValue("7"), 7);
		assertEquals("simple zero Integer", FileLine.getIntValue("0"), 0);
		assertEquals("simple negative Integer", FileLine.getIntValue("-532"), -532);
		assertEquals("Large Integer near the boundaries", FileLine.getIntValue("2147483647"), 2147483647);
		assertEquals("Large neg Integer near the boundaries", FileLine.getIntValue("-2147483648"), -2147483648);
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.FileLine#getIntValue(java.lang.String)}.
	 */
	@Test
	public void testGetStrangeIntValue() {
		try {
			assertEquals("Float", FileLine.getIntValue("3.1415"), 3);
			fail("Should fail for a floating point");
		}
		catch (NumberFormatException ex){
			// OK this should fail
		}
		try {
			FileLine.getIntValue("2147483648");
			fail("Should fail if number is too large");
		}
		catch (NumberFormatException ex){
			// OK this should fail
		}
		try {
			FileLine.getIntValue("21 years");
			fail("Should fail contains string");
		}
		catch (NumberFormatException ex){
			// OK this should fail
		}
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.FileLine#getFloatValue(java.lang.String)}.
	 */
	@Test
	public void testNormalGetFloatValue() {
		assertEquals("simple positive Float", FileLine.getFloatValue("3.1415"), 3.1415f);
		assertEquals("simple zero Float", FileLine.getFloatValue("0.0000"), 0.0000f);
		assertEquals("simple negative Float", FileLine.getFloatValue("-3.1415"), -3.1415f);
		assertEquals("simple positive Integer", FileLine.getFloatValue("200"), 200.0f);
		assertEquals("simple positive Integer", FileLine.getFloatValue("0"), 0.0f);
		assertEquals("simple positive Integer", FileLine.getFloatValue("-200"), -200.0f);

		}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.FileLine#getFloatValue(java.lang.String)}.
	 */
	@Test
	public void testStrangeGetFloatValue() {
		try {
			FileLine.getFloatValue("Hello");
			fail("Should not be able to convert an alpha string");
		} catch (NumberFormatException ex) {
			// OK
		}

	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.FileLine#getBooleanValue(java.lang.String)}.
	 */
	@Test
	public void testGetBooleanValue() {
		assertEquals("true", FileLine.getBooleanValue("true"), true);
		assertEquals("True", FileLine.getBooleanValue("True"), true);
		assertEquals("TRUE", FileLine.getBooleanValue("TRUE"), true);
		assertEquals("TruE", FileLine.getBooleanValue("True"), true);
		assertEquals("false", FileLine.getBooleanValue("false"), false);
		assertEquals("FALSE", FileLine.getBooleanValue("FALSE"), false);
		assertEquals("FalsE", FileLine.getBooleanValue("FalsE"), false);
		assertEquals("False", FileLine.getBooleanValue("False"), false);
		assertEquals("yes", FileLine.getBooleanValue("yes"), false);
		assertEquals("YES", FileLine.getBooleanValue("YES"), false);
		assertEquals("Yes", FileLine.getBooleanValue("Yes"), false);
		assertEquals("No", FileLine.getBooleanValue("No"), false);
		assertEquals("No", FileLine.getBooleanValue("No"), false);
		assertEquals("Hello", FileLine.getBooleanValue("Hello"), false);
		assertEquals("I dont care", FileLine.getBooleanValue("I dont care"), false);
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.FileLine#toString()}.
	 */
	@Test
	public void testToString() {
		String nullOut = nullLine.toString();
		assertTrue("Test Null output " + nullOut, nullOut.equals("\r\nFile: null\r\n0: "));
		String testOut = testLine.toString();
		assertTrue("Test test output " + testOut, testOut.equals("\r\nFile: null\r\n1: test"));

		String testFileName = "MyFile";
		FileLine.setFile(testFileName);

		nullOut = nullLine.toString();
		assertTrue("Test Null output " + nullOut, nullOut.equals("\r\nFile: MyFile\r\n0: "));
		testOut = testLine.toString();
		assertTrue("Test test output " + testOut, testOut.equals("\r\nFile: MyFile\r\n1: test"));

	}

}
