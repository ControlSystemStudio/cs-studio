/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitCommand;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.eclipse.core.runtime.IAdapterFactory;


/** Factory for adapters from {@link ScanCommand}
 *  to {@link ProcessVariable} for CSS context menu
 *
 *  <p>Registered in plugin.xml.
 *
 *  @author Kay Kasemir
 */
public class ScanCommandPVAdapterFactory implements IAdapterFactory
{
    final private static Class<?>[] targets = new Class<?>[]
    {
        ProcessVariable.class
    };

    /** {@inheritDoc} */
    @Override
    public Class<?>[] getAdapterList()
    {
        return targets;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType)
    {
        if (! (adaptableObject instanceof ScanCommand)  ||
                adapterType != ProcessVariable.class)
             return null;

        if (adaptableObject instanceof LoopCommand)
        {
            final LoopCommand command = (LoopCommand) adaptableObject;
            return getPV(command.getDeviceName());
        }
        if (adaptableObject instanceof SetCommand)
        {
            final SetCommand command = (SetCommand) adaptableObject;
            return getPV(command.getDeviceName());
        }
        if (adaptableObject instanceof WaitCommand)
        {
            final WaitCommand command = (WaitCommand) adaptableObject;
            return getPV(command.getDeviceName());
        }
        if (adaptableObject instanceof LogCommand)
        {
            final LogCommand command = (LogCommand) adaptableObject;
            final String[] names = command.getDeviceNames();
            if (names.length > 0)
                return getPV(names[0]);
        }

        return null;
    }

    /** @param alias Device alias
     *  @return Underlying PV or <code>null</code>
     */
    private ProcessVariable getPV(final String alias)
    {
        // Try to get device info from editor
        final ScanEditor editor = ScanEditor.getActiveEditor();
        if (editor == null)
            return null;
        final DeviceInfo[] devices = editor.getDevices();
        if (devices == null)
            return null;
        // Try to get PV for alias
        for (DeviceInfo device : devices)
            if (device.getAlias().equals(alias))
                return new ProcessVariable(device.getName());
        return null;
    }
}
