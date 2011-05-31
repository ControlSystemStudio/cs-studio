/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.rack.sns;

import static org.junit.Assert.*;


import org.csstudio.diag.rack.model.RackDataAPI;
import org.csstudio.diag.rack.model.RackList;
import org.junit.Test;

public class SNSRackDataTest {
	public int i = 0;

	@Test
	public void testGetRacks() throws Exception
	{
		System.out.println("testGetRacks()");
		RackDataAPI newRackUtil = new SNSRackData();
		
		// pass string to getFECs(String) and have it return FEC[]
		// having a list is success 
		String[] racks = newRackUtil.getRackNames("");
		assertTrue(racks.length > 0);
		
		for (String rack : racks) 
		{
			System.out.println(rack);
			if ("No Rows Selected".equals(rack.toString())) {
				System.out.println(rack);
			assertFalse("No Rows Selected".equals(rack.toString()));
			}
			i++;
			if (i == 10) break;
		}
			
		
	}
	
	@Test
	public void testGetRacksWithFilter() throws Exception
	{
		System.out.println(" ");
		System.out.println("testGetRacksWithFilter()");
		RackDataAPI newRackUtil = new SNSRackData();
		// pass string to getFECs(String) and have it return FEC[]
		// having a list is success 
		String[] racks = newRackUtil.getRackNames("%Diag%");
		assertTrue(racks.length > 0);
		
		for (String rack : racks) 
		{
			System.out.println(rack);
			if ("No Rows Selected".equals(rack.toString())) {
				System.out.println(rack);
			assertFalse("No Rows Selected".equals(rack.toString()));
			}
			i++;
			if (i == 10) break;
		}
			
		
	}

	@Test
	public void testGetEquipWithRack() throws Exception
	{	
		System.out.println(" ");
		System.out.println("testGetEquipWithRack()");

		RackDataAPI newRackUtil = new SNSRackData();
		// pass string to getFECs(String) and have it return FEC[]
		// having a list is success 
		RackList[] racks = newRackUtil.getRackListing("DTL_Diag:Cab06D10","F");
		assertTrue(racks.length > 0);
		
		for (RackList equip : racks) 
		{
			System.out.println(equip.getDvcId()+" - "+ equip.getDvcTypeId() +" - "+ equip.getBGN() +" - "+ equip.getEND());
			if ("No Rows Selected".equals(equip.getDvcId().toString())) {
				System.out.println(equip);
				assertFalse("No Rows Selected".equals(equip.getDvcId().toString()));
			}
			i++;
			if (i == 10) break;
		}
			
		
	}
	
	@Test
	public void testGetEquipWithDVC() throws Exception
	{
		System.out.println(" ");
		System.out.println("testGetEquipWithDVC()");
		
		RackDataAPI newRackUtil = new SNSRackData();
		// pass string to getFECs(String) and have it return FEC[]
		// having a list is success 
		RackList[] racks = newRackUtil.getRackListing("ICS:CoreSw1","F");
		assertTrue(racks.length > 0);
		
		for (RackList equip : racks) 
		{
			System.out.println(equip.getDvcId()+" - "+ equip.getDvcTypeId() +" - "+ equip.getBGN() +" - "+ equip.getEND());
			if ("No Rows Selected".equals(equip.getDvcId().toString())) {
				System.out.println(equip);
				assertFalse("No Rows Selected".equals(equip.getDvcId().toString()));
			}
			i++;
			if (i == 10) break;
		}
	}
			
		@Test
		public void testGetEquipWithPV() throws Exception
		{
			System.out.println(" ");
			System.out.println("testGetEquipWithPV()");

			RackDataAPI newRackUtil = new SNSRackData();
			// pass string to getFECs(String) and have it return FEC[]
			// having a list is success 
			RackList[] racks = newRackUtil.getRackListing("CCL_Diag:BLM00:AFEViewGain","F");
			assertTrue(racks.length > 0);
			for (RackList equip : racks) 
			{
				System.out.println(equip.getDvcId()+" - "+ equip.getDvcTypeId() +" - "+ equip.getBGN() +" - "+ equip.getEND());
				if ("No Rows Selected".equals(equip.getDvcId().toString())) {
					System.out.println(equip);
					assertFalse("No Rows Selected".equals(equip.getDvcId().toString()));
				}
				i++;
				if (i == 10) break;
			}
	
	}

		@Test
		public void testEmptyRack() throws Exception
		{
			System.out.println(" ");
			System.out.println("testEmptyRack()");

			RackDataAPI newRackUtil = new SNSRackData();
			// pass string to getFECs(String) and have it return FEC[]
			// having a list is success 
			RackList[] racks = newRackUtil.getRackListing("FE_ICS:Cab07","F");
			assertTrue(racks.length == 1);
			
			for (RackList equip : racks) 
			{
				if (!"No Rows Selected".equals(equip.getDvcId().toString())) {
					System.out.println("Rack Should be empty:"+equip);
				assertFalse(!"No Rows Selected".equals(equip.getDvcId().toString()));
				}
			}
		}

		@Test
		public void testnoParent() throws Exception
		{
			System.out.println(" ");
			System.out.println("testnoParent()");

			RackDataAPI newRackUtil = new SNSRackData();
			// pass string to getFECs(String) and have it return FEC[]
			// having a list is success 
			RackList[] racks = newRackUtil.getRackListing("DummyDeviceName","F");
			System.out.println("length: "+ racks.length);
			assertTrue(racks.length == 1);
			
			for (RackList equip : racks) 
			{
				System.out.println("equip:"+equip.getDvcId().toString());
				if (!"Unknown Rack Parent".equals(equip.getDvcId().toString())) {
					System.out.println("Device shouldn't have parent:"+equip);
				assertFalse(!"Unknown Rack Parent".equals(equip.getDvcId().toString()));				}
			}
		}

}
