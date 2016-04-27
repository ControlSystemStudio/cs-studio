/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.SeverityLevel;
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
        final GDCDataStructure guidance[] = new GDCDataStructure[]
        {
                new GDCDataStructure("Run", "Run as fast as you can"),
                new GDCDataStructure("Fix", "Try to fix it")
        };
        final GDCDataStructure displays[] = new GDCDataStructure[]
        {
                new GDCDataStructure("main.edl", "edm main.edl"),
                new GDCDataStructure("master.stp", "StripTool master.stp")
        };

        final GDCDataStructure commands[] = new GDCDataStructure[]
        {
                new GDCDataStructure("reset PV123", "caput PV123 Reset")
        };

        final AADataStructure automated_actions[] = new AADataStructure[]
        {
                new AADataStructure("Send Mail", "paul@home.there", 30)
        };

        // Root
        //    DTL
        //       Vacuum
        //            ...1:Pressure
        //            ...2:Pressure
        //       RCCS
        //            ...Flow
        //            ...Temp
        //    CCL
        //       Vacuum
        //            ...Pressure
        final AlarmTreeRoot tree = new AlarmTreeRoot("Root", 0);
        final AlarmTreeItem dtl = new AlarmTreeItem(tree, "DTL", 0);
        final AlarmTreeItem dtl_vac = new AlarmTreeItem(dtl, "Vacuum", 0);
        AlarmTreePV pv = new AlarmTreePV(dtl_vac, "DTL_Vac:Sensor1:Pressure", 0);
        pv.setDescription("Description");
        pv.setGuidance(guidance);
        pv.setDisplays(displays);
        pv.setCommands(commands);
        pv.setAutomatedActions(automated_actions);
        pv = new AlarmTreePV(dtl_vac, "DTL_Vac:Sensor2:Pressure", 0);

        assertEquals("/Root/DTL/Vacuum/DTL_Vac:Sensor2:Pressure", pv.getPathName());
        assertEquals(pv, tree.getItemByPath("/Root/DTL/Vacuum/DTL_Vac:Sensor2:Pressure"));

        pv.setGuidance(guidance);
        pv.setDisplays(displays);
        final AlarmTreeItem dtl_rccs = new AlarmTreeItem(dtl, "RCCS", 0);
        pv = new AlarmTreePV(dtl_rccs, "DTL_RCCS:Sensor1:Flow", 0);
        pv.setGuidance(guidance);
        pv.setDisplays(displays);
        pv = new AlarmTreePV(dtl_rccs, "DTL_RCCS:Sensor1:Temp", 0);
        pv.setGuidance(guidance);
        pv.setDisplays(displays);

        final AlarmTreeItem ccl = new AlarmTreeItem(tree, "CCL", 0);
        final AlarmTreeItem ccl_vac = new AlarmTreeItem(ccl, "Vacuum", 0);
        pv = new AlarmTreePV(ccl_vac, "CCL_Vac:Sensor:Pressure", 0);

        assertEquals(5, tree.getLeafCount());
        final List<AlarmTreeLeaf> leaves = new ArrayList<AlarmTreeLeaf>();
        tree.addLeavesToList(leaves);
        assertEquals(5, leaves.size());
        for (AlarmTreeLeaf leaf : leaves)
            System.out.println(leaf.getPathName());
        assertEquals("DTL_Vac:Sensor1:Pressure", leaves.get(0).getName());
        assertEquals("CCL_Vac:Sensor:Pressure", leaves.get(4).getName());

        // Check severity propagation
        assertEquals(SeverityLevel.OK, ccl.getSeverity());
        pv.setAlarmState(SeverityLevel.MINOR, "Nuissance",
                         SeverityLevel.MAJOR, "Problem",
                         "Value", Instant.now());
        assertEquals(SeverityLevel.MINOR, ccl.getCurrentSeverity());
        assertEquals(SeverityLevel.MAJOR, ccl.getSeverity());
        assertEquals("Problem", ccl.getMessage());
        tree.dump(System.out);

        // Check propagation of alarm that clears
        pv.setAlarmState(SeverityLevel.OK, "Nuissance",
                SeverityLevel.MAJOR_ACK, "Problem2",
                "Value2", Instant.now());
        assertEquals(SeverityLevel.OK, ccl.getCurrentSeverity());
        assertEquals(SeverityLevel.MAJOR_ACK, ccl.getSeverity());
        assertEquals("Problem2", ccl.getMessage());
        tree.dump(System.out);

        System.out.println("Total tree element count: " + tree.getElementCount());
        assertEquals(11, tree.getElementCount());
    }
}
