/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.simplepv.ExceptionHandler;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.simplepv.SimplePVLayer;
import org.epics.vtype.VType;

/**
 * An utility class that provides pv buffering read test functionalities.
 * It should be used in a plugin test.
 * 
 * @author Xihui Chen
 * 
 */
public class BufferingReadTester {

	private IPV pv;
	private AtomicInteger updates;
	private volatile boolean connected;
	private String failMessage;
	private String pvName;
	private IPVListener.Stub pvListener;

	/**Create a tester.
	 * @param pvFactoryId pv factory id.
	 * @param pvName pv name. The pv should be a read only pv that returns VType value that 
	 * updates faster than 10hz. For example, sim://ramp(0,100,1,0.1)
	 * @throws Exception 
	 */
	public BufferingReadTester(String pvFactoryId, String pvName) throws Exception {
		updates = new AtomicInteger(0);
		connected = false;
		failMessage = null;
		this.pvName = pvName;
		ExceptionHandler exceptionHandler = new ExceptionHandler() {
			@Override
			public void handleException(Exception exception) {
				System.err.println("Caught Exception in ExceptionHandler: " + exception);
			}
		};
		pv = SimplePVLayer.getPVFactory(pvFactoryId).createPV(pvName, false, 1000, true,
				Executors.newSingleThreadExecutor(), exceptionHandler);
		pvListener = new IPVListener.Stub() {
			@Override
			public void valueChanged(IPV pv) {
				try {
					List<VType> allValues = pv.getAllBufferedValues();
					if (allValues != null && allValues.size() >8) {
						updates.incrementAndGet();
					}					
					if(allValues == null)
						System.err.println("Got null values. getValue()= " + pv.getValue());
					else
						System.out.println("value " + updates.get() + ": " +
								allValues.size() + " values: " + allValues.get(0));
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void exceptionOccurred(IPV pv, Exception exception) {
				System.err.println("Exception Occurred: " + exception);
			}

			@Override
			public void connectionChanged(IPV pv) {
				connected = pv.isConnected();
				System.out.println("Connection Changed: " + connected);
			}

			@Override
			public void writePermissionChanged(IPV pv) {}

			@Override
			public void writeFinished(IPV pv, boolean isWriteSucceeded) {
				failMessage = "writeFinished should not be called";
			}
		};
		pv.addListener(pvListener);
	}


	public void testAll() throws Exception {
		// Test it in this order
		testStart();
		testRead();
		testStop();
		assertNull(failMessage, failMessage);
	}

	protected void testStart() throws Exception {
		pv.start();
		assertTrue(pv.isStarted());
		int i = 0;
		while (!pv.isConnected() && i < 2000) {
			Thread.sleep(10);
			i++;
		}
		System.out.println("It took " + i * 10 + "ms to connect.");		
		assertTrue(pv.isConnected());
		assertFalse(pv.isWriteAllowed());
		assertTrue(pv.isBufferingValues());
		assertFalse(pv.isPaused());
		assertEquals(pvName, pv.getName());

	}

	protected void testRead() throws Exception {
		Thread.sleep(10000);
		assertTrue(connected);
		assertTrue(updates.get() > 8);
		// Test pausing
		pv.setPaused(true);
		assertTrue(pv.isPaused());
		Thread.sleep(1500);
		int temp = updates.get();
		Thread.sleep(2500);
		assertEquals(temp, updates.get());
		// Test resuming
		pv.setPaused(false);
		assertFalse(pv.isPaused());
		Thread.sleep(3000);
		assertTrue(updates.get() - temp > 1);
		//Test remove and add listener
		pv.removeListener(pvListener);
		Thread.sleep(1000);
		temp=updates.get();
		Thread.sleep(3000);
		assertEquals(temp, updates.get());
		pv.addListener(pvListener);
		Thread.sleep(3000);
		assertTrue(updates.get() - temp > 3);
		
		// Test reading buffered values
		assertTrue(pv.getAllBufferedValues() instanceof List<?>);
		assertTrue(pv.getAllBufferedValues().size()>8);		
		assertTrue(pv.getAllBufferedValues().get(0) instanceof VType);
		
		//Test write
		Exception exception = null;
		try {
			pv.setValue(100);
		} catch (Exception e) {
			exception = e;
		}
		assertTrue(exception == null);
	}

	protected void testStop() throws Exception {
		pv.stop();
		assertFalse(pv.isStarted());
		int i = 0;
		while (pv.isConnected() && i < 20) {
			Thread.sleep(100);
			i++;
		}
		System.out.println("It took " + i * 100 + "ms to disconnect.");
		int temp = updates.get();
		Thread.sleep(3000);
		assertEquals(temp, updates.get());
		assertEquals(false, pv.isConnected());

		// test if it can be restarted again.
		pv.start();
//		i=0;
//		while (!pv.isConnected() && i < 200) {
//			Thread.sleep(100);
//			i++;
//		}
//		System.out.println("It took " + i * 100 + "ms to connect.");	
		Thread.sleep(3000);
		assertTrue(updates.get() > temp);
		pv.stop();
	}

}
