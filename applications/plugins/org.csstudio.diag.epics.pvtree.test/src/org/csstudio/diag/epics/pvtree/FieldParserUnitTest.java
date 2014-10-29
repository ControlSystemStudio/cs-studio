/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Test;

/** JUnit test of FieldParser
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FieldParserUnitTest
{
    @Test
	public void testFieldParser() throws Exception
	{
		final Map<String, List<String>> rec_fields =
			FieldParser.parse("ai(INP,FLNK) ; ao (DOL, SIML , FLNK, SCAN )  ; calc(X, INPA-L)");
		
		assertThat(rec_fields.get("quirk"), is(nullValue()));
		
		List<String> fields = rec_fields.get("ao");
		assertThat(fields.size(), equalTo(4));
		assertThat(fields.get(0), equalTo("DOL"));
		assertThat(fields.get(1), equalTo("SIML"));
		assertThat(fields.get(2), equalTo("FLNK"));
		assertThat(fields.get(3), equalTo("SCAN"));

		fields = rec_fields.get("calc");
        assertThat(fields.size(), equalTo(13));
        assertThat(fields.get(0), equalTo("X"));
        assertThat(fields.get(1), equalTo("INPA"));
        assertThat(fields.get(12), equalTo("INPL"));
	}
}
