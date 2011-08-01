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
package org.csstudio.domain.desy.epics.types;

import static org.csstudio.domain.desy.epics.types.EpicsEnum.RAW;
import static org.csstudio.domain.desy.epics.types.EpicsEnum.SEP;
import static org.csstudio.domain.desy.epics.types.EpicsEnum.STATE;
import junit.framework.Assert;

import org.junit.Test;

/**
 * Test for {@link EpicsEnum}.
 *
 * @author bknerr
 * @since 12.05.2011
 */
public class EpicsEnumUnitTest {

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidCreationFromString1() {
        EpicsEnum.createFromString("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidCreationFromString2() {
        EpicsEnum.createFromString("RAW:hallo");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidCreationFromString3() {
        EpicsEnum.createFromString("RAW:2.5");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidCreationFromString4() {
        EpicsEnum.createFromString("STATE(hallo):2");
    }

    @Test
    public void testRawCreationFromStringExponentialNotation() {
        EpicsEnum fromString = EpicsEnum.createFromString("RAW:1e6");
        Assert.assertEquals(Integer.valueOf((int) 1e6), fromString.getRaw());

        fromString = EpicsEnum.createFromString("RAW:-1e-6");
        Assert.assertEquals(Integer.valueOf((int) -1e-6), fromString.getRaw());
    }

    @Test
    public void testRawCreationFromStringNegativeInteger() {
        final EpicsEnum fromString = EpicsEnum.createFromString("RAW:-13");
        Assert.assertEquals(Integer.valueOf(-13), fromString.getRaw());
    }

    @Test
    public void testCreationFromString() {
        final EpicsEnum fromStateString = EpicsEnum.createFromString(STATE + "(0)" + SEP + "hallo");
        Assert.assertTrue(fromStateString.isState());
        Assert.assertFalse(fromStateString.isRaw());
        Assert.assertEquals(Integer.valueOf(0), fromStateString.getStateIndex());
        Assert.assertEquals("hallo", fromStateString.getState());
        try {
            fromStateString.getRaw();
        } catch (final IllegalStateException e) {
            // Great.
        }

        final EpicsEnum fromRawString = EpicsEnum.createFromString(RAW + SEP + "26");
        Assert.assertFalse(fromRawString.isState());
        Assert.assertTrue(fromRawString.isRaw());
        Assert.assertEquals(Integer.valueOf(26), fromRawString.getRaw());
        try {
            fromRawString.getState();
        } catch (final IllegalStateException e) {
            // Great.
        }
        try {
            fromRawString.getStateIndex();
        } catch (final IllegalStateException e) {
            // Great.
        }

    }

    @Test
    public void testCreationFromRaw() {
        final EpicsEnum fromRaw = EpicsEnum.createFromRaw(1);
        Assert.assertFalse(fromRaw.isState());
        Assert.assertTrue(fromRaw.isRaw());
        Assert.assertEquals(Integer.valueOf(1), fromRaw.getRaw());
        try {
            fromRaw.getState();
        } catch (final IllegalStateException e) {
            // Great.
        }
        try {
            fromRaw.getStateIndex();
        } catch (final IllegalStateException e) {
            // Great.
        }
        final String string = fromRaw.toString();
        Assert.assertEquals(fromRaw, EpicsEnum.createFromString(string));

    }

    @Test
    public void testCreationFromState() {

        final EpicsEnum fromStateName = EpicsEnum.createFromStateName("huhu");
        Assert.assertFalse(fromStateName.isRaw());
        Assert.assertTrue(fromStateName.isState());
        final String stateStr = fromStateName.toString();
        Assert.assertEquals("STATE(0):huhu", stateStr);
        final EpicsEnum fromString = EpicsEnum.createFromString(stateStr);
        Assert.assertEquals(fromStateName, fromString);
    }

}
