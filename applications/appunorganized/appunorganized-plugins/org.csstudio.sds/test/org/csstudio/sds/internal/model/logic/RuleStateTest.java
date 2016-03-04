/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.internal.model.logic;

import static org.junit.Assert.assertEquals;

import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for class {@link org.csstudio.sds.internal.model.logic.RuleState}.
 *
 * @author Alexander Will & Sven Wende
 * @version $Revision: 1.3 $
 *
 */
public final class RuleStateTest {

    /**
     * A test channel reference.
     */
    private ParameterDescriptor _channelReference1;

    /**
     * A test channel reference.
     */
    private ParameterDescriptor _channelReference2;

    /**
     * A test rule state.
     */
    private RuleState _state;

    /**
     */
    @Before
    public void setUp() {
        _channelReference1 = new ParameterDescriptor("channel1", "1.0"); //$NON-NLS-1$
        _channelReference2 = new ParameterDescriptor("channel2", "aa"); //$NON-NLS-1$

        _state = new RuleState(new ParameterDescriptor[] { _channelReference1,
                _channelReference2 });
    }

    /**
     * Test method for class
     * {@link org.csstudio.sds.internal.model.logic.RuleState}.
     */
    @Test
    public void testState() {
        Object[] recentValues = _state.getRecentParameterValues();

        assertEquals(2, recentValues.length);
        assertEquals("1.0", recentValues[0]);
        assertEquals("aa", recentValues[1]);
//        assertNull(recentValues[0]);
//        assertNull(recentValues[1]);

        // update value 1
        Object value1 = new Object();
        _state.cacheParameterValue(_channelReference1, value1);

        // check
        recentValues = _state.getRecentParameterValues();

        assertEquals(2, recentValues.length);
        assertEquals(value1, recentValues[0]);
        assertEquals("aa", recentValues[1]);
//        assertNull(recentValues[1]);

        // update value 2
        Object value2 = new Object();
        _state.cacheParameterValue(_channelReference2, value2);

        // check
        recentValues = _state.getRecentParameterValues();

        assertEquals(2, recentValues.length);
        assertEquals(value1, recentValues[0]);
        assertEquals(value2, recentValues[1]);
    }
}
