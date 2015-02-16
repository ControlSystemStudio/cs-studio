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
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.simplepv.ExceptionHandler;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.simplepv.SimplePVLayer;
import org.csstudio.simplepv.VTypeHelper;
import org.epics.vtype.VDouble;

/**
 * An utility class that provides basic PV test functionalities.
 * It should be used in a plugin test.
 * 
 * @author Xihui Chen
 * 
 */
public class BasicReadWriteTester {

	private IPV pv;
	private AtomicInteger updates;
	private volatile boolean connected, writeAllowed;
	private String pvName;
	private IPVListener.Stub pvListener;

	/**Create a tester.
	 * @param pvFactoryId pv factory id.
	 * @param pvName pv name. The pv should be a writable pv. For example, loc://test(0)
	 * @throws Exception 
	 */
	public BasicReadWriteTester(String pvFactoryId, String pvName) throws Exception {
		updates = new AtomicInteger(0);
		connected = false;
		this.pvName = pvName;
		ExceptionHandler exceptionHandler = new ExceptionHandler() {
			@Override
			public void handleException(Exception exception) {
				System.err.println("Caught Exception in ExceptionHandler: " + exception);
			}
		};
		pv = SimplePVLayer.getPVFactory(pvFactoryId).createPV(pvName, false, 1, false,
				Executors.newSingleThreadExecutor(), exceptionHandler);
		pvListener = new IPVListener.Stub() {
			@Override
			public void valueChanged(IPV pv) {
				try {
					System.out.println("value " + (updates.get() + 1) + ": " + pv.getValue());
					if (pv.getValue() != null && pv.getValue() instanceof VDouble) {
						updates.incrementAndGet();
					}
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
			public void writePermissionChanged(IPV pv) {
				writeAllowed = true;
				System.out.println("Write Permission Changed: " + pv.isWriteAllowed());
			}

			@Override
			public void writeFinished(IPV pv, boolean isWriteSucceeded) {
				System.out.println("Write Finished: " + isWriteSucceeded);
			}
		};
		pv.addListener(pvListener);
	}


	public void testAll() throws Exception {
		// Test it in this order
		testStart();
		testReadWrite();
		testStop();
	}

	protected void testStart() throws Exception {
		pv.start();
		assertTrue(pv.isStarted());
		int i = 0;
		while (!pv.isConnected() && i < 200) {
			Thread.sleep(100);
			i++;
		}
		System.out.println("It took " + i * 100 + "ms to connect.");		
		assertTrue(pv.isConnected());

		assertFalse(pv.isBufferingValues());
		assertFalse(pv.isPaused());
		assertEquals(pvName, pv.getName());

	}

	protected void testReadWrite() throws Exception {
		Thread.sleep(1000);
		assertTrue(pv.isWriteAllowed());
		assertTrue(writeAllowed);
		assertTrue(connected);
		assertEquals(1, updates.get());
		//Test write
		final int d = 123;
		
		pv.setValue(d);
		Thread.sleep(1000);
		assertEquals(2, updates.get());
		assertEquals(d, VTypeHelper.getNumber(pv.getValue()).intValue());
		
		// Test pausing
		pv.setPaused(true);
		assertTrue(pv.isPaused());
		int temp = updates.get();
		pv.setValue(213);
		Thread.sleep(1000);
		assertEquals(temp, updates.get());
		// Test resuming
		pv.setPaused(false);
		assertFalse(pv.isPaused());
		pv.setValue(456);
		Thread.sleep(1000);
		assertTrue(updates.get() > temp);
		//Test remove and add listener
		temp=updates.get();
		pv.removeListener(pvListener);
		pv.setValue(678);
		Thread.sleep(1000);
		assertEquals(temp, updates.get());
		pv.addListener(pvListener);
		pv.setValue(678);
		Thread.sleep(1000);
		assertEquals(updates.get(), temp +2);
		
		//test sync write
		temp=updates.get();
		pv.setValue(890, 5000);
		Thread.sleep(1000);
		assertEquals(updates.get(), temp+1);
		assertEquals(890, VTypeHelper.getDouble(pv.getValue()), 0.1);
		
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
		Thread.sleep(1000);
		assertEquals(temp, updates.get());
		assertEquals(false, pv.isConnected());

		// test if it can be restarted again.
		pv.start();
		Thread.sleep(1000);
		pv.setValue(891);
		Thread.sleep(1000);
		assertEquals(updates.get(),temp+2);
		pv.stop();
	}

}
