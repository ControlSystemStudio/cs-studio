/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.test;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.client.GDCDataStructure;

/**
 * Unit test utilities.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
@SuppressWarnings("nls")
public class UnitTestUtils {

	/** Build a basic AlarmTree */
	public static AlarmTreeRoot buildBasicTree(boolean configureSystem) {
		final GDCDataStructure guidance[] = new GDCDataStructure[] {
				new GDCDataStructure("Run", "Run as fast as you can"),
				new GDCDataStructure("Fix", "Try to fix it") };
		final GDCDataStructure displays[] = new GDCDataStructure[] {
				new GDCDataStructure("main.edl", "edm main.edl"),
				new GDCDataStructure("master.stp", "StripTool master.stp") };
		final GDCDataStructure commands[] = new GDCDataStructure[] { new GDCDataStructure(
				"reset PV123", "caput PV123 Reset") };
		final AADataStructure automated_actions[] = new AADataStructure[] {
				new AADataStructure("Send EMail", "mailto:paul@home.there", 2)
				// new AADataStructure("Execute Cmd", "cmd:caput PV123 Reset", 1) 
				};

		// Root
		// ---DTL
		// ------Vacuum
		// 		...1:Pressure
		// 		...2:Pressure
		// ------RCCS
		// 		...Flow
		// 		...Temp
		// ---CCL
		// ------Vacuum
		// 		...Pressure
		final AlarmTreeRoot tree = new AlarmTreeRoot("Root", 0);
		final AlarmTreeItem dtl = new AlarmTreeItem(tree, "DTL", 0);

		final MockAlarmTreeItem dtl_vac = new MockAlarmTreeItem(dtl, "Vacuum", 0);
		if (configureSystem) {
			dtl_vac.setAutomatedActions(automated_actions);
		}
		MockAlarmTreePV pv = new MockAlarmTreePV(dtl_vac, "DTL_Vac:Sensor1:Pressure", 0);
		pv.setLatching(true);
		pv.setDescription("*Water below {1} m3, {0} alarm");
		if (!configureSystem) {
			pv.setGuidance(guidance);
			pv.setDisplays(displays);
			pv.setCommands(commands);
			pv.setAutomatedActions(automated_actions);
		}
		pv = new MockAlarmTreePV(dtl_vac, "DTL_Vac:Sensor2:Pressure", 0);
		pv.setLatching(true);
		pv.setDescription("!Invalid pressure");
		if (!configureSystem) {
			pv.setAutomatedActions(automated_actions);
		}

		final AlarmTreeItem dtl_rccs = new AlarmTreeItem(dtl, "RCCS", 0);
		pv = new MockAlarmTreePV(dtl_rccs, "DTL_RCCS:Sensor1:Flow", 0);
		pv.setGuidance(guidance);
		pv.setDisplays(displays);
		pv = new MockAlarmTreePV(dtl_rccs, "DTL_RCCS:Sensor1:Temp", 0);
		pv.setGuidance(guidance);
		pv.setDisplays(displays);

		final AlarmTreeItem ccl = new AlarmTreeItem(tree, "CCL", 0);
		final AlarmTreeItem ccl_vac = new AlarmTreeItem(ccl, "Vacuum", 0);
		pv = new MockAlarmTreePV(ccl_vac, "CCL_Vac:Sensor:Pressure", 0);

		final List<AlarmTreeLeaf> leaves = new ArrayList<AlarmTreeLeaf>();
		tree.addLeavesToList(leaves);

		return tree;
	}

}
