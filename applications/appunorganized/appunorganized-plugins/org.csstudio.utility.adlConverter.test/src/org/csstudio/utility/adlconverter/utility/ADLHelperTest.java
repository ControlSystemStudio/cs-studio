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

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 07.09.2007
 */
public class ADLHelperTest {

    private ADLWidget _colors = new ADLWidget( "\"color map\" {"+"\r\n"+
            "ncolors=5"+"\r\n"+
            "colors {"+"\r\n"+
            "010203,"+"\r\n"+
            "ececec,"+"\r\n"+
            "dadada,"+"\r\n"+
            "c8c8c8,"+"\r\n"+
            "99Ffc7,"+"\r\n"+
      "}",null,0);

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetFontSize(){
        String font = "Times New Roman";
        String text = "hallo";
        int maxHigh = 20;
        int maxWidth = 100;
        String style = "0";
        try{
            Assert.assertTrue(ADLHelper.getFontSize(font, text, maxHigh, maxWidth, style)>1);
        }catch (Exception e){
            fail("Dislike the font property settings");
        }

        try{
            font = "gibt es nicht";
            text = "°!\"§$%&/()=?`´\\ß^+*~#',.-;:_@€<>|";
            maxHigh = 20;
            maxWidth = 100;
            style = "-1";

            Assert.assertTrue(ADLHelper.getFontSize(font, text, maxHigh, maxWidth, style)>1);
        }catch (Exception e){
            fail("Dislike the font property settings");
        }

        try{
            font = null;
            text = null;
            maxHigh = 20;
            maxWidth = 100;
            style = null;

            Assert.assertTrue(ADLHelper.getFontSize(font, text, maxHigh, maxWidth, style)>1);
        }catch (Exception e){
            fail("Dislike the font property settings");
        }

        try{
            font = "Times New Roman";
            text = "hallo";
            maxHigh = 0;
            maxWidth = 0;
            style = "0";

            Assert.assertTrue(ADLHelper.getFontSize(font, text, maxHigh, maxWidth, style)>0);
        }catch (Exception e){
            fail("Dislike the font property settings");
        }

    }

    @Test
    public void testGetRGB(){

    }

    @Test
    public void testSetChan(){

    }

    /**
     * Test method for
     * {@link org.csstudio.utility.adlconverter.utility.ADLHelper#setColorMap(java.lang.String[])}.
     */
    @Test
    public void testSetColorMap(){
        // Positive
//        try {
//            ADLHelper.setColorMap(_colors);
//            Element testGetObj = ADLHelper.getColorElement("0");
//            assertNotNull(testGetObj);
//            assertEquals("1", testGetObj.getAttributeValue("red"));
//            assertEquals("2", testGetObj.getAttributeValue("green"));
//            assertEquals("3", testGetObj.getAttributeValue("blue"));
//            testGetObj = ADLHelper.getColorElement("4");
//            assertNotNull(testGetObj);
//            assertEquals("153", testGetObj.getAttributeValue("red"));
//            assertEquals("255", testGetObj.getAttributeValue("green"));
//            assertEquals("199", testGetObj.getAttributeValue("blue"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Dislike the Colorstring");
//        }
    }

    @Test
    public void testSetConnectionState(){

    }

    @Test
    public void testCheckAndSetLayer(){

    }

    @Test
    public void testCleanString(){

    }
}
