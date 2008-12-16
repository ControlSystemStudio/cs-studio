package org.csstudio.platform.util;

import static org.junit.Assert.*;

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

}
