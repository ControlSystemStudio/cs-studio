/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.sns;

import static org.junit.Assert.*;

import org.csstudio.diag.pvutil.model.FEC;
import org.csstudio.diag.pvutil.model.PV;
import org.csstudio.diag.pvutil.model.PVUtilDataAPI;
import org.csstudio.diag.pvutil.sns.SNSPVUtilData;

import org.junit.Test;

@SuppressWarnings("nls")
public class SNSPVUtilDataTest
{

	@Test
	public void testGetFECs() throws Exception
	{
		PVUtilDataAPI newPVUtil = new SNSPVUtilData();

		// pass string to getFECs(String) and have it return FEC[]
		// having a list is success
        FEC[] fecs = newPVUtil.getFECs("%LLRF%");
		assertTrue(fecs.length > 0);

		int i = 0;
		for (FEC fec : fecs)
		{
			System.out.println(fec);
			if ("No Records Returned".equals(fec.toString())) {
				System.out.println(fec);
			assertFalse("No Records Returned".equals(fec.toString()));
			}
			i++;
			if (i == 10) break;
		}
	}

	@Test
	public void testGetPVsFromFEC() throws Exception
	{
		PVUtilDataAPI newPVUtil = new SNSPVUtilData();

		// pass FEC string without PV filter to getPVs(String,String)
		// and have it return PV[]
		// having a list is success
		PV[] pvs = newPVUtil.getPVs("SCL_LLRF:IOC17d","");
		assertTrue(pvs.length > 0);
        int i = 0;
		for (PV pv : pvs)
		{
			System.out.println(pv);
			if ("No Records Returned".equals(pv.toString())) {
				System.out.println(pv);
				assertFalse("No Records Returned".equals(pv.toString()));
			}
			i++;
			if (i == 10) break;
		}
	}

	@Test
	public void testGetPVsFromPV() throws Exception
	{
		PVUtilDataAPI newPVUtil = new SNSPVUtilData();

		// pass empty FEC string and PV filter to getPVs(String,String)
		// and have it return PV[]
		// having a list is success
		PV[] pvs = newPVUtil.getPVs("","%:xAvg");
		assertTrue(pvs.length > 0);
        int i = 0;
		for (PV pv : pvs)
		{
			System.out.println(pv);
			if ("No Records Returned".equals(pv.toString())) {
				System.out.println(pv);
				assertFalse("No Records Returned".equals(pv.toString()));
			}
			i++;
			if (i == 10) break;
		}
	}

	@Test
	public void testGetPVsFromPVandFEC() throws Exception
	{
		PVUtilDataAPI newPVUtil = new SNSPVUtilData();

		// pass FEC string and PV filter to getPVs(String,String)
		// and have it return PV[]
		// having a list is success
		PV[] pvs = newPVUtil.getPVs("SCL_LLRF:IOC17d","%:Time%");
		assertTrue(pvs.length > 0);
        int i = 0;
		for (PV pv : pvs)
		{
			System.out.println(pv);
			if ("No Records Returned".equals(pv.toString())) {
				System.out.println(pv);
				assertFalse("No Records Returned".equals(pv.toString()));
			}
			i++;
			if (i == 10) break;
		}
	}
}
