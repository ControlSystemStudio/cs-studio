/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.csstudio.opibuilder.converter.model");
		//$JUnit-BEGIN$
		suite.addTestSuite(EdmModelTest.class);
		suite.addTestSuite(EdmEntityTest.class);
		suite.addTestSuite(EdmIntTest.class);
		suite.addTestSuite(Edm_TextupdateClassTest.class);
		suite.addTestSuite(EdmFontTest.class);
		suite.addTestSuite(EdmComparator.class);
		suite.addTestSuite(EdmColorTest.class);
		suite.addTestSuite(EdmDoubleTest.class);
		suite.addTestSuite(EdmDisplayTest.class);
		suite.addTestSuite(EdmWidgetTest.class);
		suite.addTestSuite(Edm_activeXTextClassTest.class);
		suite.addTestSuite(Edm_activeGroupClassTest.class);
		suite.addTestSuite(Edm_activeRectangleClassTest.class);
		suite.addTestSuite(EdmStringTest.class);
		suite.addTestSuite(EdmColorsListTest.class);
		suite.addTestSuite(EdmBooleanTest.class);
		suite.addTestSuite(EdmAttributeTest.class);
		suite.addTestSuite(EdmLineStyleTest.class);
		//$JUnit-END$
		return suite;
	}

}
