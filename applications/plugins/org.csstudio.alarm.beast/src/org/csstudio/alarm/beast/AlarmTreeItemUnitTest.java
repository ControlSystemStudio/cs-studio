/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.platform.data.TimestampFactory;
import org.junit.Test;

/** JUnit Test of AlarmTreeItem
 *  @author Kay Kasemir, Xihui Chen
 */
@SuppressWarnings("nls")
public class AlarmTreeItemUnitTest
{
    @Test
    public void testAlarmTreeItem() throws Exception
    {
        // Build basic tree
        final List<GDCDataStructure> guidance = new ArrayList<GDCDataStructure>(
        		Arrays.asList(
        				new GDCDataStructure("Run", "Run as fast as you can"),
        				new GDCDataStructure("Fix", "Try to fix it")
        		));
        final List<GDCDataStructure> displays = new ArrayList<GDCDataStructure>(
        		Arrays.asList(
        				new GDCDataStructure("main.edl", "edm main.edl"),
        				new GDCDataStructure("master.stp", "StripTool master.stp")
        		));

        final List<GDCDataStructure> commands = new ArrayList<GDCDataStructure>(
        		Arrays.asList(
        				new GDCDataStructure("reset PV123", "caput PV123 Reset")
        		));


        final AlarmTreeRoot tree = new AlarmTreeRoot(0, "Root");

        final AlarmTreeItem dtl = new AlarmTreeItem(0, "DTL", tree);
        final AlarmTreeItem dtl_vac = new AlarmTreeItem(0, "Vacuum", dtl);
        AlarmTreePV pv = new AlarmTreePV(0, "DTL_Vac:Sensor1:Pressure", dtl_vac);
        pv.setDescription("Description");
        pv.setGuidance(guidance);
        pv.setDisplays(displays);
        pv.setCommands(commands);
        pv = new AlarmTreePV(0, "DTL_Vac:Sensor2:Pressure", dtl_vac);

        assertEquals("/Root/DTL/Vacuum/DTL_Vac:Sensor2:Pressure", pv.getPathName());
        assertEquals(pv, tree.getItemByPath("/Root/DTL/Vacuum/DTL_Vac:Sensor2:Pressure"));

        pv.setGuidance(guidance);
        pv.setDisplays(displays);
        final AlarmTreeItem dtl_rccs = new AlarmTreeItem(0, "RCCS", dtl);
        pv = new AlarmTreePV(0, "DTL_RCCS:Sensor1:Flow", dtl_rccs);
        pv.setGuidance(guidance);
        pv.setDisplays(displays);
        pv = new AlarmTreePV(0, "DTL_RCCS:Sensor1:Temp", dtl_rccs);
        pv.setGuidance(guidance);
        pv.setDisplays(displays);

        final AlarmTreeItem ccl = new AlarmTreeItem(0, "CCL", tree);
        final AlarmTreeItem ccl_vac = new AlarmTreeItem(0, "Vacuum", ccl);
        pv = new AlarmTreePV(0, "DTL_RCCS:Sensor1:Temp", ccl_vac);

        assertEquals(5, tree.getPVCount());

        // Check severity propagation
        assertEquals(SeverityLevel.OK, ccl.getSeverity());
        pv.setAlarmState(SeverityLevel.MINOR, "Nuissance",
                         SeverityLevel.MAJOR, "Problem",
                         "Value", TimestampFactory.now());
        assertEquals(SeverityLevel.MINOR, ccl.getCurrentSeverity());
        assertEquals(SeverityLevel.MAJOR, ccl.getSeverity());
        assertEquals("Problem", ccl.getMessage());

        tree.dump(System.out);

        System.out.println("Total tree element count: " + tree.getElementCount());
        assertEquals(11, tree.getElementCount());
    }
}
