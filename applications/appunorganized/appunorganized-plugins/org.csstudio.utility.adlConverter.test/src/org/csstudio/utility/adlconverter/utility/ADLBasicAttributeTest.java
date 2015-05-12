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

import org.csstudio.utility.adlconverter.utility.widgetparts.ADLBasicAttribute;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 07.09.2007
 */
public class ADLBasicAttributeTest {

    private ADLWidget _adlBasicAttributeString;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _adlBasicAttributeString = new ADLWidget(
                "\"basic attribute\" {"+"\r\n"+
                    "clr=3"+"\r\n"+
                    "fill=\"outline\""+"\r\n"+
                    "width=2"+"\r\n"+
                    "style=\"dash\""+"\r\n"+
                "}",
                null,
                0
        );
        String colors =
                "\"color map\" {"+"\r\n"+
                "ncolors=5"+"\r\n"+
                "colors {"+"\r\n"+
                "010203,"+"\r\n"+
                "ececec,"+"\r\n"+
                "dadada,"+"\r\n"+
                "c8c8c8,"+"\r\n"+
                "99Ffc7,"+"\r\n"+
          "}" ;
        ADLHelper.setColorMap(new ADLWidget(colors,null,0));

    }

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.widgetparts.ADLBasicAttribute#ADLBasicAttribute(java.lang.String[])}.
     */
    @Test
    public void testADLBasicAttribute() {
        for (ADLWidget obj : _adlBasicAttributeString.getObjects()) {
            if (obj.isType("object")) { //$NON-NLS-1$
                try {
                    ADLBasicAttribute testBA = new ADLBasicAttribute(obj, null);
                    // Positive
                    assertNotNull("ADLBasicAttribute is Null",testBA);
//                    assertTrue(testBA.getAdlBasicAttributes().length==4);
                }catch (Exception e) {
                    e.printStackTrace();
                    fail("ADLBasicAttribute dislike the _adlBasicAttributeString");
                }
                fail("Not yet implemented");

            }
        }
    }

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.widgetparts.ADLBasicAttribute#getAdlBasicAttributes()}.
     */
    @Test
    public void testGetAdlBasicAttributes() {
        ADLBasicAttribute testBA = null;
        try {
//            testBA = new ADLBasicAttribute(_adlBasicAttributeString);
            // Positive
            assertNotNull("ADLBasicAttribute is Null",testBA);
//            assertTrue(testBA.getAdlBasicAttributes().length==4);
        }catch (Exception e) {
            e.printStackTrace();
            fail("ADLBasicAttribute dislike the _adlBasicAttributeString");
        }

        fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.csstudio.utility.adlconverter.utility.widgetparts.ADLBasicAttribute#getBasicAttribute(int)}.
     */
    @Test
    public void testGetBasicAttribute() {
        fail("Not yet implemented");
    }

}
