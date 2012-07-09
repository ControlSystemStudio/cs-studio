package org.csstudio.alarm.beast.notifier.test;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.MapMessage;

import org.csstudio.alarm.beast.JMSAlarmMessage;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreePosition;
import org.csstudio.alarm.beast.notifier.Activator;
import org.csstudio.alarm.beast.notifier.AlarmNotifier;
import org.csstudio.alarm.beast.notifier.EActionPriority;
import org.csstudio.alarm.beast.notifier.EActionStatus;
import org.csstudio.alarm.beast.notifier.actions.NotificationActionFactory;
import org.csstudio.alarm.beast.notifier.actions.NotificationActionListener;
import org.csstudio.alarm.beast.notifier.model.INotificationAction;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmUpdateInfo;
import org.csstudio.logging.JMSLogMessage;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for {@link AlarmNotifier} and its behavior.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class AlarmNotifierUnitTest {
	
	/** Format of time stamps */
	final private SimpleDateFormat date_format = new SimpleDateFormat(
			JMSLogMessage.DATE_FORMAT);
	
	private AtomicInteger threadCounter;
	
	public static abstract class AbsctractTestActionListener implements NotificationActionListener
	{
		public abstract void actionCompleted(INotificationAction action);
	}
	
	private class ActionChecker 
	{
		private final EActionStatus expectedStatus;
		private final EActionPriority expectedPriority;
		
		private EActionStatus currentStatus;
		private EActionPriority currentPriority;

		public ActionChecker(final EActionStatus expectedStatus,
				final EActionPriority expectedPriority) 
		{
			this.expectedStatus = expectedStatus;
			this.expectedPriority = expectedPriority;
		}
		
		public boolean check() {
			return expectedStatus.equals(currentStatus)
					&& expectedPriority.equals(currentPriority);
		}
		
		public void fill(INotificationAction action) {
			this.currentStatus = action.getActionStatus();
			this.currentPriority = action.getActionPriority();
		}
	}
	
	private void fillAlarmCommon(final MapMessage map, 
			final String text) throws Exception {
		map.setString(JMSLogMessage.TYPE, JMSAlarmMessage.TYPE_ALARM);
		map.setString(JMSAlarmMessage.CONFIG, TestConstants.CONFIG_ROOT);
		map.setString(JMSLogMessage.TEXT, text);
		map.setString(JMSLogMessage.APPLICATION_ID, Activator.ID);
		map.setString(JMSLogMessage.HOST, InetAddress.getLocalHost().getHostName());
		map.setString(JMSLogMessage.USER, System.getProperty("user.name"));
	}
	
	private void fillAlarm(final MapMessage map, 
			final String pv,
			final SeverityLevel current_severity, 
			final String current_message,
			final SeverityLevel alarm_severity, 
			final String alarm_message,
			final String value) throws Exception {
		// Common fields
		fillAlarmCommon(map, JMSAlarmMessage.TEXT_STATE);
		
		// Alarm fields
		map.setString(JMSLogMessage.NAME, pv);
		map.setString(JMSLogMessage.SEVERITY, alarm_severity.name());
		map.setString(JMSAlarmMessage.STATUS, alarm_message);
		if (value != null) map.setString(JMSAlarmMessage.VALUE, value);
		map.setString(JMSAlarmMessage.EVENTTIME, date_format.format(new Date()));
		map.setString(JMSAlarmMessage.CURRENT_SEVERITY, current_severity.name());
		map.setString(JMSAlarmMessage.CURRENT_STATUS, current_message);
	}
	
	@SuppressWarnings("unused")
	private void fillAlarmMaintenance(final MapMessage map, 
			final String pv,
			final SeverityLevel current_severity, 
			final String current_message,
			final SeverityLevel alarm_severity, 
			final String alarm_message,
			final String value) throws Exception {
		// Common fields
		fillAlarmCommon(map, JMSAlarmMessage.TEXT_STATE_MAINTENANCE);

		// Alarm fields
		map.setString(JMSLogMessage.NAME, pv);
		map.setString(JMSLogMessage.SEVERITY, alarm_severity.name());
		map.setString(JMSAlarmMessage.STATUS, alarm_message);
		if (value != null) map.setString(JMSAlarmMessage.VALUE, value);
		map.setString(JMSAlarmMessage.EVENTTIME, date_format.format(new Date()));
		map.setString(JMSAlarmMessage.CURRENT_SEVERITY, current_severity.name());
		map.setString(JMSAlarmMessage.CURRENT_STATUS, current_message);
	}
	
	@SuppressWarnings("unused")
	private void fillIdle(final MapMessage map) throws Exception {
		// Common fields
		fillAlarmCommon(map, JMSAlarmMessage.TEXT_IDLE);
	}
	
	@SuppressWarnings("unused")
	private void fillIdleMaintenance(final MapMessage map) throws Exception {
		// Common fields
		fillAlarmCommon(map, JMSAlarmMessage.TEXT_IDLE_MAINTENANCE);
	}
	
	@SuppressWarnings("unused")
	private void fillConfig(final MapMessage map,
			final String path) throws Exception {
		// Common fields
		fillAlarmCommon(map, JMSAlarmMessage.TEXT_CONFIG);
		map.setString(JMSLogMessage.NAME, path);
	}
	
	@SuppressWarnings("unused")
	private int findMaxDelay(AADataStructure[] auto_actions) {
		int delay = 0;
		for (AADataStructure auto_action : auto_actions)
			if (auto_action.getDelay() > delay)
				delay = auto_action.getDelay();
		return delay;
	}
	
	private int countAutomatedActions(final AlarmTreeItem item) {
		int aaCount = item.getAutomatedActions().length;
		AlarmTreeItem parent = item.getClientParent();
		while (parent != null) {
			aaCount += parent.getAutomatedActions().length;
			parent = parent.getClientParent();
			if (parent.getPosition().equals(AlarmTreePosition.Root))
				break;
		}

		return aaCount;
	}
	
	private void assertActions(final AlarmNotifier alarm_notifer,
			final AlarmTreeItem item, 
			final EActionStatus expectedStatus,
			final EActionPriority expectedPriority) 
	{
		final int aaCount = countAutomatedActions(item);
		
		// WARNING: take care of parents automated actions
		// Assert.assertTrue(alarm_notifer.getWork_queue().size() == aaCount);
		// alarm_notifer.getWork_queue().dump();

		threadCounter = new AtomicInteger(aaCount);
		final List<ActionChecker> actionCheckers = new ArrayList<ActionChecker>(aaCount);

		for (AADataStructure auto_action : item.getAutomatedActions()) {
			INotificationAction action = alarm_notifer.getWork_queue().findAction(item, auto_action);
			if(action != null) {
				action.addListener(new AbsctractTestActionListener() {
					@Override
					public void actionCompleted(INotificationAction action) {
						ActionChecker checker = new ActionChecker(expectedStatus, expectedPriority);
						checker.fill(action);
						synchronized (actionCheckers) {
							actionCheckers.add(checker);
						}
						threadCounter.decrementAndGet();
					}
				});
			} else {
				threadCounter.decrementAndGet();
			}
		}
		try {
			while (threadCounter.get() > 0)
				Thread.sleep(500);
		} catch (InterruptedException e) { 
			Assert.fail();
		}
		Assert.assertTrue(threadCounter.get() == 0);
		for (ActionChecker checker : actionCheckers)
			Assert.assertTrue(checker.check());
		Assert.assertTrue(alarm_notifer.getWork_queue().size() == 0);
	}
	
	@Test
	public void testSimpleCase() 
	{
		AlarmNotifier alarm_notifer;
		SimpleJMSPublisher publisher;
		try {
			publisher = new SimpleJMSPublisher(TestConstants.JMS_HOST, TestConstants.SERVER_TOPIC);
			NotificationActionFactory factory = NotificationActionFactory.getInstance();
			factory.init(TestUtils.buildExtensionPoints());
			final MockAlarmRDBHandler rdbHandler = new MockAlarmRDBHandler(false);
			alarm_notifer = new AlarmNotifier(TestConstants.CONFIG_ROOT, rdbHandler, factory, TestConstants.THRESHOLD);
			rdbHandler.init(alarm_notifer);
			alarm_notifer.start();
			Thread.sleep(500);
			
			// Simple sequential scenario
			MapMessage msg = publisher.createMapMessage();
			AlarmTreePV pv = rdbHandler.findPV(TestConstants.PV_NAME);
			
			// 1. JMS alarm STATE PV1 Minor
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.MINOR, "",
					SeverityLevel.MINOR, "", 
					TestConstants.PV_VALUE_MINOR);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			assertActions(alarm_notifer, pv, EActionStatus.OK, EActionPriority.MINOR);
			
			// 2. JMS alarm STATE PV1 Major
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.MAJOR, "",
					SeverityLevel.MAJOR, "", 
					TestConstants.PV_VALUE_MAJOR);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			assertActions(alarm_notifer, pv, EActionStatus.OK, EActionPriority.MAJOR);
			
			// 3. JMS alarm STATE PV1 Minor 
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.MINOR, "",
					SeverityLevel.MAJOR, "", 
					TestConstants.PV_VALUE_MINOR);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			assertActions(alarm_notifer, pv, EActionStatus.OK, EActionPriority.MINOR);
			
			// 4. JMS alarm STATE PV1 Major
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.MAJOR, "",
					SeverityLevel.MAJOR, "", 
					TestConstants.PV_VALUE_MAJOR);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			assertActions(alarm_notifer, pv, EActionStatus.OK, EActionPriority.MAJOR);
			
			// 5. JMS alarm STATE PV1 Invalid
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.INVALID, "",
					SeverityLevel.INVALID, "", 
					TestConstants.PV_VALUE_INVALID);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			assertActions(alarm_notifer, pv, EActionStatus.OK, EActionPriority.MAJOR);
			
			// 6. JMS alarm STATE PV1 No_Alarm
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.OK, "",
					SeverityLevel.INVALID, "", 
					TestConstants.PV_VALUE_OK);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			assertActions(alarm_notifer, pv, EActionStatus.OK, EActionPriority.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testComplexCase() 
	{
		AlarmNotifier alarm_notifer;
		SimpleJMSPublisher publisher;
		try {
			publisher = new SimpleJMSPublisher(TestConstants.JMS_HOST, TestConstants.SERVER_TOPIC);
			NotificationActionFactory factory = NotificationActionFactory.getInstance();
			factory.init(TestUtils.buildExtensionPoints());
			final MockAlarmRDBHandler rdbHandler = new MockAlarmRDBHandler(false);
			alarm_notifer = new AlarmNotifier(TestConstants.CONFIG_ROOT, rdbHandler, factory, TestConstants.THRESHOLD);
			rdbHandler.init(alarm_notifer);
			alarm_notifer.start();
			Thread.sleep(500);
			
			// Complex sequential scenario
			MapMessage msg = publisher.createMapMessage();
			AlarmTreePV pv = rdbHandler.findPV(TestConstants.PV2_NAME);
			
			// JMS alarm STATE PV1 Minor
			// JMS alarm STATE PV1 Major
			// JMS alarm STATE PV1 Minor
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.MINOR, "",
					SeverityLevel.MINOR, "", 
					TestConstants.PV2_VALUE_MINOR);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			Thread.sleep(500);
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.MAJOR, "",
					SeverityLevel.MAJOR, "", 
					TestConstants.PV2_VALUE_MAJOR);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			Thread.sleep(500);
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.MINOR, "",
					SeverityLevel.MAJOR, "", 
					TestConstants.PV2_VALUE_MINOR);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			Thread.sleep(500);
			assertActions(alarm_notifer, pv, EActionStatus.OK, EActionPriority.IMPORTANT);
			
			// JMS alarm STATE PV1 Minor
			// JMS alarm STATE PV1 No_Alarm
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.MAJOR, "",
					SeverityLevel.MAJOR, "", 
					TestConstants.PV2_VALUE_MAJOR);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			Thread.sleep(500);
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.OK, "",
					SeverityLevel.MAJOR, "", 
					TestConstants.PV2_VALUE_OK);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			Thread.sleep(500);
			assertActions(alarm_notifer, pv, EActionStatus.CANCELED, EActionPriority.IMPORTANT);
			
			// JMS alarm STATE PV1 Major
			// JMS alarm STATE PV1 Major ACK
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.MAJOR, "",
					SeverityLevel.MAJOR, "", 
					TestConstants.PV2_VALUE_MAJOR);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			Thread.sleep(500);
			fillAlarm(msg, pv.getName(), 
					SeverityLevel.MAJOR, "",
					SeverityLevel.MAJOR_ACK, "", 
					TestConstants.PV2_VALUE_OK);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			Thread.sleep(500);
			assertActions(alarm_notifer, pv, EActionStatus.CANCELED, EActionPriority.IMPORTANT);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testSystem() 
	{
		AlarmNotifier alarm_notifer;
		SimpleJMSPublisher publisher;
		try {
			publisher = new SimpleJMSPublisher(TestConstants.JMS_HOST, TestConstants.SERVER_TOPIC);
			NotificationActionFactory factory = NotificationActionFactory.getInstance();
			factory.init(TestUtils.buildExtensionPoints());
			final MockAlarmRDBHandler rdbHandler = new MockAlarmRDBHandler(true);
			alarm_notifer = new AlarmNotifier(TestConstants.CONFIG_ROOT, rdbHandler, factory, TestConstants.THRESHOLD);
			rdbHandler.init(alarm_notifer);
			alarm_notifer.start();
			Thread.sleep(500);
			
			MapMessage msg = publisher.createMapMessage();
			AlarmTreePV pv1 = rdbHandler.findPV(TestConstants.PV_NAME);
			AlarmTreePV pv2 = rdbHandler.findPV(TestConstants.PV2_NAME);
			AlarmTreeItem item = pv1.getClientParent();
			
			// Complex sequential scenario
			fillAlarm(msg, pv1.getPathName(), 
					SeverityLevel.INVALID, "",
					SeverityLevel.INVALID, "", 
					TestConstants.PV_VALUE_INVALID);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			Thread.sleep(500);
			fillAlarm(msg, pv2.getPathName(), 
					SeverityLevel.MINOR, "",
					SeverityLevel.MINOR, "", 
					TestConstants.PV2_VALUE_MINOR);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			Thread.sleep(500);
			fillAlarm(msg, pv2.getPathName(), 
					SeverityLevel.MAJOR, "",
					SeverityLevel.MAJOR, "", 
					TestConstants.PV2_VALUE_MAJOR);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			Thread.sleep(500);
			fillAlarm(msg, pv2.getPathName(), 
					SeverityLevel.MINOR, "",
					SeverityLevel.MAJOR, "", 
					TestConstants.PV2_VALUE_MINOR);
			rdbHandler.updatePV(AlarmUpdateInfo.fromMapMessage(msg));
			Thread.sleep(500);
			assertActions(alarm_notifer, item, EActionStatus.OK, EActionPriority.IMPORTANT);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
}
