package org.csstudio.opibuilder.util;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OpiFontTest {

    private final int FONT_SIZE = 10;

    private final Point DUMMY_DPI = new Point(100, 100);

    @Test
    public void testSizeInPoints() {
        OPIFont opiFont = new OPIFont(new FontData("Arial", FONT_SIZE, 0)) {
            @Override  // Can't call this without the RCP running
            protected boolean getDefaultIsInPixels() {
                return false;
            }
        };
        // Returned size should be 10 points
        assertEquals(FONT_SIZE, opiFont.getFontData().getHeight());
    }

    @Test
    public void testSizeInPixels() throws Exception {
        OPIFont opiFont = new OPIFont(new FontData("Arial", FONT_SIZE, 0)) {
            @Override  // Can't call this without the RCP running
            protected boolean getDefaultIsInPixels() {
                return false;
            }
            protected Point getDPI() {
                return DUMMY_DPI;
            }
        };
        // Set font size to be in pixels
        opiFont.setSizeInPixels(true);
        // Returned size should be 10 pixels converted to points based on the display DPI
        int expected = FONT_SIZE * OPIFont.POINTS_PER_INCH / DUMMY_DPI.y;
        assertEquals(expected, opiFont.getFontData().getHeight());
    }

}
