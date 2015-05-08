/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.VTypeHelper;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

/** Helper for creating test data
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TestHelper
{
    final public static Display display = ValueFactory.newDisplay(0.0, 1.0, 2.0, "Eggs", NumberFormats.format(2), 8.0, 9.0, 10.0, 0.0, 10.0);

    /** @param value Value
     *  @return VType for that value
     */
    public static VType newValue(final double value)
    {
        return new ArchiveVNumber(Timestamp.now(), AlarmSeverity.NONE, "OK", display, value);
    }

    /** @param samples {@link SampleBuffer} to dump
     *  @return Sample count
     */
    public static int dump(final SampleBuffer samples)
    {
        final int count = samples.getQueueSize();
        VType sample;
        while ((sample = samples.remove()) != null)
            System.out.println(VTypeHelper.toString(sample));
        return count;
    }
}
