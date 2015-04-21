/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.csstudio.opibuilder.converter.writer");
		//$JUnit-BEGIN$
		suite.addTestSuite(Opi_activeXTextClassTest.class);
		suite.addTestSuite(OpiWidgetTest.class);
		suite.addTestSuite(OpiColorTest.class);
		suite.addTestSuite(OpiFontTest.class);
		suite.addTestSuite(OpiDoubleTest.class);
		suite.addTestSuite(OpiIntTest.class);
		suite.addTestSuite(OpiColorDefTest.class);
		suite.addTestSuite(OpiStringTest.class);
		suite.addTestSuite(OpiBooleanTest.class);
		suite.addTestSuite(Opi_TextupdateClassTest.class);
		suite.addTestSuite(OpiDisplayTest.class);
		suite.addTestSuite(OpiAttributeTest.class);
		suite.addTestSuite(Opi_activeRectangleClassTest.class);
		suite.addTestSuite(Opi_activeGroupClassTest.class);
		suite.addTestSuite(OpiWriterTest.class);
		//$JUnit-END$
		return suite;
	}

}
