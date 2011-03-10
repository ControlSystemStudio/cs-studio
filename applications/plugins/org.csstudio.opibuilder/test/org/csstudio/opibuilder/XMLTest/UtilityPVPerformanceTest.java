/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.XMLTest;

import junit.framework.TestCase;

import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.junit.Test;

public class UtilityPVPerformanceTest extends TestCase {

	
	
	
	@Test
	public void testPVStartStop() throws Exception{
			final PV pv = PVFactory.createPV("sim://ramp");	
			int i = 100000;
			while(i >0){
				i--;
				PVListener pvListener = new PVListener(){
					public void pvDisconnected(PV pv) {
						// TODO Auto-generated method stub
						
					}
					public void pvValueUpdate(PV pv) {
						System.out.println(pv.getValue());
					}
				};
				pv.addListener(pvListener);
				pv.start();		
				
				Thread.sleep(10);
				pv.removeListener(pvListener);
				pv.stop();
				
			}
			
	}

	
}
