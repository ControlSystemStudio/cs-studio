/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.app.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/** Command handler to perform a CSS restart
 *  @author Kay Kasemir
 */
public class Restart extends AbstractHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();

        // In the future, use this?
        //final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        //workbench = window.getWorkbench();

        workbench.restart();
        return null;
    }
}
