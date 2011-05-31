/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

/** JUnit test of FieldParser
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FieldParserTest
{
    @Test
	public void testFieldParser() throws Exception
	{
		final HashMap<String, List<String>> rec_fields =
			FieldParser.parse("ai(INP,FLNK) ; ao (DOL, SIML , FLNK, SCAN )");
		assertNull(rec_fields.get("quirk"));
		final List<String> fields = rec_fields.get("ao");
		assertNotNull(fields);
		assertEquals(4, fields.size());
        assertEquals("DOL", fields.get(0));
        assertEquals("SIML", fields.get(1));
		assertEquals("FLNK", fields.get(2));
        assertEquals("SCAN", fields.get(3));
	}
}
