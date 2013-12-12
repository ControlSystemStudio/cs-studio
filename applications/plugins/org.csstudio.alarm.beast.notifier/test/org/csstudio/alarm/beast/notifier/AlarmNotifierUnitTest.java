/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.notifier.actions.AutomatedActionFactory;
import org.csstudio.alarm.beast.notifier.history.AlarmNotifierHistory;
import org.csstudio.alarm.beast.notifier.model.IActionHandler;
import org.csstudio.alarm.beast.notifier.model.IActionProvider;
import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.test.MockAlarmRDBHandler;
import org.csstudio.alarm.beast.notifier.test.UnitTestConstants;
import org.csstudio.alarm.beast.notifier.util.EMailCommandValidator;
import org.csstudio.alarm.beast.notifier.util.NotifierUtils;
import org.csstudio.alarm.beast.notifier.util.SmsCommandValidator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link AlarmNotifier} workflow.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 * 
 */
public class AlarmNotifierUnitTest {

	private static final Integer MAX_WAIT_DELAY = 10;
	private static ConcurrentHashMap<Integer, Boolean> actionMap;
	private static Integer currentId = 0;

	private static class EmptyAction implements IAutomatedAction {

		private final Integer uniqueId;

		public EmptyAction() {
			this.uniqueId = ++currentId;
		}

		@Override
		public void init(ItemInfo item, AAData auto_action,
				IActionHandler handler) throws Exception {
			actionMap.put(uniqueId, false);
		}

		@Override
		public void execute(List<PVSnapshot> pvs) throws Exception {
			actionMap.put(uniqueId, true);
		}
	}

	// Build action extension point map with mock classes
	private static Map<String, IActionProvider> buildExtensionPoints() {
		final Map<String, IActionProvider> schemeMap = new HashMap<String, IActionProvider>();

		schemeMap.put("mailto", new IActionProvider() {
			@Override
			public IActionValidator getValidator() {
				return new EMailCommandValidator();
			}

			@Override
			public IAutomatedAction getNotifier() {
				return new EmptyAction();
			}
		});

		schemeMap.put("smsto", new IActionProvider() {
			@Override
			public IActionValidator getValidator() {
				return new SmsCommandValidator();
			}

			@Override
			public IAutomatedAction getNotifier() {
				return new EmptyAction();
			}
		});

		schemeMap.put("cmd", new IActionProvider() {
			@Override
			public IActionValidator getValidator() {
				return null;
			}

			@Override
			public IAutomatedAction getNotifier() {
				return new EmptyAction();
			}
		});

		return schemeMap;
	}

