package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class EdmModelTest extends TestCase {

	private String displayFile2 = "src/test/resources/LLRF_AUTO.edl";

	public void testEdmModel() throws EdmException {

		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		EdmModel.getInstance();
		EdmModel.getDisplay(displayFile2);

	}
}
