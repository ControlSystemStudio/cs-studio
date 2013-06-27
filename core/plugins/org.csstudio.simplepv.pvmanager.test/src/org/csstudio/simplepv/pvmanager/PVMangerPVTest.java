/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv.pvmanager;

import org.csstudio.simplepv.test.SimplePVBasicReadTester;
import org.junit.Test;

/**
 * Test simple PV Read functionalities.
 * 
 * @author Xihui Chen
 * 
 */
public class PVMangerPVTest {

	@Test
	public void test() throws Exception {
		SimplePVBasicReadTester tester = 
				new SimplePVBasicReadTester("pvmanager", "sim://ramp(0,100,1,0.1)");
		tester.testAll();
		
	}

}
