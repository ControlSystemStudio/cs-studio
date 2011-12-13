/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.eclipse.ui.views.properties.IPropertySource;

/** Base for Adapters from {@link ScanCommand} to {@link IPropertySource}
 *  to allow display and editing of commands in Properties View.
 *  @author Kay Kasemir
 */
abstract public class ScanCommandAdapter<C extends ScanCommand> implements IPropertySource
{
    final private ScanEditor editor;
    private C command;
    
    /** Initialize
     *  @param scan_editor GUI that displays the command
     *  @param command {@link ScanCommand}
     */
    public ScanCommandAdapter(final ScanEditor editor, final C command)
    {
        this.editor = editor;
        this.command = command;
    }
    
    /** @return {@link ScanCommand} */
    protected C getCommand()
    {
        return command;
    }

    /** @param command Command that changed, requiring a GUI refresh */
    protected void refreshCommand(final C command)
    {
        if (editor != null)
            editor.refreshCommand(command);
    }
    
    /** {@inheritDoc} */
    @Override
    public Object getEditableValue()
    {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPropertySet(final Object id)
    {
        return getPropertyValue(id) != null;
    }

    /** {@inheritDoc} */
    @Override
    public void resetPropertyValue(Object id)
    {
        // NOP
    }
}
