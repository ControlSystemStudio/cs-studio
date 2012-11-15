/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.epics;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.junit.Test;

/** Test access security updates
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EPICS_V3_PV_ACFTest
{
	private Boolean write_allowed = null;
	
    @Test(timeout=15000)
    public void testSinglePVStartStop() throws Exception
    {
        final PV disable = TestUtil.getPV("enum");
        disable.start();
        while (! disable.isConnected())
        	Thread.sleep(100);
        disable.setValue(0);
        
        final PV pv = TestUtil.getPV("fred");
        pv.addListener(new PVListener()
        {
			@Override
			public void pvValueUpdate(final PV pv)
			{
				System.out.println("Update: " + pv.getName() + " = " + pv.getValue());
				synchronized (EPICS_V3_PV_ACFTest.this)
				{
					write_allowed = pv.isWriteAllowed();
					EPICS_V3_PV_ACFTest.this.notifyAll();
				}
			}
			
			@Override
			public void pvDisconnected(final PV pv)
			{
				System.out.println("Disconnected: " + pv.getName());
			}
		});
        pv.start();
        
        synchronized (this)
        {
        	while (write_allowed == null  ||  write_allowed != true)
        		wait(100);
            System.out.println("Write permitted: " + write_allowed);
            assertThat(write_allowed, equalTo(true));
		}

        disable.setValue(1);
        synchronized (this)
        {
        	while (write_allowed == null  ||  write_allowed == true)
        		wait(100);
            System.out.println("Write permitted: " + write_allowed);
            assertThat(write_allowed, equalTo(false));
		}
        
        pv.stop();
        disable.stop();
	}
}
