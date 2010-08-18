package org.remotercp.util.status;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.junit.Test;
import org.remotercp.util.UtilActivator;

public class StatusUtilTest {

	@Test
	public void checkStatusTest() {
		List<IStatus> statusCollector = new ArrayList<IStatus>();

		IStatus stat1 = new Status(Status.OK, UtilActivator.PLUGIN_ID, "");
		IStatus stat2 = new Status(Status.OK, UtilActivator.PLUGIN_ID, "");
		IStatus stat3 = new Status(Status.WARNING, UtilActivator.PLUGIN_ID, "");
		IStatus stat4 = new Status(Status.ERROR, UtilActivator.PLUGIN_ID, "");
		IStatus stat5 = new Status(Status.INFO, UtilActivator.PLUGIN_ID, "");

		statusCollector.add(stat1);
		statusCollector.add(stat2);
		statusCollector.add(stat3);
		statusCollector.add(stat4);
		statusCollector.add(stat5);

		int checkStatus = StatusUtil.checkStatus(statusCollector);
		assertEquals(Status.ERROR, checkStatus);

		statusCollector.clear();

		stat1 = new Status(Status.INFO, UtilActivator.PLUGIN_ID, "");
		stat2 = new Status(Status.OK, UtilActivator.PLUGIN_ID, "");
		stat3 = new Status(Status.WARNING, UtilActivator.PLUGIN_ID, "");
		stat4 = new Status(Status.OK, UtilActivator.PLUGIN_ID, "");
		stat5 = new Status(Status.OK, UtilActivator.PLUGIN_ID, "");

		statusCollector.add(stat1);
		statusCollector.add(stat2);
		statusCollector.add(stat3);
		statusCollector.add(stat4);
		statusCollector.add(stat5);

		checkStatus = StatusUtil.checkStatus(statusCollector);

		assertEquals(Status.WARNING, checkStatus);

		statusCollector.clear();
		stat1 = new Status(Status.INFO, UtilActivator.PLUGIN_ID, "");
		stat2 = new Status(Status.OK, UtilActivator.PLUGIN_ID, "");
		stat3 = new Status(Status.INFO, UtilActivator.PLUGIN_ID, "");
		stat4 = new Status(Status.OK, UtilActivator.PLUGIN_ID, "");
		stat5 = new Status(Status.OK, UtilActivator.PLUGIN_ID, "");
		
		statusCollector.add(stat1);
		statusCollector.add(stat2);
		statusCollector.add(stat3);
		statusCollector.add(stat4);
		statusCollector.add(stat5);
		
		checkStatus = StatusUtil.checkStatus(statusCollector);
		assertEquals(Status.OK, checkStatus);
	}

}
