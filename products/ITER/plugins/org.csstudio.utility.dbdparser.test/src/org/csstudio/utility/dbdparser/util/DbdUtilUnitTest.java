/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.util;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.utility.dbdparser.data.Field;
import org.csstudio.utility.dbdparser.data.RecordType;
import org.csstudio.utility.dbdparser.data.Template;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link DbdUtil}. readFile: read a {@link File} and return a
 * {@link String} with comments & C declaration replaced by an empty line.
 * generateTemplate: return a non-null {@link Template} initialized from
 * base.dbd file in the specified directory.
 * Test compatibility with EPICS 3.15.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DbdUtilUnitTest {

	/**
	 * Test that the {@link String} read from testRecord.dbd is equal to the
	 * content of testRecordCleaned.dbd (expected result).
	 */
	@Test
	public void testReadFile() {
		try {
			String testNull = DbdUtil.readFile(null);
			Assert.assertNull(testNull);
			String testDBD = DbdUtil.readFile(UnitTestUtils
					.getTestResource("testRecord.dbd"));
			String testDBDCleaned = UnitTestUtils.readFile(UnitTestUtils
					.getTestResource("testRecordCleaned.dbd"));
			Assert.assertEquals(testDBDCleaned, testDBD);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test that {@link Template} generated from resource/test/base.dbd is not
	 * null and not empty.
	 */
	@Test
	public void testGenerateTemplate() {
		try {
			Template testNull = DbdUtil.generateTemplate(null);
			Assert.assertNull(testNull);
			Template t = DbdUtil.generateTemplate("resources/test/");
			Assert.assertNotNull(t);
			Assert.assertFalse(t.getRecordTypes().isEmpty());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test that the {@link DbdParser} is compatible with EPICS 3.15 Based on
	 * EPICS Base Release 3.15.0.2
	 * (http://aps.anl.gov/epics/base/R3-15/0-docs/RELEASE_NOTES.html)
	 */
	@Test
	public void testEPICSFields() {
		final String basePath = "/opt/codac/epics/base/dbd";
		if (!UnitTestUtils.isEPICS315(basePath))
			return;

		try {
			Template dbdTemplate = DbdUtil.generateTemplate(basePath);
			Assert.assertNotNull(dbdTemplate);

			// New record types
			Map<String, List<Field>> fieldMap = new HashMap<String, List<Field>>();
			for (RecordType recordType : dbdTemplate.getRecordTypes())
				fieldMap.put(recordType.getName(), recordType.getFields());
			Assert.assertTrue(fieldMap.keySet().contains("lso"));
			Assert.assertTrue(fieldMap.keySet().contains("lsi"));
			Assert.assertTrue(fieldMap.keySet().contains("printf"));
			Assert.assertTrue(fieldMap.keySet().contains("histogram"));

			Field biRecordZNAM = null;
			for (Field f : fieldMap.get("bi"))
				if (f.getName().equals("ZNAM"))
					biRecordZNAM = f;
			Assert.assertNotNull(biRecordZNAM);
			Assert.assertTrue(biRecordZNAM.getRules().keySet().contains("prop"));

			Set<String> fieldsNames = new HashSet<String>();
			for (Field f : fieldMap.get("bi"))
				fieldsNames.add(f.getName());
			// New Undefined Severity field UDFS (common field)
			Assert.assertTrue(fieldsNames.contains("UDFS"));

			// Sequence record enhancements
			fieldsNames.clear();
			for (Field f : fieldMap.get("seq"))
				fieldsNames.add(f.getName());
			Assert.assertTrue(fieldsNames.contains("OFFS"));
			Assert.assertTrue(fieldsNames.contains("SHFT"));
			Assert.assertTrue(fieldsNames.contains("DOF"));
			Assert.assertTrue(fieldsNames.contains("DOLF"));
			Assert.assertTrue(fieldsNames.contains("DLYF"));

			// Sequence record enhancements
			fieldsNames.clear();
			for (Field f : fieldMap.get("fanout"))
				fieldsNames.add(f.getName());
			Assert.assertTrue(fieldsNames.contains("OFFS"));
			Assert.assertTrue(fieldsNames.contains("SHFT"));
			Assert.assertTrue(fieldsNames.contains("LNK0"));

			// Alarm filtering added to input record types
			fieldsNames.clear();
			for (Field f : fieldMap.get("ai"))
				fieldsNames.add(f.getName());
			Assert.assertTrue(fieldsNames.contains("AFTC"));
			Assert.assertTrue(fieldsNames.contains("AFVL"));
			Assert.assertTrue(fieldsNames.contains("UDFS"));

			fieldsNames.clear();
			for (Field f : fieldMap.get("calc"))
				fieldsNames.add(f.getName());
			Assert.assertTrue(fieldsNames.contains("AFTC"));
			Assert.assertTrue(fieldsNames.contains("AFVL"));
			Assert.assertTrue(fieldsNames.contains("UDFS"));

			fieldsNames.clear();
			for (Field f : fieldMap.get("longin"))
				fieldsNames.add(f.getName());
			Assert.assertTrue(fieldsNames.contains("AFTC"));
			Assert.assertTrue(fieldsNames.contains("AFVL"));
			Assert.assertTrue(fieldsNames.contains("UDFS"));

			fieldsNames.clear();
			for (Field f : fieldMap.get("mbbi"))
				fieldsNames.add(f.getName());
			Assert.assertTrue(fieldsNames.contains("AFTC"));
			Assert.assertTrue(fieldsNames.contains("AFVL"));
			Assert.assertTrue(fieldsNames.contains("UDFS"));

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

}
