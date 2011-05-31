/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.sns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.diag.pvfields.model.PVFieldsAPI;
import org.csstudio.diag.pvfields.model.PVInfo;
import org.junit.Test;

/** JUnit test of SNSPVFieldsData
 *  @author Dave Purcell
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSPVFieldsDataTest
{
    @Test
	public void testGetFieldsUsingPV() throws Exception
	{
	    final PVFieldsAPI newPVField = new SNSPVFieldsData();

		// pass PV to getPVs(String,String)
		// and have it return PV[]
		// having a list is success 
	    final String pv_name = "SCL_Diag:IOC_BLM1:Load";
        final PVInfo pvs [] = newPVField.getPVInfo(pv_name,null);
		assertTrue(pvs.length > 0);
        assertEquals(pv_name, pvs[0].getPVName());
		for (int i=0; i<pvs.length; ++i)
        {
		    final String field = pvs[i].getFieldName();
		    assertEquals(pv_name+"."+field, pvs[i].getName());
			System.out.println(pvs[i].getName());
		}
	}
    
    @Test
	public void testGetFieldsUsingPVAndField() throws Exception
	{
	    final PVFieldsAPI newPVField = new SNSPVFieldsData();

		// pass PV to getPVs(String,String)
		// and have it return PV[]
		// having a list is success 
	    final String pv_name = "CCL_LLRF:FCM3:ADC_1";
	    final String field = "HIGH";
        final PVInfo pvs [] = newPVField.getPVInfo(pv_name,field);
		assertTrue(pvs.length == 1);
        assertEquals(pv_name, pvs[0].getPVName());
        assertEquals(field, pvs[0].getFieldName());
        assertEquals(pv_name+"."+field, pvs[0].getName());
        
        System.out.println("There should only be One Row.  Number of Rows Returned = " + pvs.length);
        System.out.println(pvs[0].getName());
	}    

    @Test
	public void testGetFieldsUsingWildCardPVAndField() throws Exception
	{
	    final PVFieldsAPI newPVField = new SNSPVFieldsData();

		// pass PV to getPVs(String,String)
		// and have it return PV[]
		// having a list is success 
	    final String pv_name = "CCL_LLRF:FCM%:ADC_1";
	    final String field = "HIGH";
        final PVInfo pvs [] = newPVField.getPVInfo(pv_name,field);
		assertTrue(pvs.length > 1);
        assertEquals(field, pvs[0].getFieldName());
		
        System.out.println("There should be more than One Row.  Number of Rows Returned = " + pvs.length);
		for (int i=0; i<pvs.length; ++i)
        {
			assertEquals(field, pvs[i].getFieldName());
			assertEquals(pvs[i].getPVName()+"."+field, pvs[i].getName());
			System.out.println(pvs[i].getName());
		}

	}    

    @Test
	public void testGetFieldsUsingPVAndWildCardField() throws Exception
	{
	    final PVFieldsAPI newPVField = new SNSPVFieldsData();

		// pass PV to getPVs(String,String)
		// and have it return PV[]
		// having a list is success 
	    final String pv_name = "CCL_LLRF:FCM%:ADC_1";
	    final String field = "H%";
        final PVInfo pvs [] = newPVField.getPVInfo(pv_name,field);
		
        assertTrue(pvs.length > 1);
		
        System.out.println("There should be more than One Row.  Number of Rows Returned = " + pvs.length);
		for (int i=0; i<pvs.length; ++i)
        {
			assertEquals(pvs[i].getPVName()+"."+pvs[i].getFieldName(), pvs[i].getName());
			System.out.println("PV and either field of High or HSV: " +pvs[i].getName());
		}

	}    

}
