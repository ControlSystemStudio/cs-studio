/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.csstudio.scan.command.ScanCommandProperty;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.UnknownScanCommandPropertyException;
import org.junit.Test;

/** JUnit test of the ScanCommand property handling
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanCommandPropertyUnitTest
{
    @Test
    public void testCommands() throws Exception
    {
        final SetCommand command = new SetCommand("setpoint", 3.14);

        final ScanCommandProperty[] properties2 = command.getProperties();
        for (ScanCommandProperty p : properties2)
            System.out.println(p);
        assertEquals(8, properties2.length);
        assertEquals("device_name", properties2[0].getID());

        command.setProperty("device_name", "my_device");
        assertEquals("my_device", command.getDeviceName());
        command.setProperty("value", 5.0);
        assertEquals(5.0, (Double) command.getValue(), 0.1);

        final Object value = command.getProperty("value");
        assertEquals(Double.class, value.getClass());
        assertEquals(5.0, (Double) value, 0.1);

        try
        {
            command.getProperty("invalid_property");
            fail("allowed invalid property name");
        }
        catch (UnknownScanCommandPropertyException ex)
        {
            System.out.println("Caught " + ex.getMessage());
        }

        try
        {
            command.setProperty("device_name", Double.NaN);
            fail("allowed invalid property value");
        }
        catch (UnknownScanCommandPropertyException ex)
        {
            System.out.println("Caught " + ex.getMessage());
        }
    }
}
