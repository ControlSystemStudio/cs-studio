package org.csstudio.sds.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ColorAndFontUtil}.
 *
 * @author Sven Wende
 *
 */
public class ColorAndFontUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testToFontString() {
        assertEquals("Arial", ColorAndFontUtil.toFontString("Arial", -1));
        assertEquals("Arial, 10", ColorAndFontUtil.toFontString("Arial", 10));
        assertEquals("Arial, bold, italic", ColorAndFontUtil.toFontString("Arial", true, true));
        assertEquals("Arial, bold", ColorAndFontUtil.toFontString("Arial", true, false));
        assertEquals("Arial, italic", ColorAndFontUtil.toFontString("Arial", false, true));
        assertEquals("Arial, bold, italic", ColorAndFontUtil.toFontString("Arial", -1, true, true));
        assertEquals("Arial, 10, bold, italic", ColorAndFontUtil.toFontString("Arial", 10, true, true));
    }

    @Test
    public void testRgbToHex() {
        assertEquals("#ff0000", ColorAndFontUtil.toHex(255, 0, 0));
        assertEquals("#ffff00", ColorAndFontUtil.toHex(255, 255, 0));
        assertEquals("#ffffff", ColorAndFontUtil.toHex(255, 255, 255));
        assertEquals("#00ff00", ColorAndFontUtil.toHex(0, 255, 0));
        assertEquals("#00ffff", ColorAndFontUtil.toHex(0, 255, 255));
        assertEquals("#0000ff", ColorAndFontUtil.toHex(0, 0, 255));
    }

    @Test
    public void testIsHex() {
        assertTrue(ColorAndFontUtil.isHex("#123456"));
        assertTrue(ColorAndFontUtil.isHex("#1234ff"));
        assertTrue(ColorAndFontUtil.isHex("#aaccff"));
        assertFalse(ColorAndFontUtil.isHex("#aaccf"));
        assertFalse(ColorAndFontUtil.isHex("#aacc"));
        assertFalse(ColorAndFontUtil.isHex("#aac"));
        assertFalse(ColorAndFontUtil.isHex("#aa"));
        assertFalse(ColorAndFontUtil.isHex("#a"));
        assertFalse(ColorAndFontUtil.isHex("#"));
        assertFalse(ColorAndFontUtil.isHex(""));
        assertFalse(ColorAndFontUtil.isHex(null));
    }

    @Test
    public void testIsVariable() {
        assertTrue(ColorAndFontUtil.isVariable("${aa}"));
        assertTrue(ColorAndFontUtil.isVariable("${a-a}"));
        assertTrue(ColorAndFontUtil.isVariable("${a.a}"));
        assertTrue(ColorAndFontUtil.isVariable("${a.a-x}"));
        assertFalse(ColorAndFontUtil.isVariable("{a.a-x}"));
        assertFalse(ColorAndFontUtil.isVariable("${}"));
        assertFalse(ColorAndFontUtil.isVariable("${$}"));
        assertFalse(ColorAndFontUtil.isVariable("${aaa"));
    }
}
