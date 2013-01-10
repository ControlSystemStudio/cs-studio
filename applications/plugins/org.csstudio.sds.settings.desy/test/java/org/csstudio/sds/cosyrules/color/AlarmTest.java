package org.csstudio.sds.cosyrules.color;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;

import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.Timestamp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: $
 * @since 17.09.2010
 */
public class AlarmTest {

	private Alarm _alarm;


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		_alarm = new Alarm();
	}

	@Test
	public void nullArgument(){
		final Object out = _alarm.evaluate(null);
		assertEquals(Alarm.UNKNOW, out);
	}

	@Test
	public void longArument() {

		final Long[] minusOne = new Long[]{-1l};
		final Long[] zero = new Long[]{0l};
		final Long[] one = new Long[]{1l};
		final Long[] two = new Long[]{2l};
		final Long[] three = new Long[]{3l};

		Object out = _alarm.evaluate(minusOne);
		assertEquals(Alarm.UNKNOW, out);

		out = _alarm.evaluate(zero);
		assertEquals(Alarm.NORMAL, out);

		out = _alarm.evaluate(one);
		assertEquals(Alarm.WARNING, out);

		out = _alarm.evaluate(two);
		assertEquals(Alarm.ALARM, out);

		out = _alarm.evaluate(three);
		assertEquals(Alarm.ERROR, out);

	}

	@Test
	public void doubleArument() {

		final Double[] minusOne = new Double[]{-1d};
		final Double[] zero = new Double[]{0.000000000000001d};
		final Double[] one = new Double[]{ 0.999999999999999d};
		final Double[] two = new Double[]{ 2.000000000000001d};
		final Double[] three = new Double[]{3d};

		Object out = _alarm.evaluate(minusOne);
		assertEquals(Alarm.UNKNOW, out);

		out = _alarm.evaluate(zero);
		assertEquals(Alarm.NORMAL, out);

		out = _alarm.evaluate(one);
		assertEquals(Alarm.WARNING, out);

		out = _alarm.evaluate(two);
		assertEquals(Alarm.ALARM, out);

		out = _alarm.evaluate(three);
		assertEquals(Alarm.ERROR, out);

	}

	@Test
	public void StringArument() {
		final String[] dirty = new String[]{"test123dirtyIncomming"};
		final String[] noAlarm = new String[]{"NO_ALARM"};
		final String[] minor = new String[]{"MINOR"};
		final String[] warning = new String[]{"WARNING"};
		final String[] major = new String[]{"MAJOR"};
		final String[] alarm = new String[]{"ALARM"};

		Object out = _alarm.evaluate(dirty);
		assertEquals(Alarm.UNKNOW, out);

		out = _alarm.evaluate(noAlarm);
		assertEquals(Alarm.NORMAL, out);

		out = _alarm.evaluate(minor);
		assertEquals(Alarm.WARNING, out);

		out = _alarm.evaluate(warning);
		assertEquals(Alarm.WARNING, out);

		out = _alarm.evaluate(major);
		assertEquals(Alarm.ALARM, out);

		out = _alarm.evaluate(alarm);
		assertEquals(Alarm.ALARM, out);
	}

	@Test
	public void DynamicValueStateArument() {
		final Timestamp timestamp = new Timestamp();
		final DynamicValueCondition[] noValue = new DynamicValueCondition[]{new DynamicValueCondition(EnumSet.of(DynamicValueState.NO_VALUE), timestamp, "test")};
		final DynamicValueCondition[] normal = new DynamicValueCondition[]{new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL), timestamp, "test")};
		final DynamicValueCondition[] warning = new DynamicValueCondition[]{new DynamicValueCondition(EnumSet.of(DynamicValueState.WARNING), timestamp, "test")};
		final DynamicValueCondition[] alarm = new DynamicValueCondition[]{new DynamicValueCondition(EnumSet.of(DynamicValueState.ALARM), timestamp, "test")};
		final DynamicValueCondition[] error = new DynamicValueCondition[]{new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR), timestamp, "test")};
		final DynamicValueCondition[] linkNotAvailable = new DynamicValueCondition[]{new DynamicValueCondition(EnumSet.of(DynamicValueState.LINK_NOT_AVAILABLE), timestamp, "test")};
		final DynamicValueCondition[] timelag = new DynamicValueCondition[]{new DynamicValueCondition(EnumSet.of(DynamicValueState.TIMELAG), timestamp, "test")};
		final DynamicValueCondition[] timeout = new DynamicValueCondition[]{new DynamicValueCondition(EnumSet.of(DynamicValueState.TIMEOUT), timestamp, "test")};

		Object out;

		out = _alarm.evaluate(normal);
		assertEquals(Alarm.NORMAL, out);

		out = _alarm.evaluate(warning);
		assertEquals(Alarm.WARNING, out);

		out = _alarm.evaluate(alarm);
		assertEquals(Alarm.ALARM, out);

		out = _alarm.evaluate(error);
		assertEquals(Alarm.ERROR, out);

		out = _alarm.evaluate(linkNotAvailable);
		assertEquals(Alarm.UNKNOW, out);

		out = _alarm.evaluate(timelag);
		assertEquals(Alarm.UNKNOW, out);

		out = _alarm.evaluate(timeout);
		assertEquals(Alarm.UNKNOW, out);

		out = _alarm.evaluate(noValue);
		assertEquals(Alarm.UNKNOW, out);
	}

	@Test
	public void miscAruments(){
		final Timestamp timestamp = new Timestamp();
		final DynamicValueCondition[] noValue = new DynamicValueCondition[]{new DynamicValueCondition(EnumSet.of(DynamicValueState.NO_VALUE), timestamp, "test")};
		final DynamicValueCondition[] normal = new DynamicValueCondition[]{new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL), timestamp, "test")};
		final DynamicValueCondition[] warning = new DynamicValueCondition[]{new DynamicValueCondition(EnumSet.of(DynamicValueState.WARNING), timestamp, "test")};
		final DynamicValueCondition[] alarm = new DynamicValueCondition[]{new DynamicValueCondition(EnumSet.of(DynamicValueState.ALARM), timestamp, "test")};

		Object[] misc;

		misc = new Object[]{new Long(0l),new Double(0.0d),"NO_ALARM", normal};
		Object out = _alarm.evaluate(misc);
		assertEquals(Alarm.NORMAL, out);

		misc = new Object[]{new Long(1l),new Double(1.0d),"MINOR", warning};
		out = _alarm.evaluate(misc);
		assertEquals(Alarm.WARNING, out);

		misc = new Object[]{new Long(2l),new Double(2.0d),"MAJOR", alarm};
		out = _alarm.evaluate(misc);
		assertEquals(Alarm.ALARM, out);

		misc = new Object[]{new Long(3l),new Double(3.0d),"ErrOr", noValue};
		out = _alarm.evaluate(misc);
		assertEquals(Alarm.ERROR, out);

		misc = new Object[]{new Long(1l),new Double(0.0d),"NO_ALARM", normal};
		out = _alarm.evaluate(misc);
		assertEquals(Alarm.WARNING, out);

		misc = new Object[]{new Long(0l),new Double(2.0d),"NO_ALARM", normal};
		out = _alarm.evaluate(misc);
		assertEquals(Alarm.ALARM, out);

		misc = new Object[]{new Long(0l),new Double(1.0d),"ErrOr", normal};
		out = _alarm.evaluate(misc);
		assertEquals(Alarm.ERROR, out);
	}

	@Test
    public void alarmOrderingTest() throws Exception {
	    Object[] misc;
	    // MINOR
	    misc = new Object[]{new Long(0l),new Double(0.0d),"NO_ALARM", new DynamicValueCondition(DynamicValueState.WARNING)};
	    Object out = _alarm.evaluate(misc);
	    assertEquals(Alarm.WARNING, out);

	    misc = new Object[]{new Long(0l),new Double(0.0d),"MINOR", new DynamicValueCondition(DynamicValueState.NORMAL)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.WARNING, out);

	    misc = new Object[]{new Long(0l),new Double(1.0d),"NO_ALARM", new DynamicValueCondition(DynamicValueState.NORMAL)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.WARNING, out);

	    misc = new Object[]{new Long(1l),new Double(0.0d),"NO_ALARM", new DynamicValueCondition(DynamicValueState.NORMAL)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.WARNING, out);
	    //MAJOR
	    misc = new Object[]{new Long(0l),new Double(1.0d),"NO_ALARM", new DynamicValueCondition(DynamicValueState.ALARM)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.ALARM, out);

	    misc = new Object[]{new Long(1l),new Double(0.0d),"MAJOR", new DynamicValueCondition(DynamicValueState.NORMAL)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.ALARM, out);

	    misc = new Object[]{new Long(0l),new Double(2.0d),"MINOR", new DynamicValueCondition(DynamicValueState.NORMAL)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.ALARM, out);

	    misc = new Object[]{new Long(2l),new Double(0.0d),"NO_ALARM", new DynamicValueCondition(DynamicValueState.WARNING)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.ALARM, out);
	    //INVALID
	    misc = new Object[]{new Long(0l),new Double(1.0d),"NO_ALARM", new DynamicValueCondition(DynamicValueState.ERROR)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.ERROR, out);

	    misc = new Object[]{new Long(1l),new Double(0.0d),"INVALID", new DynamicValueCondition(DynamicValueState.NORMAL)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.ERROR, out);

	    misc = new Object[]{new Long(0l),new Double(3.0d),"MINOR", new DynamicValueCondition(DynamicValueState.NORMAL)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.ERROR, out);

	    misc = new Object[]{new Long(3l),new Double(0.0d),"NO_ALARM", new DynamicValueCondition(DynamicValueState.WARNING)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.ERROR, out);
	    //UNKWON
	    misc = new Object[]{new Long(0l),new Double(1.0d),"NO_ALARM", new DynamicValueCondition(DynamicValueState.HAS_METADATA)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.UNKNOW, out);

	    misc = new Object[]{new Long(1l),new Double(0.0d),"BLABLUP", new DynamicValueCondition(DynamicValueState.NORMAL)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.UNKNOW, out);

	    misc = new Object[]{new Long(0l),new Double(-08.15d),"MINOR", new DynamicValueCondition(DynamicValueState.NORMAL)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.UNKNOW, out);

	    misc = new Object[]{new Long(4711l),new Double(0.0d),"NO_ALARM", new DynamicValueCondition(DynamicValueState.WARNING)};
	    out = _alarm.evaluate(misc);
	    assertEquals(Alarm.UNKNOW, out);

    }



	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

}
