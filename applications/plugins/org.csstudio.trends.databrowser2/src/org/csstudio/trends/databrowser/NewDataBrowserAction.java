/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser;

import org.csstudio.trends.databrowser.editor.DataBrowserEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/** Action connected to workbench menu action set for opening a new editor.
 *  @author Kay Kasemir
 */
public class NewDataBrowserAction implements IWorkbenchWindowActionDelegate
{
    public void init(IWorkbenchWindow window)
    {
        // NOP
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
        // NOP
    }

    /** {@inheritDoc} */
    public void run(IAction action)
    {
        DataBrowserEditor.createInstance();
        try
        {
        	Perspective.showPerspective();
        }
        catch (Exception ex)
        {
        	// never mind
        }
    }

    public void dispose()
    {
        // NOP
    }
}
