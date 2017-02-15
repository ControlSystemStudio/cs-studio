/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.csstudio.diag.epics.pvtree.model.PVNameFilter;
import org.junit.Test;

/** JUnit test of FieldParser
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVNameFilterUnitTest
{
    @Test
    public void testPVNames() throws Exception
    {
        assertThat(PVNameFilter.isPvName("10"), equalTo(false));
        assertThat(PVNameFilter.isPvName("-3.14"), equalTo(false));
        assertThat(PVNameFilter.isPvName("@vme whatever"), equalTo(false));
        assertThat(PVNameFilter.isPvName("#C2 S2 @whatever"), equalTo(false));
        assertThat(PVNameFilter.isPvName("SomePV"), equalTo(true));
        assertThat(PVNameFilter.isPvName("SomePV47 NMS PP"), equalTo(true));
    }
}
