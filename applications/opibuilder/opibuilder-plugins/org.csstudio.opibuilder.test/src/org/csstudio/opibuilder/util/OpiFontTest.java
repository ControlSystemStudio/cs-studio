package org.csstudio.opibuilder.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OpiFontTest {

    private final int FONT_SIZE = 10;

    private final int DUMMY_STYLE = 5;

    private final Point DUMMY_DPI = new Point(100, 100);

    private OPIFont testOPIFont;

    /**
     * This mock uses protected methods in OPIFont to override static calls
     * that would otherwise prevent testing.
     * All relevant logic should still be the same.
     */
    private class TestableOPIFont extends OPIFont {
        public TestableOPIFont(FontData fontData) {
            super(fontData);
        }
        public TestableOPIFont(OPIFont font) {
            super(font);
        }
        @Override
        protected boolean getDefaultIsInPixels() {
            return false;
        }
        protected Point getDPI() {
            return DUMMY_DPI;
        }
    }

    @Before
    public void setUp() {
        this.testOPIFont = new TestableOPIFont(new FontData("Arial", FONT_SIZE, DUMMY_STYLE));
    }

    @Test
    public void testCopyConstructor() {
        OPIFont copyFont = new TestableOPIFont(testOPIFont);
        assertEquals(testOPIFont, copyFont);
        assertTrue(testOPIFont != copyFont);
        assertTrue(testOPIFont.hashCode() == copyFont.hashCode());
    }

    @Test
    public void testSizeInPoints() {
        // Returned size should be 10 points
        assertEquals(FONT_SIZE, testOPIFont.getFontData().getHeight());
    }

    @Test
    public void testSizeInPixels() throws Exception {
        // Set font size to be in pixels
        testOPIFont.setSizeInPixels(true);
        // Returned size should be 10 pixels converted to points based on the display DPI
        int expected = FONT_SIZE * OPIFont.POINTS_PER_INCH / DUMMY_DPI.y;
        assertEquals(expected, testOPIFont.getFontData().getHeight());
    }

    @Test
    public void testGetRawFontData() throws Exception {
        // Set font size to be in pixels
        testOPIFont.setSizeInPixels(true);
        // Returned size should still be 10 pixels
        assertEquals(FONT_SIZE, testOPIFont.getRawFontData().getHeight());
    }

}
