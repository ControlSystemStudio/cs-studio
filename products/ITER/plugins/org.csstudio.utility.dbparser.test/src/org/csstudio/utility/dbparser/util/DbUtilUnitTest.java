/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbparser.util;

import java.util.List;

import org.csstudio.utility.dbparser.data.Record;
import org.eclipse.core.resources.IFile;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link DbUtil}. readFile: read a {@link IFile} and return a
 * {@link String} with comments replaced by an empty line. parseDb: return a
 * non-empty list of {@link Record} initialized from specified DB file.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DbUtilUnitTest {

	/**
	 * Test that the {@link String} read from n6368_ai.db is equal to the
	 * content of n6368_ai_cleaned.db (expected result).
	 */
	@Test
	public void testReadFile() {
		try {
			String testNull = DbUtil.readFile(null);
			Assert.assertNull(testNull);
			String testDB = DbUtil.readFile(UnitTestUtils
					.getTestIResource("testRecord.db"));
			String testDBCleaned = UnitTestUtils.readFile(UnitTestUtils
					.getTestResource("testRecord_cleaned.db"));
			Assert.assertEquals(testDBCleaned, testDB);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test that generated lists are not null.
	 */
	@Test
	public void testParseDb() {
		try {
			List<Record> testNull = DbUtil.parseDb(null);
			Assert.assertNotNull(testNull);
			Assert.assertTrue(testNull.isEmpty());
			List<Record> recordList = DbUtil.parseDb(DbUtil
					.readFile(UnitTestUtils.getTestIResource("testRecord.db")));
			Assert.assertNotNull(recordList);
			Assert.assertFalse(recordList.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
