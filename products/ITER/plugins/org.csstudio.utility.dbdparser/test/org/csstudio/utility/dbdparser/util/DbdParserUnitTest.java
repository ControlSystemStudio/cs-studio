/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.util;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.Tree;
import org.csstudio.utility.dbdparser.antlr.DbdFileLexer;
import org.csstudio.utility.dbdparser.data.Template;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link DbdParser}: create {@link Template} from ANTLR {@link Tree}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DbdParserUnitTest {

	/**
	 * Test that the {@link String} printed from ANTLR {@link Tree} generated
	 * from testRecord.dbd is equal to the content of testRecordTree.txt
	 * (expected result). Test that the {@link Template} generated from ANTLR
	 * {@link Tree} contains the right number of values. Test that the toString
	 * method of {@link Template} returns a {@link String} equal to the content
	 * of testRecordTemplate.txt (expected result).
	 */
	@Test
	public void testParser() {
		try {
			String dbFile = DbdUtil.readFile(UnitTestUtils
					.getTestResource("testRecord.dbd"));
			CharStream cs = new ANTLRStringStream(dbFile);
			DbdFileLexer lexer = new DbdFileLexer(cs);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			DbdParser parser = new DbdParser();
			parser.parse(tokens);

			String tree = parser.print();
			String testRecordTree = UnitTestUtils.readFile(UnitTestUtils
					.getTestResource("testRecordTree.txt"));
			Assert.assertEquals(testRecordTree, tree);

			parser.transform();
			Template t = parser.getCurrentTemplate();
			Assert.assertEquals(2, t.getPaths().size());
			Assert.assertEquals(1, t.getIncludes().size());
			Assert.assertEquals(1, t.getMenus().size());
			Assert.assertEquals(2, t.getMenus().get(0).getChoices().size());
			Assert.assertEquals(2, t.getRecordTypes().size());
			Assert.assertEquals(7, t.getRecordTypes().get(1).getFields().size());
			Assert.assertEquals(3, t.getDevices().size());
			Assert.assertEquals(3, t.getDrivers().size());
			Assert.assertEquals(2, t.getRegistrars().size());
			Assert.assertEquals(2, t.getVariables().size());
			Assert.assertEquals(2, t.getFunctions().size());
			Assert.assertEquals(1, t.getBreaktables().size());
			Assert.assertEquals(2, t.getBreaktables().get(0).getValues().length);

			String testRecordTemplate = UnitTestUtils.readFile(UnitTestUtils
					.getTestResource("testRecordTemplate.txt"));
			Assert.assertEquals(testRecordTemplate, t.toString() + "\n");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
