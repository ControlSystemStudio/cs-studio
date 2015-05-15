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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.csstudio.sds.model.DisplayModel;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLObject;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 05.09.2007
 */
public class ADLObjectTest {

    private ADLWidget _adlObjectString;
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _adlObjectString = new ADLWidget("   object {"+"\r\n"+
                            "x=10"+"\r\n"+
                            "y=60"+"\r\n"+
                            "width=1200"+"\r\n"+
                            "height=880"+"\r\n"+
                            "}",null,0);
    }

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.widgetparts.ADLObject#ADLObject(java.lang.String[])}.
     */
    @Test
    public void testADLObject() {
        ADLObject testObj = null;
        try {
            testObj = new ADLObject(_adlObjectString, new DisplayModel());
            // Positive
            assertNotNull("ADLObject is Null",testObj);
            // check x
            assertNotNull("ADLObject x is Null", testObj.getX());
            assertEquals("x is not a Property --" + testObj.getX(), "10", testObj.getX());
            // check y
            assertNotNull("ADLObject y is Null", testObj.getY());
            assertEquals("y is not a Property --" + testObj.getY(), "60", testObj.getY());
            // check width
            assertNotNull("ADLObject width is Null", testObj.getWidth());
            assertEquals("y is not a Property --" + testObj.getWidth(), "1200", testObj.getWidth());

            // check height
            assertNotNull("ADLObject height is Null",testObj.getHeight());
            assertEquals("height is not a Propertheight --"+testObj.getHeight(), "property", testObj.getHeight());
            // Negative
//            assertNull("Illegal index (-1) return is not Null",testObj.getAdlObjects(-1));
//            assertNull("Illegal index (4) return is not Null",testObj.getAdlObjects(4));
//            assertNull("Illegal index (Integer.MAX_VALUE) return is not Null",testObj.getAdlObjects(Integer.MAX_VALUE));
        } catch (final Exception e) {
            e.printStackTrace();
            fail("ADLObject dislike the adlObjectString");
        }
        try{
            testObj = null;
//            testObj = new ADLObject(new String[] {"objekt {","x=10"});
            fail("ADLObject acceppt the wrong adlObjectString");
        } catch (final Exception e) {
            assertNull("ADLObject don't acceppt the adlObjectString but Object is not Null",testObj);
        }
        try{
            testObj = null;
//            testObj = new ADLObject(new String[] {"object {","x:10"});
            fail("ADLObject acceppt the wrong adlObjectString");
        } catch (final Exception e) {
            assertNull("ADLObject don't acceppt the adlObjectString but Object is not Null",testObj);
        }
        try{
            testObj = null;
//            testObj = new ADLObject(new String[] {"object {","xy=10,11"});
            fail("ADLObject acceppt the wrong adlObjectString");
        } catch (final Exception e) {
            assertNull("ADLObject don't acceppt the adlObjectString but Object is not Null",testObj);
        }

    }

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.widgetparts.ADLObject#getAdlObject()}.
     */
    @Test
    public void testGetAdlObject() {
//        try{
//            ADLObject testObj = new ADLObject(adlObejectString);
//            // Positive
//            assertNotNull("ADLObject is Null",testObj);
//            // check x
//            assertNotNull("ADLObject x is Null",testObj.getX());
//            assertEquals("x is not a Property --"+testObj.getX().getName(), "property", testObj.getX().getName());
//            assertEquals("x isn't a Integer --"+testObj.getX().getAttributeValue("type"), "sds.integer", testObj.getX().getAttributeValue("type"));
//            assertEquals("x isn't position.x --"+testObj.getX().getAttributeValue("id"), "position.x", testObj.getX().getAttributeValue("id"));
//            assertEquals("Make wrong x value --"+testObj.getX().getAttributeValue("value"), "10", testObj.getX().getAttributeValue("value"));
//            // check y
//            assertNotNull("ADLObject y is Null",testObj.getY());
//            assertEquals("y is not a Property --"+testObj.getY().getName(), "property", testObj.getY().getName());
//            assertEquals("y isn't a Integer --"+testObj.getY().getAttributeValue("type"), "sds.integer", testObj.getY().getAttributeValue("type"));
//            assertEquals("y isn't positoion.y --"+testObj.getY().getAttributeValue("id"), "position.y", testObj.getY().getAttributeValue("id"));
//            assertEquals("Make wrong y value --"+testObj.getY().getAttributeValue("value"), "60", testObj.getY().getAttributeValue("value"));
//            // check width
//            assertNotNull("ADLObject width is Null",testObj.getAdlObjects(2));
//            assertEquals("width is not a Propertwidth --"+testObj.getAdlObjects(2).getName(), "property", testObj.getAdlObjects(2).getName());
//            assertEquals("width isn't a Integer--"+testObj.getAdlObjects(2).getAttributeValue("type"), "sds.integer", testObj.getAdlObjects(2).getAttributeValue("type"));
//            assertEquals("width isn't width--"+testObj.getAdlObjects(2).getAttributeValue("id"), "width", testObj.getAdlObjects(2).getAttributeValue("id"));
//            assertEquals("Make wrong width value --"+testObj.getAdlObjects(2).getAttributeValue("value"), "1200", testObj.getAdlObjects(2).getAttributeValue("value"));
//            // check height
//            assertNotNull("ADLObject height is Null",testObj.getAdlObjects(3));
//            assertEquals("height is not a Propertheight --"+testObj.getAdlObjects(3).getName(), "property", testObj.getAdlObjects(3).getName());
//            assertEquals("height isn't a Integer--"+testObj.getAdlObjects(3).getAttributeValue("type"), "sds.integer", testObj.getAdlObjects(3).getAttributeValue("type"));
//            assertEquals("height isn't height--"+testObj.getAdlObjects(3).getAttributeValue("id"), "height", testObj.getAdlObjects(3).getAttributeValue("id"));
//            assertEquals("Make wrong height value --"+testObj.getAdlObjects(3).getAttributeValue("value"), "880", testObj.getAdlObjects(3).getAttributeValue("value"));
//            // Negative
//            assertNull("Illegal index (-1) return is not Null",testObj.getAdlObjects(-1));
//            assertNull("Illegal index (4) return is not Null",testObj.getAdlObjects(4));
//            assertNull("Illegal index (Integer.MAX_VALUE) return is not Null",testObj.getAdlObjects(Integer.MAX_VALUE));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("ADLObject dislike the adlObjectString");
//        }
    }

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.widgetparts.ADLObject#getAdlObjects(int)}.
     */
    @Test
    public void testGetAdlObjects() {
        try{
            final ADLObject testObj = null;
//            ADLObject testObj = new ADLObject(_adlObejectString);
            // Positive
            assertNotNull("ADLObject is Null",testObj);
//            assertTrue(testObj.getAdlObject().length==4);
//            // check x
//            assertNotNull("ADLObject x is Null",testObj.getAdlObject()[0]);
//            assertEquals("x is not a Property --"+testObj.getAdlObject()[0].getName(), "property", testObj.getAdlObject()[0].getName());
//            assertEquals("x isn't a Integer --"+testObj.getAdlObject()[0].getAttributeValue("type"), "sds.integer", testObj.getAdlObject()[0].getAttributeValue("type"));
//            assertEquals("x isn't position.x --"+testObj.getAdlObject()[0].getAttributeValue("id"), "position.x", testObj.getAdlObject()[0].getAttributeValue("id"));
//            assertEquals("Make wrong x value --"+testObj.getAdlObject()[0].getAttributeValue("value"), "10", testObj.getAdlObject()[0].getAttributeValue("value"));
//            // check y
//            assertNotNull("ADLObject y is Null",testObj.getAdlObject()[1]);
//            assertEquals("y is not a Property --"+testObj.getAdlObject()[1].getName(), "property", testObj.getAdlObject()[1].getName());
//            assertEquals("y isn't a Integer --"+testObj.getAdlObject()[1].getAttributeValue("type"), "sds.integer", testObj.getAdlObject()[1].getAttributeValue("type"));
//            assertEquals("y isn't position.y --"+testObj.getAdlObject()[1].getAttributeValue("id"), "position.y", testObj.getAdlObject()[1].getAttributeValue("id"));
//            assertEquals("Make wrong y value --"+testObj.getAdlObject()[1].getAttributeValue("value"), "60", testObj.getAdlObject()[1].getAttributeValue("value"));
//            // check width
//            assertNotNull("ADLObject width is Null",testObj.getAdlObject()[2]);
//            assertEquals("width is not a Propertwidth --"+testObj.getAdlObject()[2].getName(), "property", testObj.getAdlObject()[2].getName());
//            assertEquals("width isn't a Integer--"+testObj.getAdlObject()[2].getAttributeValue("type"), "sds.integer", testObj.getAdlObject()[2].getAttributeValue("type"));
//            assertEquals("width isn't width--"+testObj.getAdlObject()[2].getAttributeValue("id"), "width", testObj.getAdlObject()[2].getAttributeValue("id"));
//            assertEquals("Make wrong width value --"+testObj.getAdlObject()[2].getAttributeValue("value"), "1200", testObj.getAdlObject()[2].getAttributeValue("value"));
//            // check height
//            assertNotNull("ADLObject height is Null",testObj.getAdlObject()[3]);
//            assertEquals("height is not a Propertheight --"+testObj.getAdlObject()[3].getName(), "property", testObj.getAdlObject()[3].getName());
//            assertEquals("height isn't a Integer--"+testObj.getAdlObject()[3].getAttributeValue("type"), "sds.integer", testObj.getAdlObject()[3].getAttributeValue("type"));
//            assertEquals("height isn't height--"+testObj.getAdlObject()[3].getAttributeValue("id"), "height", testObj.getAdlObject()[3].getAttributeValue("id"));
//            assertEquals("Make wrong height value --"+testObj.getAdlObject()[3].getAttributeValue("value"), "880", testObj.getAdlObject()[3].getAttributeValue("value"));

        } catch (final Exception e) {
            e.printStackTrace();
            fail("ADLObject dislike the adlObjectString");
        }

    }

}
