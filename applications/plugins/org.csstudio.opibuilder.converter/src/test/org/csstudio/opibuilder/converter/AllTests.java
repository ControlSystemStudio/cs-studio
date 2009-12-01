package org.csstudio.opibuilder.converter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.csstudio.opibuilder.converter");
		//$JUnit-BEGIN$
		suite.addTestSuite(EdmConverterTest.class);
		//$JUnit-END$
		return suite;
	}
}
