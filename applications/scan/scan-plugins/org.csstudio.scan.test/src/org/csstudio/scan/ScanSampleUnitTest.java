/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.csstudio.scan.data.NumberScanSample;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFactory;
import org.csstudio.scan.data.ScanSampleFormatter;
import org.junit.Test;

/** JUnit Test of the {@link ScanSample} and related classes
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanSampleUnitTest
{
    @Test
    public void testScanSample() throws Exception
    {
        ScanSample sample = new NumberScanSample(new Date(), 1, new Number[] { 3.14 });
        System.out.println(sample);
        assertThat(sample.toString().endsWith("3.14"), equalTo(true));

        sample = new NumberScanSample(new Date(), 1, new Number[] { 3.14, 42.0 });
        System.out.println(sample);
        assertThat(sample.toString().endsWith("42.0]"), equalTo(true));


        sample = ScanSampleFactory.createSample(new Date(), 0, 1, 2, 3, 4);
        System.out.println(sample);
        assertThat(sample.toString().endsWith("3, 4]"), equalTo(true));

        assertThat(ScanSampleFormatter.asDouble(sample), equalTo(1.0));
    }

    @Test
    public void testTimeFormatting() throws Exception
    {
        // 'now', today
        Date date = new Date();
        String text = ScanSampleFormatter.formatCompactDateTime(date);
        System.out.println(date + " -> " + text);
        assertThat(text.length(), equalTo(8));

        // Different day
        date = new Date(date.getTime() + 25l*60*60*1000);
        text = ScanSampleFormatter.formatCompactDateTime(date);
        System.out.println(date + " -> " + text);
        assertThat("HH:MM separator", text.charAt(8), equalTo(':'));

        // Different year
        date = new Date(date.getTime() + 366l*24*60*60*1000);
        text = ScanSampleFormatter.formatCompactDateTime(date);
        System.out.println(date + " -> " + text);
        assertThat(text.length(), equalTo(10));
    }
}
