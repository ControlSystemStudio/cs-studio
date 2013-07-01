/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv.pvmanager;

import org.csstudio.simplepv.test.BasicReadTester;
import org.csstudio.simplepv.test.BasicReadWriteTester;
import org.csstudio.simplepv.test.BufferingReadTester;
import org.junit.Test;

/**
 * Test simple PV Read functionalities.
 * 
 * @author Xihui Chen
 * 
 */
public class PVMangerPVTest {

	@Test
	public void testSimpleRead() throws Exception {
		BasicReadTester tester = 
				new BasicReadTester("pvmanager", "sim://ramp(0,100,1,0.1)");
		tester.testAll();
		
	}
	
	@Test
	public void testBufferingRead() throws Exception {
		BufferingReadTester tester = 
				new BufferingReadTester("pvmanager", "sim://ramp(0,80,1,0.1)");
		tester.testAll();		
	}

	
	@Test
	public void testReadWrite() throws Exception {
		BasicReadWriteTester tester = 
				new BasicReadWriteTester("pvmanager", "loc://test(0)");
		tester.testAll();		
	}
}
