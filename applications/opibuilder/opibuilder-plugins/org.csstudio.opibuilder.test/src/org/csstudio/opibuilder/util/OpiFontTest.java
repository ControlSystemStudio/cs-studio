package org.csstudio.opibuilder.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OpiFontTest {

    private final int FONT_SIZE = 15;

    private final int DUMMY_STYLE = 5;

    private final Point DUMMY_DPI = new Point(100, 100);

    private OPIFont testOPIFont;

    /**
     * This mock uses protected methods in OPIFont to override static calls
     * that would otherwise prevent testing.
     * All relevant logic should still be the same.
     */
    private class TestableOPIFont extends OPIFont {
        private Point dpi;
        public TestableOPIFont(FontData fontData, Point dpi) {
            super(fontData);
            this.dpi = dpi;
        }
        public TestableOPIFont(OPIFont font) {
            super(font);
        }
        @Override
        protected boolean getDefaultIsInPixels() {
            return false;
        }
        @Override
        protected Point getDPI() {
            return this.dpi;
        }
    }

    @Before
    public void setUp() {
        this.testOPIFont = new TestableOPIFont(new FontData("Arial", FONT_SIZE, DUMMY_STYLE), DUMMY_DPI);
    }

    @Test
    public void testCopyConstructorCreatesNewObjectWithSameHashCodeThatEvaluatesAsEqual() {
        OPIFont copyFont = new TestableOPIFont(testOPIFont);
        assertEquals(testOPIFont, copyFont);
        assertTrue(testOPIFont != copyFont);
        assertTrue(testOPIFont.hashCode() == copyFont.hashCode());
    }

    @Test
    public void testSizeInPointsIsEqualToRawFontData() {
        // Returned size should be 10 points
        assertEquals(FONT_SIZE, testOPIFont.getFontData().getHeight());
    }

    @Test
    public void testSizeInPixelsIsDifferentToRawFontData() throws Exception {
        // Set font size to be in pixels
        testOPIFont.setSizeInPixels(true);
        // Returned size should be 10 pixels converted to points based on the display DPI
        int expected = Math.round((float) FONT_SIZE * OPIFont.POINTS_PER_INCH / DUMMY_DPI.y);
        assertEquals(expected, testOPIFont.getFontData().getHeight());
    }

    @Test
    public void testFontSizeConversionIsCorrectForDifferentSizes() throws Exception {
        Point dpi = new Point(96, 96);
        // These answers have been manually calculated using the above dpi.
        List<Integer> pointSizes = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18));
        List<Integer> pixelSizes = new ArrayList<>(Arrays.asList(8, 8, 9, 10, 11, 11, 12, 13, 14));
        for (int i = 0; i < pointSizes.size(); i++) {
            OPIFont font = new TestableOPIFont(new FontData("Arial", pointSizes.get(i), DUMMY_STYLE), dpi);
            font.setSizeInPixels(true);
            assertEquals(font.getFontData().getHeight(), pixelSizes.get(i).intValue());
        }
    }

    @Test
    public void testGetRawFontDataDoesNotChangeIfSizeInPixelsIsTrue() throws Exception {
        // Set font size to be in pixels
        testOPIFont.setSizeInPixels(true);
        // Returned size should still be 10 pixels
        assertEquals(FONT_SIZE, testOPIFont.getRawFontData().getHeight());
    }

}