	private static void wait(final WorkQueue workQueue) {
		int count = 0;
		while (workQueue.countPendingActions() > 0) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) { }
			if (++count > (MAX_WAIT_DELAY * 4)) {
				Assert.fail("No response received from action threads");
				break;
			}
		}
	}
	
	private static WorkQueue init(final MockAlarmRDBHandler rdbHandler)
			throws Exception {
		actionMap = new ConcurrentHashMap<Integer, Boolean>();
		final AutomatedActionFactory factory = AutomatedActionFactory.getInstance();
		factory.init(buildExtensionPoints());
		final AlarmNotifier notifier = new AlarmNotifier(
				UnitTestConstants.CONFIG_ROOT, rdbHandler, factory,
				UnitTestConstants.TIMER_THRESHOLD);
		rdbHandler.init(notifier);
		notifier.start();
		final WorkQueue workQueue = notifier.getWorkQueue();
		workQueue.setDebug(true); // turn on action history
		AlarmNotifierHistory.getInstance().clearAll();
		return workQueue;
	}

	/**
	 * Test PV alarm state changes.
	 * 
	 * Initializes a RDB mock, finds a PV item with 1 automated action and calculates its {@link ActionID}. 
	 * When the {@link WorkQueue} is set to debug=true, each action is recorded in the history. 
	 * This test sends one or more alarm update to the model, waits for the work queue to finish
	 * executing scheduled actions if necessary and checks the
	 * {@link EActionStatus} retrieved with {@link ActionID} from the history.
	 * 
	 * EXECUTED => executed by the timer AFTER the delay 
	 * FORCED => interrupted + executed WITHOUT delay 
	 * CANCELED => NOT executed AFTER the delay
	 * 
	 */
	@Test
	public void testPVStateChange() {
		final MockAlarmRDBHandler rdbHandler = new MockAlarmRDBHandler(false);
		try {
			final WorkQueue workQueue = init(rdbHandler);
			final AlarmNotifierHistory history = AlarmNotifierHistory.getInstance();
			final AlarmTreePV pv = rdbHandler.findPV(UnitTestConstants.PV_NAME);
			final ActionID actionId = NotifierUtils.getActionID(pv,
					pv.getAutomatedActions()[0]);

			// A. Minor => EXECUTED AFTER DELAY
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MINOR, SeverityLevel.MINOR);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.EXECUTED,
					history.getAction(actionId).getStatus());

			// B. Major => EXECUTED NO DELAY
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MAJOR, SeverityLevel.MAJOR);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.FORCED,
					history.getAction(actionId).getStatus());

			// C. Minor => EXECUTED NO DELAY
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MINOR, SeverityLevel.MAJOR);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.FORCED,
					history.getAction(actionId).getStatus());

			// D. Ok => EXECUTED NO DELAY
			rdbHandler.updatePV(pv.getName(), SeverityLevel.OK, SeverityLevel.MAJOR);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.FORCED,
					history.getAction(actionId).getStatus());

			// E. ACK => EXECUTED NO DELAY
			rdbHandler.updatePV(pv.getName(), SeverityLevel.OK, SeverityLevel.OK);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.FORCED,
					history.getAction(actionId).getStatus());

			// F. Minor => EXECUTED AFTER DELAY
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MINOR, SeverityLevel.MINOR);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.EXECUTED,
					history.getAction(actionId).getStatus());

			// F. ACK => EXECUTED NO DELAY
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MINOR, SeverityLevel.MINOR_ACK);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.FORCED,
					history.getAction(actionId).getStatus());

			// G. Major => CANCELED
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MAJOR, SeverityLevel.MAJOR);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.CANCELED,
					history.getAction(actionId).getStatus());

			// G. ACK => CANCELED
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MAJOR, SeverityLevel.MAJOR_ACK);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.CANCELED,
					history.getAction(actionId).getStatus());

			// H. Minor => CANCELED
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MINOR, SeverityLevel.MAJOR_ACK);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.CANCELED,
					history.getAction(actionId).getStatus());

			// I. un-ACK => EXECUTED NO DELAY
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MINOR, SeverityLevel.MAJOR);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.FORCED,
					history.getAction(actionId).getStatus());

			// J. ACK => EXECUTED NO DELAY
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MINOR, SeverityLevel.MINOR_ACK);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.FORCED,
					history.getAction(actionId).getStatus());

			// J. Ok => CANCELED
			rdbHandler.updatePV(pv.getName(), SeverityLevel.OK, SeverityLevel.OK);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.CANCELED,
					history.getAction(actionId).getStatus());

			// K. Minor + Ok => CANCELED
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MINOR, SeverityLevel.MINOR);
			rdbHandler.updatePV(pv.getName(), SeverityLevel.OK, SeverityLevel.MINOR);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.CANCELED,
					history.getAction(actionId).getStatus());

			// K. ACK => CANCELED
			rdbHandler.updatePV(pv.getName(), SeverityLevel.OK, SeverityLevel.OK);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.CANCELED,
					history.getAction(actionId).getStatus());

			// L. Minor + ACK => CANCELED
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MINOR, SeverityLevel.MINOR);
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MINOR, SeverityLevel.MINOR_ACK);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.CANCELED,
					history.getAction(actionId).getStatus());

			// B. Major => CANCELED
			// C. Minor => CANCELED
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MAJOR, SeverityLevel.MAJOR);
			rdbHandler.updatePV(pv.getName(), SeverityLevel.MINOR, SeverityLevel.MAJOR);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.CANCELED,
					history.getAction(actionId).getStatus());

			// D. Ok => CANCELED
			rdbHandler.updatePV(pv.getName(), SeverityLevel.OK, SeverityLevel.MAJOR);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.CANCELED,
					history.getAction(actionId).getStatus());

			// E. ACK => CANCELED
			rdbHandler.updatePV(pv.getName(), SeverityLevel.OK, SeverityLevel.OK);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.CANCELED,
					history.getAction(actionId).getStatus());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test System alarm state changes.
	 * 
	 * Initializes a RDB mock, finds 2 PV items and 1 System item with 1 automated action 
	 * and calculates system's {@link ActionID}. 
	 * When the {@link WorkQueue} is set to debug=true, each action is recorded in the history. 
	 * This test sends one or more alarm update to the model, waits for the work queue to finish
	 * executing scheduled actions if necessary and checks the
	 * {@link EActionStatus} retrieved with {@link ActionID} from the history.
	 * 
	 * EXECUTED => executed by the timer AFTER the delay 
	 * FORCED => interrupted + executed WITHOUT delay 
	 * CANCELED => NOT executed AFTER the delay
	 * 
	 */
	@Test
	public void testSystemStateChange() {
		final MockAlarmRDBHandler rdbHandler = new MockAlarmRDBHandler(true);
		try {
			final WorkQueue workQueue = init(rdbHandler);
			final AlarmNotifierHistory history = AlarmNotifierHistory
					.getInstance();
			final AlarmTreePV pv1 = rdbHandler.findPV(UnitTestConstants.PV_NAME);
			final AlarmTreePV pv2 = rdbHandler.findPV(UnitTestConstants.PV2_NAME);
			final AlarmTreeItem sys = pv1.getParent();
			final ActionID actionId = NotifierUtils.getActionID(sys,
					sys.getAutomatedActions()[0]);

			// PV1 Minor + PV2 Major => EXECUTED AFTER DELAY
			rdbHandler.updatePV(pv1.getName(), SeverityLevel.MINOR, SeverityLevel.MINOR);
			rdbHandler.updatePV(pv2.getName(), SeverityLevel.MAJOR, SeverityLevel.MAJOR);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.EXECUTED,
					history.getAction(actionId).getStatus());

			// PV2 ACK => EXECUTED NO DELAY
			rdbHandler.updatePV(pv2.getName(), SeverityLevel.MAJOR, SeverityLevel.MAJOR_ACK);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.FORCED,
					history.getAction(actionId).getStatus());

			// PV2 Minor + PV1 Major => EXECUTED NO DELAY
			rdbHandler.updatePV(pv2.getName(), SeverityLevel.MINOR, SeverityLevel.MAJOR_ACK);
			rdbHandler.updatePV(pv1.getName(), SeverityLevel.MAJOR, SeverityLevel.MAJOR);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.FORCED,
					history.getAction(actionId).getStatus());

			// PV1 ACK => EXECUTED NO DELAY
			rdbHandler.updatePV(pv1.getName(), SeverityLevel.MAJOR, SeverityLevel.MAJOR_ACK);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.FORCED,
					history.getAction(actionId).getStatus());

			// PV2 Ok => CANCELED
			rdbHandler.updatePV(pv2.getName(), SeverityLevel.OK, SeverityLevel.OK);
			wait(workQueue); // wait delay
			Assert.assertNotNull(history.getAction(actionId));
			Assert.assertEquals(EActionStatus.CANCELED,
					history.getAction(actionId).getStatus());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
