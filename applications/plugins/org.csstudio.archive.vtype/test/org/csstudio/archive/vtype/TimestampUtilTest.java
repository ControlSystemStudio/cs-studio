/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.epics.util.time.TimestampFormat;
import org.junit.Test;

/** JUnit test of {@link TimestampUtil}
 *  @author Kay Kasemir
 */
public class TimestampUtilTest
{
	@Test
	public void testRoundUp() throws Exception
	{
		final TimestampFormat format = new TimestampFormat(TimestampUtil.FORMAT_SECONDS);

		final Timestamp orig = format.parse("2012-01-19 12:23:14");
		String text = format.format(orig);
		System.out.println(text);
		assertThat(text, equalTo("2012-01-19 12:23:14"));
		
		Timestamp time;
		
		time = TimestampUtil.roundUp(orig, 10);
		text = format.format(time);
		System.out.println(text);
		assertThat(text, equalTo("2012-01-19 12:23:20"));

		time = TimestampUtil.roundUp(orig, TimeDuration.ofSeconds(30));
		text = format.format(time);
		System.out.println(text);
		assertThat(text, equalTo("2012-01-19 12:23:30"));

		time = TimestampUtil.roundUp(orig, 60);
		text = format.format(time);
		System.out.println(text);
		assertThat(text, equalTo("2012-01-19 12:24:00"));

		time = TimestampUtil.roundUp(orig, TimeDuration.ofHours(1.0));
		text = format.format(time);
		System.out.println(text);
		assertThat(text, equalTo("2012-01-19 13:00:00"));

		time = TimestampUtil.roundUp(orig, 2L*60*60);
		text = format.format(time);
		System.out.println(text);
		assertThat(text, equalTo("2012-01-19 14:00:00"));
		
		time = TimestampUtil.roundUp(orig, 24L*60*60);
		text = format.format(time);
		System.out.println(text);
		assertThat(text, equalTo("2012-01-20 00:00:00"));
		
		// TODO Longer time spans, see http://epicschanarch.cvs.sourceforge.net/viewvc/epicschanarch/ChannelArchiver/Tools/epicsTimeHelper.cpp?revision=1.8&view=markup
	}
}
