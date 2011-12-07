/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitForValueCommand;
import org.csstudio.scan.ui.scantree.properties.DelayCommandAdapter;
import org.csstudio.scan.ui.scantree.properties.LogCommandAdapter;
import org.csstudio.scan.ui.scantree.properties.LoopCommandAdapter;
import org.csstudio.scan.ui.scantree.properties.SetCommandAdapter;
import org.csstudio.scan.ui.scantree.properties.WaitForValueCommandAdapter;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

/** Factory for adapters from {@link ScanCommand}
 *  to the {@link IPropertySource}
 *  required by the Properties View.
 *  
 *  <p>Registered in plugin.xml.
 * 
 *  @author Kay Kasemir
 */
public class ScanCommandAdapterFactory implements IAdapterFactory
{
    final private static Class<?>[] targets = new Class<?>[]
    {
        IPropertySource.class
    };
    
    private static GUI gui = null;
    
    @Override
    public Class<?>[] getAdapterList()
    {
        return targets;
    }
    
    /** @param gui Current GUI to which commands are bound */
    public static void setGUI(final GUI gui)
    {
        ScanCommandAdapterFactory.gui = gui;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType)
    {
        final ScanCommand command = (ScanCommand) adaptableObject;
        if (adapterType == IPropertySource.class)
        {
            if (command instanceof DelayCommand)
                return new DelayCommandAdapter(gui, (DelayCommand) command);
            else if (command instanceof LogCommand)
                return new LogCommandAdapter(gui, (LogCommand) command);
            else if (command instanceof LoopCommand)
                return new LoopCommandAdapter(gui, (LoopCommand) command);
            else if (command instanceof SetCommand)
                return new SetCommandAdapter(gui, (SetCommand) command);
            else if (command instanceof WaitForValueCommand)
                return new WaitForValueCommandAdapter(gui, (WaitForValueCommand) command);
        }
        return null;
    }
}
