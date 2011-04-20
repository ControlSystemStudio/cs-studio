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
package org.csstudio.domain.desy.epics.alarm;

import junit.framework.Assert;

import org.csstudio.domain.desy.alarm.IAlarm;
import org.csstudio.domain.desy.system.ControlSystemType;
import org.junit.Test;

/**
 * Tests the EPICS_V3 alarm class.
 *
 * @author bknerr
 * @since 16.02.2011
 */
public class EpicsAlarmUnitTest {

    @Test
    public void testParseFromAndToString() {
        final String rep = "ALARM(EPICS_V3:MINOR,COMM)";
        final IAlarm parsed = EpicsAlarm.parseFrom(rep);
        Assert.assertNotNull(parsed);
        Assert.assertEquals(ControlSystemType.EPICS_V3, parsed.getControlSystemType());
        Assert.assertEquals(rep, parsed.toString());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFromStringFailed() {
        final String rep = "ALRM(EPICS_V3:MINOR,COMM)";
        final IAlarm parsed = EpicsAlarm.parseFrom(rep);
        Assert.assertNotNull(parsed);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFromStringFailed2() {
        final String rep = "ALARM(XXX:MINOR,COMM)";
        final IAlarm parsed = EpicsAlarm.parseFrom(rep);
        Assert.assertNotNull(parsed);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFromStringFailed3() {
        final String rep = "ALARM(EPICS_V3:MINOR,FOO)";
        final IAlarm parsed = EpicsAlarm.parseFrom(rep);
        Assert.assertNotNull(parsed);
    }
}
