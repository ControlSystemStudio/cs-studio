/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.sds.internal.model;

import static org.junit.Assert.assertEquals;

import org.csstudio.sds.model.WidgetPropertyCategory;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 21.11.2011
 */
public class StringPropertyTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link org.csstudio.sds.internal.model.AbstractStringProperty#checkValue(java.lang.Object)}.
     */
    @Test
    public void testCheckValue() {
        final StringProperty stringProperty = new StringProperty("", WidgetPropertyCategory.FORMAT, "1");
        Object checkValue = stringProperty.checkValue(0d);
        assertEquals("0", checkValue);
        checkValue = stringProperty.checkValue(10d);
        assertEquals("10", checkValue);
        checkValue = stringProperty.checkValue(1000000000000000000d);
        assertEquals("1000000000000000000", checkValue);
        checkValue = stringProperty.checkValue(0.1d);
        assertEquals("0.1", checkValue);
        checkValue = stringProperty.checkValue(.0000000001d);
        assertEquals("0.0000000001", checkValue);
        checkValue = stringProperty.checkValue(.00000000000000000001d);
        assertEquals("0.00000000000000000001", checkValue);
    }

}
