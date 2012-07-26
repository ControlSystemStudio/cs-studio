package org.csstudio.alarm.beast.notifier.test;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.notifier.ActionID;
import org.csstudio.alarm.beast.notifier.AlarmNotifier;
import org.csstudio.alarm.beast.notifier.EActionPriority;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.WorkQueue;
import org.csstudio.alarm.beast.notifier.actions.NotificationActionFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link WorkQueue}
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class WorkQueueUnitTest {
	
	private void fireActions(AlarmNotifier alarm_notifer,
			AlarmTreeItem item,
			String aaPrefix,
			EActionPriority priority, int nb) 
	{
		ItemInfo info = ItemInfo.fromItem(item);
		WorkQueue work_queue = alarm_notifer.getWork_queue();
		for (int count = 0; count < nb; count++) {
			final AADataStructure aa = new AADataStructure(aaPrefix + count, "smsto:fake", 30);
			final ActionID id = WorkQueue.getActionID(item, aa);

			MockSMSNotificationAction action = new MockSMSNotificationAction();
			action.init(alarm_notifer, id, info, aa.getDelay(), aa.getDetails());
			action.setPriority(priority);

			work_queue.add(action);
		}
	}
	
	@Test
	public void testOverflow() 
	{
		try {
			final MockAlarmRDBHandler rdbHandler = new MockAlarmRDBHandler(true);
			AlarmTreePV pv1 = rdbHandler.findPV(TestConstants.PV_NAME);
			AlarmTreePV pv2 = rdbHandler.findPV(TestConstants.PV2_NAME);
			AlarmTreeItem sys = pv1.getClientParent();

			NotificationActionFactory factory = NotificationActionFactory.getInstance();
			factory.init(TestUtils.buildExtensionPoints());
			AlarmNotifier alarm_notifer = new AlarmNotifier(
					TestConstants.CONFIG_ROOT, 
					rdbHandler, 
					factory,
					TestConstants.THRESHOLD);
			rdbHandler.init(alarm_notifer);
			alarm_notifer.start();
			Thread.sleep(500);

			WorkQueue work_queue = alarm_notifer.getWork_queue();

			// Test PVs
			fireActions(alarm_notifer, pv1, "AA1_", EActionPriority.MAJOR, 3);
			Assert.assertTrue(work_queue.size() == 3);
			fireActions(alarm_notifer, pv1, "AA2_", EActionPriority.MINOR, 3);
			Assert.assertTrue(work_queue.size() == 6);
			fireActions(alarm_notifer, pv2, "AA3_", EActionPriority.MAJOR, 3);
			Assert.assertTrue(work_queue.size() == 9);
			fireActions(alarm_notifer, pv2, "AA4_", EActionPriority.MINOR, 3);
			Thread.sleep(5000);
			Assert.assertTrue(work_queue.size() == 0);
			
			// Test Systems
			fireActions(alarm_notifer, sys, "AA5_", EActionPriority.MAJOR, 5);
			Assert.assertTrue(work_queue.size() == 5);
			fireActions(alarm_notifer, sys, "AA6_", EActionPriority.MINOR, 6);
			Thread.sleep(5000);
			Assert.assertTrue(work_queue.size() == 0);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
		
}
