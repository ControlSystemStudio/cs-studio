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
 /**
 *
 */
package org.csstudio.platform.internal.model.pvs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link SimpleNameParser}.
 *
 * @author Sven Wende
 *
 */
public class SimpleNameParserTest {
    private SimpleNameParser _epicsParser;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _epicsParser = new SimpleNameParser(ControlSystemEnum.EPICS);
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.model.pvs.DalNameParser#doParse(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testParse() {
        test("abc", ControlSystemEnum.EPICS, null, null, "abc");
        test("epics://abc", ControlSystemEnum.EPICS, null, null, "abc");
        test("epics://abc[cde]", ControlSystemEnum.EPICS, null, null,
                "abc[cde]");
        test("", ControlSystemEnum.UNKNOWN, null, null, "");
    }

    /**
     * Tests the specified raw name and checks whether the returned pv does
     * match the requirements.
     *
     * @param rawName
     *            the raw name, which is used as input for the parser
     * @param expectedCharacteristics
     *            the expected characteristic part
     * @param expectedDevice
     *            the expected device part
     * @param expectedProperty
     *            the expected property part
     */
    private void test(final String rawName, final ControlSystemEnum expectedControlSystem,
            final String expectedCharacteristics, final String expectedDevice,
            final String expectedProperty) {
        final IProcessVariableAddress pv = _epicsParser.parseRawName(rawName);

        assertNotNull(pv);
        assertEquals(expectedControlSystem, pv.getControlSystem());
        assertEquals(expectedProperty, pv.getProperty());
        assertEquals(expectedCharacteristics, pv.getCharacteristic());
        assertEquals(expectedDevice, pv.getDevice());
    }

}
