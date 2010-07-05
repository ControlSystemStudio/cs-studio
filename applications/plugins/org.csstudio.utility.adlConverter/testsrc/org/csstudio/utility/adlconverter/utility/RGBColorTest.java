/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id$
 */
package org.csstudio.utility.adlconverter.utility;

import junit.framework.TestCase;

import org.junit.Test;


/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 10.09.2007
 */
public class RGBColorTest extends TestCase {




    private static final String NEW_RGB_COLOR_IS_NULL = "new RGBColor is Null";

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.RGBColor#RGBColor(java.lang.String)}.
     */
    @Test
    public void testRGBColor() {
        RGBColor rgb = null;
        try {
           rgb = new RGBColor("010203"); //$NON-NLS-1$
           assertNotNull(NEW_RGB_COLOR_IS_NULL, rgb);
        } catch (final Exception e) {
            e.printStackTrace();
            fail("unexpected Color String");
        }
        rgb = null;
        try {
            rgb = new RGBColor("01203"); //$NON-NLS-1$
            fail("accept worng colorString");
            assertNotNull(NEW_RGB_COLOR_IS_NULL,rgb);
         } catch (final Exception e) {
             assertNull("rgb is not null and is generate with wrong color string", rgb);
         }
         rgb = null;
         try {
             rgb = new RGBColor("0120321");
             fail("accept worng colorString");
             assertNotNull(NEW_RGB_COLOR_IS_NULL,rgb);
          } catch (final Exception e) {
              assertNull("rgb is not null and is generate with wrong color string",rgb);
          }
          rgb = null;
          try {
              rgb = new RGBColor("HelgeR");
              fail("accept worng colorString");
              assertNotNull(NEW_RGB_COLOR_IS_NULL,rgb);
           } catch (final Exception e) {
               assertNull("rgb is not null and is generate with wrong color string",rgb);
           }


    }

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.RGBColor#getRed()}.
     */
    @Test
    public void testGetRed() {
        try {
            RGBColor rgb = new RGBColor("010203");
            assertNotNull(NEW_RGB_COLOR_IS_NULL,rgb);
            assertEquals(1, rgb.getRed());
            rgb = new RGBColor(Messages.RGBColorTest_18);
            assertNotNull(Messages.RGBColorTest_19,rgb);
            assertEquals(153, rgb.getRed());
            rgb = new RGBColor(Messages.RGBColorTest_21);
            assertNotNull(Messages.RGBColorTest_22,rgb);
            assertEquals(107, rgb.getRed());
            rgb = new RGBColor(Messages.RGBColorTest_24);
            assertNotNull(Messages.RGBColorTest_25,rgb);
            assertEquals(Messages.RGBColorTest_26, String.valueOf(rgb.getRed()));
            rgb = new RGBColor(Messages.RGBColorTest_27);
            assertNotNull(Messages.RGBColorTest_28,rgb);
            assertEquals(Messages.RGBColorTest_29, String.valueOf(rgb.getRed()));
         } catch (final Exception e) {
             e.printStackTrace();
             fail(Messages.RGBColorTest_30);
         }

    }

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.RGBColor#getGreen()}.
     */
    @Test
    public void testGetGreen() {
        try {
            RGBColor rgb = new RGBColor(Messages.RGBColorTest_31);
            assertNotNull(Messages.RGBColorTest_32,rgb);
            assertEquals(Messages.RGBColorTest_33, String.valueOf(rgb.getGreen()));
            rgb = new RGBColor(Messages.RGBColorTest_34);
            assertNotNull(Messages.RGBColorTest_35,rgb);
            assertEquals(Messages.RGBColorTest_36, String.valueOf(rgb.getGreen()));
            rgb = new RGBColor(Messages.RGBColorTest_37);
            assertNotNull(Messages.RGBColorTest_38,rgb);
            assertEquals(Messages.RGBColorTest_39, String.valueOf(rgb.getGreen()));
            rgb = new RGBColor(Messages.RGBColorTest_40);
            assertNotNull(Messages.RGBColorTest_41,rgb);
            assertEquals(Messages.RGBColorTest_42, String.valueOf(rgb.getGreen()));
            rgb = new RGBColor(Messages.RGBColorTest_43);
            assertNotNull(Messages.RGBColorTest_44,rgb);
            assertEquals(Messages.RGBColorTest_45, String.valueOf(rgb.getGreen()));
         } catch (final Exception e) {
             e.printStackTrace();
             fail(Messages.RGBColorTest_46);
         }

    }

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.RGBColor#getBlue()}.
     */
    @Test
    public void testGetBlue() {
        try {
            RGBColor rgb = new RGBColor(Messages.RGBColorTest_47);
            assertNotNull(Messages.RGBColorTest_48,rgb);
            assertEquals(Messages.RGBColorTest_49, String.valueOf(rgb.getBlue()));
            rgb = new RGBColor(Messages.RGBColorTest_50);
            assertNotNull(Messages.RGBColorTest_51,rgb);
            assertEquals(Messages.RGBColorTest_52, String.valueOf(rgb.getBlue()));
            rgb = new RGBColor(Messages.RGBColorTest_53);
            assertNotNull(Messages.RGBColorTest_54,rgb);
            assertEquals(Messages.RGBColorTest_55, String.valueOf(rgb.getBlue()));
            rgb = new RGBColor(Messages.RGBColorTest_56);
            assertNotNull(Messages.RGBColorTest_57,rgb);
            assertEquals(Messages.RGBColorTest_58, String.valueOf(rgb.getBlue()));
            rgb = new RGBColor(Messages.RGBColorTest_59);
            assertNotNull(Messages.RGBColorTest_60,rgb);
            assertEquals(Messages.RGBColorTest_61, String.valueOf(rgb.getBlue()));
         } catch (final Exception e) {
             e.printStackTrace();
             fail(Messages.RGBColorTest_62);
         }
    }

}
