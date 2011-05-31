/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.internal.test;

import static org.junit.Assert.assertEquals;

import org.csstudio.archive.rdb.internal.TimestampUtil;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.junit.Test;

/** Tests for TimestampUtil
 *  @author Kay Kasemir
 */
public class TimestampUtilTest
{
    @SuppressWarnings("nls")
    @Test
    public void testTimestampUtil()
    {
        ITimestamp time = TimestampFactory.createTimestamp(15, 0);
        assertEquals(20, TimestampUtil.roundUp(time,  5).seconds());
        assertEquals(20, TimestampUtil.roundUp(time, 10).seconds());
        assertEquals(20, TimestampUtil.roundUp(time, 20).seconds());
        assertEquals(30, TimestampUtil.roundUp(time, 30).seconds());

        time = TimestampFactory.createTimestamp(4, 0);
        assertEquals( 5, TimestampUtil.roundUp(time,  5).seconds());
        assertEquals(10, TimestampUtil.roundUp(time, 10).seconds());
        assertEquals(20, TimestampUtil.roundUp(time, 20).seconds());
        assertEquals(30, TimestampUtil.roundUp(time, 30).seconds());

        time = TimestampFactory.now();
        System.out.println("Now:          " + TimestampUtil.roundUp(time, 0));
        System.out.println("Next minute:  " + TimestampUtil.roundUp(time, 60));
        System.out.println("Next 10 mins: " + TimestampUtil.roundUp(time, 10*60));
        System.out.println("Next hour:    " + TimestampUtil.roundUp(time, 60*60));
    }
}
