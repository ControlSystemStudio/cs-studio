/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitCommand;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.csstudio.scan.ui.scantree.gui.ScanEditorContributor;
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
        ProcessVariable[].class,
        ProcessVariable.class,
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
        if (! (adaptableObject instanceof ScanCommand))
             return null;

        ProcessVariable[] pvs = null;
        if (adaptableObject instanceof LoopCommand)
        {
            final LoopCommand command = (LoopCommand) adaptableObject;
            pvs = getPV(command.getDeviceName());
        }
        else if (adaptableObject instanceof SetCommand)
        {
            final SetCommand command = (SetCommand) adaptableObject;
            pvs = getPV(command.getDeviceName());
        }
        else if (adaptableObject instanceof WaitCommand)
        {
            final WaitCommand command = (WaitCommand) adaptableObject;
            pvs = getPV(command.getDeviceName());
        }
        else if (adaptableObject instanceof LogCommand)
        {
            final LogCommand command = (LogCommand) adaptableObject;
            final String[] names = command.getDeviceNames();
            pvs = getPV(names);
        }
        if (pvs == null)
            return null;

        if (adapterType == ProcessVariable[].class)
            return pvs;
        if (adapterType == ProcessVariable.class)
            return pvs[0];
        return null;
    }

    /** @param aliases Device aliases
     *  @return Underlying PVs or <code>null</code>
     */
    private ProcessVariable[] getPV(final String... aliases)
    {
        // Try to get device info from editor
        final ScanEditor editor = ScanEditorContributor.getCurrentScanEditor();
        if (editor == null)
            return null;
        final DeviceInfo[] devices = editor.getDevices();
        if (devices == null)
            return null;

        final List<ProcessVariable> pvs = new ArrayList<ProcessVariable>();
        // Try to get PVs for aliass
        for (String alias : aliases)
            for (DeviceInfo device : devices)
                if (device.getAlias().equals(alias))
                    pvs.add(new ProcessVariable(device.getName()));

        if (pvs.size() <= 0)
            return null;
        return pvs.toArray(new ProcessVariable[pvs.size()]);
    }
}
