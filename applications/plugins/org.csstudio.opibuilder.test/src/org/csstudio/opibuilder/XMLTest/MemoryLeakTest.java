/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.XMLTest;

import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MemoryLeakTest {
	
	
	public static void main(String[] args) throws Exception {
		final PV pv = PVFactory.createPV("sim://ramp");
		
		
		
		
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Hello Shell");
		shell.open();
		//DisplayModel displayModel= new DisplayModel();
		
		
		final Runnable tester = new Runnable() {
		
			public void run() {
				try {
					
					pv.start();
					pv.addListener(new PVListener(){
						public void pvDisconnected(PV pv) {
							// TODO Auto-generated method stub
							
						}
						public void pvValueUpdate(PV pv) {
							System.out.println(pv.getValue());
						}
					});
					Thread.sleep(10);
					pv.stop();
					
					//final InputStream inputStream = new FileInputStream("start.opi");
				//	XMLUtil.fillDisplayModelFromInputStream(inputStream, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Display.getCurrent().timerExec(1, this);
			}
		};
		
		Display.getCurrent().timerExec(100, tester);
		
		
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		
		
	}
}
