/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitCommand;
import org.csstudio.scan.ui.scantree.properties.DelayCommandAdapter;
import org.csstudio.scan.ui.scantree.properties.LogCommandAdapter;
import org.csstudio.scan.ui.scantree.properties.LoopCommandAdapter;
import org.csstudio.scan.ui.scantree.properties.SetCommandAdapter;
import org.csstudio.scan.ui.scantree.properties.WaitCommandAdapter;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertySource;

/** Factory for adapters from {@link ScanCommand}
 *  to the {@link IPropertySource}
 *  required by the Properties View.
 *  
 *  <p>Registered in plugin.xml.
 * 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanCommandAdapterFactory implements IAdapterFactory
{
    final private static Class<?>[] targets = new Class<?>[]
    {
        IPropertySource.class
    };
    
    /** {@inheritDoc} */
    @Override
    public Class<?>[] getAdapterList()
    {
        return targets;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType)
    {
        if (! (adaptableObject instanceof ScanCommand)  ||
               adapterType != IPropertySource.class)
            return null;
        final ScanCommand command = (ScanCommand) adaptableObject;
        
        // Locate the currently active editor, the one that needs to be updated when the command changes
        final IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (! (editor instanceof ScanEditor))
            return null;
        final ScanEditor scan_editor = (ScanEditor) editor;

        // Create the appropriate adapter
        if (command instanceof DelayCommand)
            return new DelayCommandAdapter(scan_editor, (DelayCommand) command);
        else if (command instanceof LogCommand)
            return new LogCommandAdapter(scan_editor, (LogCommand) command);
        else if (command instanceof LoopCommand)
            return new LoopCommandAdapter(scan_editor, (LoopCommand) command);
        else if (command instanceof SetCommand)
            return new SetCommandAdapter(scan_editor, (SetCommand) command);
        else if (command instanceof WaitCommand)
            return new WaitCommandAdapter(scan_editor, (WaitCommand) command);
        
        Logger.getLogger(getClass().getName()).log(Level.WARNING, "No adapter for {0}", command.getClass().getName());
        return null;
    }
}
