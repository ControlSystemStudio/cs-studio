/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.util;

import java.io.File;

import org.csstudio.utility.dbdparser.data.Template;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link DbdUtil}. readFile: read a {@link File} and return a
 * {@link String} with comments & C declaration replaced by an empty line.
 * generateTemplate: return a non-null {@link Template} initialized from
 * base.dbd file in the specified directory.
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

}
