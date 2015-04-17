/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class EdmAttributeTest extends TestCase {

	private EdmAttribute testAttribute;
	private static final String testVal = "TEST";
	private static final String val2 = "VALUE_TWO";
	private static final String val3 = "VALUE_THREE";
	private static final String val4 = "VALUE_FOUR";

	/**
	 * Creates basic instance of EdmAttribute class with sample element.
	 */
	public void setupAttribute() {
		testAttribute = new EdmAttribute(testVal);
	}

	public void testAppendValue() {

		testAttribute = new EdmAttribute();

		assertEquals("check_count", 0, testAttribute.getValueCount());

		testAttribute.appendValue(testVal);

		assertEquals("check_count", 1, testAttribute.getValueCount());
		assertEquals("check_value", testVal,
				testAttribute.getValue(testAttribute.getValueCount() - 1 ));
	}

	public void testAppendAndSetMoreValues() {

		setupAttribute();

		testAttribute.appendValue(val2);
		assertEquals("check_value", val2,
				testAttribute.getValue(testAttribute.getValueCount() - 1 ));

		testAttribute.appendValue(val3);
		assertEquals("check_value", val3,
				testAttribute.getValue(testAttribute.getValueCount() - 1 ));

		testAttribute.appendValue(val4);
		assertEquals("check_value", val4,
				testAttribute.getValue(testAttribute.getValueCount() - 1 ));

		assertEquals("check_count", 4, testAttribute.getValueCount());

		assertEquals("check_value", val3,
				testAttribute.getValue(testAttribute.getValueCount() - 2 ));
		assertEquals("check_value", val2,
				testAttribute.getValue(testAttribute.getValueCount() - 3 ));

	}

	public void testToStringMethod() {
		setupAttribute();

		testAttribute.appendValue(val2);
		testAttribute.appendValue(val3);
		testAttribute.appendValue(val4);
		
		String concatenatedString = testVal + " " + val2 + " " + val3 + " " + val4;

		assertEquals(concatenatedString, testAttribute.toString());
	}

	public void testCopyConstructor() {

		setupAttribute();
		
		try {
			EdmAttribute attribute = new EdmAttribute(testAttribute);
			int valCount = testAttribute.getValueCount();
			assertEquals(valCount, attribute.getValueCount());

			for (int i = 0; i < valCount; i++)
				assertEquals(testAttribute.getValue(i), attribute.getValue(i));
		}
		catch (EdmException e) {
			assertEquals(EdmException.SPECIFIC_PARSING_ERROR, e.getType());
		}
	}
}
