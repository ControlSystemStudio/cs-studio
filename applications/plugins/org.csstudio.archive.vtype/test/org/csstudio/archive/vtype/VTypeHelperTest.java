/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import static org.csstudio.utility.test.HamcrestMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.junit.Test;

/** JUnit test of {@link VType}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class VTypeHelperTest
{
    @Test
    public void testFormat() throws Exception
    {
        final Date now = new Date();
        final Display display = ValueFactory.newDisplay(0.0, 1.0, 2.0, "a.u.", NumberFormats.format(3), 8.0, 9.0, 10.0, 0.0, 10.0);
        final VType num = new ArchiveVNumber(Timestamp.of(now), AlarmSeverity.MINOR, "Troubling", display, 3.14);
        
        String text = VTypeHelper.toString(num);
        System.out.println(text);
        assertThat(now, equalTo(VTypeHelper.getTimestamp(num).toDate()));
        assertThat(text, containsString("3.14"));
        assertThat(text, containsString("a.u."));
        assertThat(text, containsString("MINOR"));
        assertThat(text, containsString("Troubling"));

        final List<String> labels = Arrays.asList("zero", "one", "two", "three");
        final VType enumerated = new ArchiveVEnum(Timestamp.of(now), AlarmSeverity.MINOR, "Troubling", labels, 3);
        text = VTypeHelper.toString(enumerated);
        System.out.println(text);
        assertThat(text, containsString("three"));
        assertThat(text, containsString("(3)"));
    }
}
