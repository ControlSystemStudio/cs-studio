/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.csstudio.scan.ui.scantree.gui.ScanEditorContributor;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

/** Factory for adapters from {@link ScanCommand}
 *  to the {@link IPropertySource} required by the Properties View.
 *
 *  <p>Registered in plugin.xml.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanCommandPropertyAdapterFactory implements IAdapterFactory
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

        // Locate the currently active editor, the one that
        // needs to be updated when the command changes
        final ScanEditor scan_editor = ScanEditorContributor.getCurrentScanEditor();
        if (scan_editor == null)
            return null;

        // Create the adapter
        try
        {
            return new GenericCommandAdapter(scan_editor, command);
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName())
                .log(Level.WARNING,
                     "Cannot edit properties of " + command.getClass().getName(), ex);
            return null;
        }
    }
}
