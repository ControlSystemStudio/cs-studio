package org.csstudio.opibuilder.pvmanager;

import static org.junit.Assert.assertEquals;

import org.csstudio.platform.data.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class PVManagerLocalPVTest {

	private static int updates = 0;

	static String pvName;
	private static PVManagerPV pv, pv2;

	private static PVListener listener;

	@BeforeClass
	public static void setUp() throws Exception {
		updates = 0;
		pvName =
		// "css:sine";
		// "css:count";
		// "css:setpoint";
		// "Ring_Diag:VFM:image";
		//"Ring_IDmp:Foil_Plunge:Psn";
		// "css:sensor";
		"sim://noise";
		//"loc://test";
		// "CG1D:Cam:Cam1:AcquireTime";

		listener = new PVListener() {

			@Override
			public void pvValueUpdate(PV pv) {
				System.out.println("pvValueUpdate: " + pv.getValue().format());
				updates++;
			}

			@Override
			public void pvDisconnected(PV pv) {
				System.out.println("Disconnected: " + pv.getStateInfo());
			}
		};

		pv = new PVManagerPV(pvName, false, 100);
		pv.start();
		
		while (!pv.isConnected()) {
			System.out.println("Connecting 1...");
			Thread.sleep(1000);
		}
		
		pv2 = new PVManagerPV(pvName, false, 100);
		pv2.start();
		
		while (!pv2.isConnected()) {
			System.out.println("Connecting 2...");
			Thread.sleep(1000);
		}

	}

	@AfterClass
	public static void tearDown() throws Exception {
		Thread.sleep(30000);
		pv.stop();
		pv2.stop();
	}

	@Test
	public void testAddListener() throws InterruptedException {
		pv.addListener(listener);
		pv2.addListener(listener);
	}

	@Ignore
	@Test
	public void testWrite() throws Exception {
		int oldUpdates = updates;
		pv.setValue("10");
		Thread.sleep(100);
		assertEquals("10", ValueUtil.getString(pv.getValue()));
		assertEquals(oldUpdates+1, updates);
		
		oldUpdates = updates;
		pv.setValue(20);
		Thread.sleep(100);
		assertEquals(20, ValueUtil.getDouble(pv.getValue()), 0);
		assertEquals(oldUpdates+1, updates);
		
		pv.removeListener(listener);
		oldUpdates = updates;
		pv.setValue(30.232);
		Thread.sleep(100);
		assertEquals(30.232, ValueUtil.getDouble(pv.getValue()), 0);
		assertEquals(oldUpdates, updates);

	}

	

}
