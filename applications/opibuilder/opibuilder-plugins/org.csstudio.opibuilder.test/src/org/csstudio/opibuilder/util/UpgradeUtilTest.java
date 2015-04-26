/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** JUnit test of the {@link UpgradeUtil}
 *  @author Xihui Chen
 *  @author Kay Kasemir
 */
public class UpgradeUtilTest {
	
	@Test
	public void testConvertUtilityPVNameToPM() {
		
		//local pv
		assertEquals("loc://myPV(\"fred\")",
				UpgradeUtil.convertUtilityPVNameToPM("loc://myPV(fred)"));
		assertEquals("loc://myPV(\"fred\")",
				UpgradeUtil.convertUtilityPVNameToPM("loc://myPV(\"fred\")"));
		assertEquals("loc://myPV(\"\")",
				UpgradeUtil.convertUtilityPVNameToPM("loc://myPV(\"\")"));
		assertEquals("loc://myPV(12.34)",
				UpgradeUtil.convertUtilityPVNameToPM("loc://myPV(12.34)"));
		assertEquals("loc://myPV(12,34,2.3,4.56)",
				UpgradeUtil.convertUtilityPVNameToPM("loc://myPV(12,34,2.3,4.56)"));
		assertEquals("loc://myPV",
				UpgradeUtil.convertUtilityPVNameToPM("loc://myPV"));

		
		//macro should not be converted
		assertEquals("loc://my$(DID)PV(\"fred\")",
				UpgradeUtil.convertUtilityPVNameToPM("loc://my$(DID)PV(fred)"));		
		assertEquals("loc://my$(DID)PV$(DID)",
				UpgradeUtil.convertUtilityPVNameToPM("loc://my$(DID)PV$(DID)"));
		assertEquals("loc://trace0PV(\"sim://sine(0,100,100,1)\")",
				UpgradeUtil.convertUtilityPVNameToPM("loc://trace0PV(\"sim://sine(0,100,100,1)\")"));
		
		
		//double constants
		assertEquals("=12.345",
				UpgradeUtil.convertUtilityPVNameToPM("12.345"));
		assertEquals("=123456789",
				UpgradeUtil.convertUtilityPVNameToPM("123456789"));
		assertEquals("=1.23e23",
				UpgradeUtil.convertUtilityPVNameToPM("1.23e23"));
		assertEquals("=0.0",
				UpgradeUtil.convertUtilityPVNameToPM("0.0"));
		
		assertEquals("=123",
				UpgradeUtil.convertUtilityPVNameToPM("const://$(DID)_name$(DID)(123)"));
		
		
		//String constants		
		assertEquals("=\"1.23e23\"",
				UpgradeUtil.convertUtilityPVNameToPM("\"1.23e23\""));
		
		
		//array constants
		assertEquals("sim://const(12,23,56, 78.123)",
				UpgradeUtil.convertUtilityPVNameToPM("const://myArray(12,23,56, 78.123)"));
		
		//String constants
		assertEquals("=\"fred\"",
				UpgradeUtil.convertUtilityPVNameToPM("const://mypv(fred)"));
	
		assertEquals("=\"fred\"",
				UpgradeUtil.convertUtilityPVNameToPM("const://mypv(\"fred\")"));
		
		assertEquals("=123.45678",
				UpgradeUtil.convertUtilityPVNameToPM("const://mypv(123.45678)"));
		
		//regular pv
		assertEquals("fred:current",
				UpgradeUtil.convertUtilityPVNameToPM("fred:current"));
		
		// Macros:
		// Unclear how to convert constants, because strings would require quotes,
		// but content of macro is not known.
		// Bug discussion https://github.com/ControlSystemStudio/cs-studio/issues/412
		// decided on unquoted
        assertEquals("=$(M)",
                UpgradeUtil.convertUtilityPVNameToPM("const://x($(M))"));
	}
}
