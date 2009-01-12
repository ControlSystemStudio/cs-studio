package org.csstudio.platform.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class StringUtilTest {

	@Before
	public void setUp() throws Exception {
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
	public final void testToSeparatedString() {
		List<String> l = new ArrayList<String>();
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

		
}
