/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.config.savevalue.internal.dbfile;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;


/**
 * @author Joerg Rathlev
 */
public class RecordInstanceDatabaseParserTest {
	
	@Test
	public void testParseEmptyString() throws Exception {
		// Expected: parsing an empty string returns an empty list of record
		// instances.
		Reader reader = new StringReader("");
		RecordInstanceDatabaseParser parser = new RecordInstanceDatabaseParser(reader);
		List<RecordInstance> result = parser.parse();
		assertEquals(0, result.size());
	}
	
	@Test
	public void testParseInstanceDefinitionWithoutFields() throws Exception {
		// Expected: parsing an instance definition with no fields returns
		// that instance definition.
		Reader reader = new StringReader("record(ai, TEST){}");
		RecordInstanceDatabaseParser parser = new RecordInstanceDatabaseParser(reader);
		List<RecordInstance> result = parser.parse();
		assertEquals(1, result.size());
		RecordInstance ri = result.get(0);
		assertEquals("ai", ri.getType());
		assertEquals("TEST", ri.getName());
	}

	@Test
	public void testParseInstanceDefinitionWithSingleField() throws Exception {
		// Expected: parsing an instance definition with no fields returns
		// that instance definition.
		Reader reader = new StringReader("record(ai,TEST){field(TEST,\"foo\")}");
		RecordInstanceDatabaseParser parser = new RecordInstanceDatabaseParser(reader);
		List<RecordInstance> result = parser.parse();
		assertEquals(1, result.size());
		RecordInstance ri = result.get(0);
		assertEquals("ai", ri.getType());
		assertEquals("TEST", ri.getName());
		assertEquals(1, ri.getFields().size());
		Field f  = ri.getFields().get(0);
		assertEquals("TEST", f.getName());
		assertEquals("foo", f.getValue());
	}
	
	@Test
	public void testExampleFromEpicsAppDevGuide() throws Exception {
		// This is a shortened example from the EPICS Application Developer's
		// Guide.
		// 
		// record(bi,STS_AbDiA0C0S0) {
		//    field(SCAN,"I/O Intr")
		//    field(DTYP,"AB-Binary Input")
		//    field(INP,"#L0 A0 C0 S0 F0 @")
		// }
		Reader reader = new StringReader("record(bi,STS_AbDiA0C0S0) {\n" +
				"   field(SCAN,\"I/O Intr\")\n" +
				"   field(DTYP,\"AB-Binary Input\")\n" +
				"   field(INP,\"#L0 A0 C0 S0 F0 @\")\n"+
				"}");
		RecordInstanceDatabaseParser parser = new RecordInstanceDatabaseParser(reader);
		List<RecordInstance> result = parser.parse();
		assertEquals(1, result.size());
		RecordInstance ri = result.get(0);
		assertEquals("bi", ri.getType());
		assertEquals("STS_AbDiA0C0S0", ri.getName());
		assertEquals(3, ri.getFields().size());
		Field f  = ri.getFields().get(0);
		assertEquals("SCAN", f.getName());
		assertEquals("I/O Intr", f.getValue());
		f  = ri.getFields().get(1);
		assertEquals("DTYP", f.getName());
		assertEquals("AB-Binary Input", f.getValue());
		f  = ri.getFields().get(2);
		assertEquals("INP", f.getName());
		assertEquals("#L0 A0 C0 S0 F0 @", f.getValue());
	}
}
