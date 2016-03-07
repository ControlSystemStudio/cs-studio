/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.platform.internal.simpledal;

import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.StringProperty;
import org.csstudio.dal.impl.DefaultApplicationContext;
import org.csstudio.dal.spi.DefaultPropertyFactoryService;
import org.csstudio.dal.spi.LinkPolicy;
import org.csstudio.dal.spi.PropertyFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DalCacheBugTest {

    private PropertyFactory _propertyFactory;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // set system properties
        System.setProperty("dal.plugs", "EPICS");
        System.setProperty("dal.plugs.default", "EPICS");
        System.setProperty("dal.propertyfactory.EPICS",
                "org.csstudio.dal.epics.PropertyFactoryImpl");

        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
                "YES");
        System.setProperty(
                "com.cosylab.epics.caj.CAJContext.connection_timeout", "30.0");
        System.setProperty("com.cosylab.epics.caj.CAJContext.beacon_period",
                "15.0");
        System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port",
                "5065");
        System.setProperty("com.cosylab.epics.caj.CAJContext.server_port",
                "5064");
        System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes",
                "16384");

        // get the property _factory
        _propertyFactory = DefaultPropertyFactoryService.getPropertyFactoryService()
                .getPropertyFactory(new DefaultApplicationContext("Test"),
                        LinkPolicy.ASYNC_LINK_POLICY, "EPICS");

    }

    /**
     * @throws Exception
     */
    @Test
    public void testCacheBug() throws Exception {
        String pv = "Random:1";

        // get the pv as DoubleProperty
        DoubleProperty doubleProperty = _propertyFactory.getProperty(pv, DoubleProperty.class, null);
        assertNotNull(doubleProperty);
        assertTrue(doubleProperty instanceof DoubleProperty);

        // get the same pv as StringProperty
        StringProperty stringProperty = _propertyFactory.getProperty(pv, StringProperty.class, null);
        assertNotNull(stringProperty);
        assertTrue(stringProperty instanceof StringProperty);


        // ergo -> ClassCastException
    }
}
