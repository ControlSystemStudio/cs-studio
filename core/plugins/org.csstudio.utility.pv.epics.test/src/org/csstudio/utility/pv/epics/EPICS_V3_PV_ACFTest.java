/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.epics;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.logging.Level;

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
    public void testWritePermissionUpdate() throws Exception
    {
    	TestUtil.log_level = Level.WARNING;
    	
    	// Allow write access
        final PV disable = TestUtil.getPV("allow");
        disable.start();
        while (! disable.isConnected())
        	Thread.sleep(100);
        disable.setValue(0);
        
        // Monitor permissions of a test PV that doesn't update except for permission changes
        final PV pv = TestUtil.getPV("check_access");
        pv.addListener(new PVListener()
        {
			@Override
			public void pvValueUpdate(final PV pv)
			{
				synchronized (EPICS_V3_PV_ACFTest.this)
				{
					write_allowed = pv.isWriteAllowed();
					System.out.println("Update: " + pv.getName() + " = " + pv.getValue() + (write_allowed ? " (write)" : " (read-only)"));
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

        // Wait until we see the write access
        synchronized (this)
        {
        	while (write_allowed == null  ||  write_allowed != true)
        		wait(100);
            System.out.println("Write permitted: " + write_allowed);
            assertThat(write_allowed, equalTo(true));
		}

        // Disable write access
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
