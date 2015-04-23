/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.csstudio.opibuilder.converter.parser");
		//$JUnit-BEGIN$
		suite.addTestSuite(EdmParserTest.class);
		suite.addTestSuite(EdmDisplayParserTest.class);
		suite.addTestSuite(EdmFontsListParserTest.class);
		suite.addTestSuite(EdmColorsListParserTest.class);
		//$JUnit-END$
		return suite;
	}

}
