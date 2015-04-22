/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.device.DeviceContextHelper;
import org.csstudio.scan.server.JythonSupport;
import org.csstudio.scan.server.MacroContext;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanCommandImplTool;
import org.junit.Test;

/** [Headless] JUnit Plug-in Test of the {@link DeviceContextHelper}
 *  @author Kay Kasemir
 */
public class DeviceContextHelperHeadlessTest
{
    @Test
    public void testDeviceContextHelper() throws Exception
    {
        final MacroContext macros = new MacroContext("T=Test");
        
        final List<ScanCommand> commands = Arrays.asList(
            (ScanCommand) new SetCommand("device1", 3.14),
            (ScanCommand) new SetCommand("xpos", 2),
            (ScanCommand) new SetCommand("motor_x", 3)
            );
        final JythonSupport jython = new JythonSupport();
        final List<ScanCommandImpl<?>> main_scan =
                ScanCommandImplTool.getInstance().implement(commands, jython);
        final DeviceContext device_context = new DeviceContext();
        DeviceContextHelper.addScanDevices(device_context, macros, main_scan);
        
        final Device[] devices = device_context.getDevices();
        System.out.println(Arrays.toString(devices));
        
        // Devices from commands, but account for motor_x = alias xpos
        assertThat(devices.length, equalTo(commands.size() - 1));
        
        // Check lookup via alias
        final Device aliased_device = device_context.getDevice("xpos");
        assertThat(aliased_device.getName(), equalTo("motor_x"));

        // Check lookup via 'real' name
        final Device real_device = device_context.getDevice("motor_x");
        assertThat(real_device, sameInstance(aliased_device));
    }
}
