package org.csstudio.diag.epics.pvtree;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

/** JUnit test of FieldParser
 *  @author Kay Kasemir
 */
public class FieldParserTest
{
	@Test
	public void testFieldParser() throws Exception
	{
		final HashMap<String, List<String>> rec_fields =
			FieldParser.parse("ai(INP,FLNK) ; ao (DOL, SIML , FLNK )");
		assertNull(rec_fields.get("quirk"));
		List<String> fields = rec_fields.get("ao");
		assertNotNull(fields);
		assertEquals(3, fields.size());
		assertEquals("FLNK", fields.get(2));
	}
}
