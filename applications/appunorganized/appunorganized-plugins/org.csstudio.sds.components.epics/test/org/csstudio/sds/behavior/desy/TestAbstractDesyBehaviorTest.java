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
package org.csstudio.sds.behavior.desy;

import static org.junit.Assert.assertEquals;

import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.simple.Severity;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 03.11.2011
 */
public class TestAbstractDesyBehaviorTest {

    private TestAbstractDesyBehavior _testAbstractDesyBehavior;

    @Before
    public void setUp() throws Exception {
        _testAbstractDesyBehavior = new TestAbstractDesyBehavior();
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyBehavior#determineBorderStyle(org.csstudio.dal.context.ConnectionState)}.
     */
    @Test
    public void testDetermineBorderStyle() {
        BorderStyleEnum determineBorderStyle = _testAbstractDesyBehavior.determineBorderStyle(null);
        assertEquals(BorderStyleEnum.DASH_DOT, determineBorderStyle);
        determineBorderStyle = _testAbstractDesyBehavior.determineBorderStyle(ConnectionState.CONNECTED);
        assertEquals(BorderStyleEnum.NONE, determineBorderStyle);
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyBehavior#determineBackgroundColor(org.csstudio.dal.context.ConnectionState)}.
     */
    @Test
    public void testDetermineBackgroundColor() {
        String determineBackgroundColor = _testAbstractDesyBehavior.determineBackgroundColor(null);
        assertEquals("${Initial}", determineBackgroundColor);
        determineBackgroundColor = _testAbstractDesyBehavior.determineBackgroundColor(ConnectionState.CONNECTING);
        assertEquals("${Initial}", determineBackgroundColor);
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyBehavior#determineBorderColor(org.csstudio.dal.context.ConnectionState)}.
     */
    @Test
    public void testDetermineBorderColor() {
        String determineBorderColor = _testAbstractDesyBehavior.determineBorderColor(null);
        assertEquals("${Initial}", determineBorderColor);
        determineBorderColor = _testAbstractDesyBehavior.determineBorderColor(ConnectionState.CONNECTION_FAILED);
        assertEquals("${VerbAbbr}", determineBorderColor);
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyBehavior#determineBorderWidth(org.csstudio.dal.context.ConnectionState)}.
     */
    @Test
    public void testDetermineBorderWidth() {
        Integer determineBorderWidth = _testAbstractDesyBehavior.determineBorderWidth(null);
        assertEquals(Integer.valueOf(1), determineBorderWidth);
        determineBorderWidth = _testAbstractDesyBehavior.determineBorderWidth(ConnectionState.CONNECTION_LOST);
        assertEquals(Integer.valueOf(1), determineBorderWidth);
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyBehavior#determineBorderStyleBySeverity(org.csstudio.dal.simple.Severity)}.
     */
    @Test
    public void testDetermineBorderStyleBySeverity() {
        BorderStyleEnum determineBorderStyleBySeverity = _testAbstractDesyBehavior.determineBorderStyleBySeverity(null);
        assertEquals(BorderStyleEnum.LINE, determineBorderStyleBySeverity);
        final Severity severity = new DynamicValueCondition(DynamicValueState.ALARM);
        determineBorderStyleBySeverity = _testAbstractDesyBehavior.determineBorderStyleBySeverity(severity);
        assertEquals(BorderStyleEnum.LINE, determineBorderStyleBySeverity);
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyBehavior#determineBorderWidthBySeverity(org.csstudio.dal.simple.Severity)}.
     */
    @Test
    public void testDetermineBorderWidthBySeverity() {
        Integer determineBorderWidth = _testAbstractDesyBehavior.determineBorderWidthBySeverity(null);
        assertEquals(Integer.valueOf(3), determineBorderWidth);
        final Severity severity = new DynamicValueCondition(DynamicValueState.ALARM);
        determineBorderWidth = _testAbstractDesyBehavior.determineBorderWidthBySeverity(severity);
        assertEquals(Integer.valueOf(3), determineBorderWidth);
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyBehavior#determineColorBySeverity(org.csstudio.dal.simple.Severity, java.lang.String)}.
     */
    @Test
    public void testDetermineColorBySeverity() {
        String determineColorBySeverity = _testAbstractDesyBehavior.determineColorBySeverity(null,null);
        assertEquals("#000000", determineColorBySeverity);
        Severity severity = new DynamicValueCondition(DynamicValueState.ALARM);
        determineColorBySeverity = _testAbstractDesyBehavior.determineColorBySeverity(severity, null);
        assertEquals("${Major}", determineColorBySeverity);
        determineColorBySeverity = _testAbstractDesyBehavior.determineColorBySeverity(severity, "Test1234");
        assertEquals("${Major}", determineColorBySeverity);
        severity = new DynamicValueCondition(DynamicValueState.NORMAL);
        determineColorBySeverity = _testAbstractDesyBehavior.determineColorBySeverity(severity, "Test1234");
        assertEquals("Test1234", determineColorBySeverity);
    }

}
