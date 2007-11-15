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

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 10.09.2007
 */
public class RGBColorTest extends TestCase {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.RGBColor#RGBColor(java.lang.String)}.
     */
    @Test
    public void testRGBColor() {
        RGBColor rgb = null;
        try {
           rgb = new RGBColor("010203");
           assertNotNull("new RGBColor is Null",rgb);
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexecpt Color String");
        }
        rgb = null;
        try {
            rgb = new RGBColor("01203");
            fail("accept worng colorString");
            assertNotNull("new RGBColor is Null",rgb);
         } catch (Exception e) {
             assertNull("rgb is not null and is generate with wrong color string",rgb);
         }
         rgb = null;
         try {
             rgb = new RGBColor("0120321");
             fail("accept worng colorString");
             assertNotNull("new RGBColor is Null",rgb);
          } catch (Exception e) {
              assertNull("rgb is not null and is generate with wrong color string",rgb);
          }
          rgb = null;
          try {
              rgb = new RGBColor("HelgeR");
              fail("accept worng colorString");
              assertNotNull("new RGBColor is Null",rgb);
           } catch (Exception e) {
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
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("1", rgb.getRed());
            rgb = new RGBColor("990203");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("153", rgb.getRed());
            rgb = new RGBColor("6B0203");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("107", rgb.getRed());
            rgb = new RGBColor("a70203");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("167", rgb.getRed());
            rgb = new RGBColor("cF0203");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("207", rgb.getRed());
         } catch (Exception e) {
             e.printStackTrace();
             fail("unexecpt Color String");
         }

    }

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.RGBColor#getGreen()}.
     */
    @Test
    public void testGetGreen() {
        try {
            RGBColor rgb = new RGBColor("010203");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("2", rgb.getGreen());
            rgb = new RGBColor("019803");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("152", rgb.getGreen());
            rgb = new RGBColor("006A03");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("106", rgb.getGreen());
            rgb = new RGBColor("00b603");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("182", rgb.getGreen());
            rgb = new RGBColor("23fD03");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("253", rgb.getGreen());
         } catch (Exception e) {
             e.printStackTrace();
             fail("unexecpt Color String");
         }

    }

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.RGBColor#getBlue()}.
     */
    @Test
    public void testGetBlue() {
        try {
            RGBColor rgb = new RGBColor("010203");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("3", rgb.getBlue());
            rgb = new RGBColor("010098");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("152", rgb.getBlue());
            rgb = new RGBColor("00116A");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("106", rgb.getBlue());
            rgb = new RGBColor("0022b6");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("182", rgb.getBlue());
            rgb = new RGBColor("2333fD");
            assertNotNull("new RGBColor is Null",rgb);
            assertEquals("253", rgb.getBlue());
         } catch (Exception e) {
             e.printStackTrace();
             fail("unexecpt Color String");
         }
    }

}
