/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.epics.util.text.NumberFormats;
import org.epics.vtype.Display;
import org.epics.vtype.ValueFactory;
import org.junit.Test;

/** JUnit test of {@link MetaDataHelper}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MetaDataHelperTest
{
    @Test
    public void testDisplay() throws Exception
    {
        final Display d1 = ValueFactory.newDisplay(0.0, 1.0, 2.0, "a.u.", NumberFormats.format(3), 8.0, 9.0, 10.0, 0.0, 10.0);
        final Display d1b = ValueFactory.newDisplay(0.0, 1.0, 2.0, "a.u.", NumberFormats.format(3), 8.0, 9.0, 10.0, 0.0, 10.0);
        final Display d2 = ValueFactory.newDisplay(0.0, 1.0, 2.0, "a.u.", NumberFormats.format(4), 8.0, 9.0, 10.0, 0.0, 10.0);
        final Display d3 = ValueFactory.newDisplay(0.0, 1.0, 2.0, "au", NumberFormats.format(3), 8.0, 9.0, 10.0, 0.0, 10.0);
        final Display d4 = ValueFactory.newDisplay(0.0, 0.5, 2.0, "a.u.", NumberFormats.format(3), 8.0, 9.0, 10.0, 0.0, 10.0);
        assertTrue(MetaDataHelper.equals(d1, d1));
        assertTrue(MetaDataHelper.equals(d1, d1b));
        assertFalse(MetaDataHelper.equals(d1, d2));
        assertFalse(MetaDataHelper.equals(d1, d3));
        assertFalse(MetaDataHelper.equals(d1, d4));

        final Display n1 = ValueFactory.newDisplay(0.0, null, null, "a.u.", null, null, null, null, 0.0, 10.0);
        final Display n1b = ValueFactory.newDisplay(0.0, null, null, "a.u.", null, null, null, null, 0.0, 10.0);
        assertTrue(MetaDataHelper.equals(n1, n1b));
    }

    @Test
    public void testLabels() throws Exception
    {
        final List<String> l1 = Arrays.asList("one", "two");
        final List<String> l1b = Arrays.asList("one", "two");
        assertTrue(MetaDataHelper.equals(l1, l1));
        assertTrue(MetaDataHelper.equals(l1, l1b));

        final List<String> l2 = Arrays.asList("one", "twos");
        assertFalse(MetaDataHelper.equals(l1, l2));
    }
}
