package org.csstudio.opibuilder.pvmanager;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.csstudio.platform.data.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class PVManagerEPICSPVTest {

	private static int updates = 0;

	static String pvName;
	private static PVManagerPV pv;

	private static PVListener listener;

	@BeforeClass
	public static void setUp() throws Exception {
		updates = 0;
		pvName =
		 "css:sine";
		// "css:count";
		// "css:setpoint";
		// "Ring_Diag:VFM:image";
		//"Ring_IDmp:Foil_Plunge:Psn";
		// "css:sensor";
		// "sim://noise";
		//"loc://test";
		// "CG1D:Cam:Cam1:AcquireTime";

		listener = new PVListener() {

			@Override
			public void pvValueUpdate(PV pv) {
				System.out.println("pvValueUpdate: " + Arrays.toString(
						ValueUtil.getAllBufferedDoubles((PMObjectValue) pv.getValue())));
				updates++;
			}

			@Override
			public void pvDisconnected(PV pv) {
				System.out.println("Disconnected: " + pv.getStateInfo());
			}
		};

		pv = new PVManagerPV(pvName, true, 100);
		pv.start();
		System.out.println("Connecting...");
		while (!pv.isConnected()) {
			Thread.sleep(100);
		}

	}

	@AfterClass
	public static void tearDown() throws Exception {
		Thread.sleep(30000);
		pv.stop();
	}

	@Test
	public void testAddListener() throws InterruptedException {
		pv.addListener(listener);
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
