/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link DbdReader}: recursively replaces all dbd file inclusion by the
 * corresponding file content.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DbdReaderUnitTest {

	/**
	 * Test that the {@link String} generated from resources/test/base.dbd is
	 * equal to the content of baseWithIncludes.dbd (expected result).
	 */
	@Test
	public void testGetCompleteDbdFile() {
		try {
			new DbdReader(null);
			Assert.fail();
		} catch (Exception e) {
		}
		try {
			DbdReader reader = new DbdReader("resources/test");
			String base = reader.getCompleteDbdFile();
			String baseWithIncludes = UnitTestUtils.readFile(UnitTestUtils
					.getTestResource("baseWithIncludes.dbd"));
			Assert.assertEquals(baseWithIncludes, base);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

}
