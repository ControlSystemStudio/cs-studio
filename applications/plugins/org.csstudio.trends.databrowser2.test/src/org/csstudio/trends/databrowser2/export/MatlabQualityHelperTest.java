/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.export;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.epics.vtype.AlarmSeverity;
import org.junit.Test;

/** JUnit test of the MatlabQualityHelper
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MatlabQualityHelperTest
{
    @Test
    public void testMatlabQualityHelper()
    {
        final MatlabQualityHelper quality_helper = new MatlabQualityHelper();

        // Assume quality codes are assigned first-come, so the first one is 0
        assertThat(quality_helper.getQualityCode(AlarmSeverity.NONE, "OK"), equalTo(0));
        // then 1
        assertThat(quality_helper.getQualityCode(AlarmSeverity.INVALID, "READ"), equalTo(1));

        // Looking for the same severity/status again gives the same code
        assertThat(quality_helper.getQualityCode(AlarmSeverity.INVALID, "READ"), equalTo(1));

        // New code
        assertThat(quality_helper.getQualityCode(AlarmSeverity.INVALID, "WRITE"), equalTo(2));

        // Check codes learned so far
        assertThat(quality_helper.getNumCodes(), equalTo(3));
        assertThat(quality_helper.getQuality(2), equalTo("INVALID/WRITE"));
    }
}
