/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.csstudio.scan.util.TextTable;
import org.junit.Test;

/** JUnit test of the {@link TextTable}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TextTableUnitTest
{
    @Test
    public void testTextTable()
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final TextTable table = new TextTable(out);

        table.addColumn("Time");
        table.addColumn("Value");

        table.addCell("2012/01/17");
        table.addCell("41");
        table.addRow("2012/01/19", "42");

        table.flush();

        final String result = out.toString();
        System.out.println(out);
        String eol = System.getProperty("line.separator");

        assertEquals("Time       Value"+eol+"========== ====="+eol+"2012/01/17 41   "+eol+"2012/01/19 42   "+eol+"", result);
    }
}
