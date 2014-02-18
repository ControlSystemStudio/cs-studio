/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.notifier.model.IActionHandler;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.test.MockAlarmRDBHandler;
import org.csstudio.alarm.beast.notifier.test.UnitTestConstants;
import org.csstudio.alarm.beast.notifier.util.NotifierUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link WorkQueue}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class WorkQueueUnitTest {

	private static ConcurrentHashMap<Integer, Boolean> actionMap;
	private static Integer currentId = 0;

	private static class EmptyAction implements IAutomatedAction {

		private final Integer uniqueId;

		public EmptyAction(Integer uniqueId) {
			this.uniqueId = uniqueId;
		}

		@Override
		public void init(ItemInfo item, AAData auto_action,
				IActionHandler handler) throws Exception {
		}

		@Override
		public void execute(List<PVSnapshot> pvs) throws Exception {
			actionMap.put(uniqueId, true);
		}
	}

	private void fireActions(final WorkQueue workQueue, AlarmTreeItem item,
			String aaPrefix, EActionPriority priority, int nb) {
		final ItemInfo info = ItemInfo.fromItem(item);
		for (int count = 0; count < nb; count++) {
			final AADataStructure aa = new AADataStructure(aaPrefix + count, "smsto:fake", 6);
			final ActionID id = NotifierUtils.getActionID(item, aa);
			final IAutomatedAction newAction = new EmptyAction(++currentId);
			final AlarmHandler newTask = new AlarmHandler(id, info, newAction, aa.getDelay());
			newTask.setPriority(priority);
			actionMap.put(currentId, false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					workQueue.schedule(newTask, false);
				}
			}).start();
		}
		try { Thread.sleep(500); } catch (InterruptedException e) { }
	}

	/**
	 * Test that a scheduled action is executed after the defined delay (5s).
	 */
	@Test
	public void testSchedule() {
		actionMap = new ConcurrentHashMap<Integer, Boolean>();

		final WorkQueue workQueue = new WorkQueue(10, 10000); // 10 actions per 10s
		final MockAlarmRDBHandler rdbHandler = new MockAlarmRDBHandler(false);
		try {
			final AlarmTreePV pv = rdbHandler.findPV(UnitTestConstants.PV_NAME);
			final ItemInfo info = ItemInfo.fromItem(pv);
			final AADataStructure aa = new AADataStructure("AA", "smsto:fake", 5);
			final ActionID id = NotifierUtils.getActionID(pv, aa);
			final IAutomatedAction newAction = new EmptyAction(0);
			final AlarmHandler newTask = new AlarmHandler(id, info, newAction, aa.getDelay());
			actionMap.put(currentId, false);
			// Schedule with delay = 5s
			workQueue.schedule(newTask, false);
			// Wait 5s
			Thread.sleep(5500);
			// Check that action as been executed
			Assert.assertTrue(actionMap.get(0));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test overflow.
	 * 
	 * Schedules actions in the work queue. A unique id is attributed to each
	 * action. When the action is initialized, its unique id is added to the
	 * actions map and its value is set to false. When the action is executed,
	 * it sets its value to true. The test waits for expected actions to be
	 * executed and asserts the size of the work queue before and after this.
	 * 
	 */
	@Test
	public void testOverflow() {
		actionMap = new ConcurrentHashMap<Integer, Boolean>();

		final WorkQueue workQueue = new WorkQueue(10, 10000); // 10 actions per 10s
		final MockAlarmRDBHandler rdbHandler = new MockAlarmRDBHandler(false);
		try {
			AlarmTreePV pv1 = rdbHandler.findPV(UnitTestConstants.PV_NAME);
			AlarmTreePV pv2 = rdbHandler.findPV(UnitTestConstants.PV2_NAME);
			AlarmTreeItem sys = pv1.getParent();

			// PV alarms overflow => execute only MAJOR
			currentId = 0;
			fireActions(workQueue, pv1, "AA1_", EActionPriority.MINOR, 3);
			Assert.assertEquals(3, workQueue.countPendingActions());
			fireActions(workQueue, pv1, "AA2_", EActionPriority.MAJOR, 3);
			Assert.assertEquals(6, workQueue.countPendingActions());
			fireActions(workQueue, pv2, "AA3_", EActionPriority.MINOR, 3);
			Assert.assertEquals(9, workQueue.countPendingActions());
			fireActions(workQueue, pv2, "AA4_", EActionPriority.MAJOR, 3);
			// Wait 10s
			Thread.sleep(10000);
			Assert.assertEquals(0, workQueue.countPendingActions());
			int count = 0;
			while (!(actionMap.get(4) && actionMap.get(5) && actionMap.get(6)
					&& actionMap.get(10) && actionMap.get(11) && actionMap.get(12))) {
				Thread.sleep(500);
				if (++count > 20) { // wait 10s max
					Assert.fail("No response received from action threads");
					break;
				}
			}

			// System alarms overflow => execute ALL
			currentId = 0;
			fireActions(workQueue, sys, "AA5_", EActionPriority.MAJOR, 5);
			Assert.assertEquals(5, workQueue.countPendingActions());
			fireActions(workQueue, sys, "AA6_", EActionPriority.MINOR, 5);
			fireActions(workQueue, sys, "AA7_", EActionPriority.MAJOR, 2);
			// Wait 6s
			Thread.sleep(6500);
			Assert.assertEquals(0, workQueue.countPendingActions());
			for (int i = 1; i < 13; i++)
				Assert.assertTrue(actionMap.get(i));

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

}
