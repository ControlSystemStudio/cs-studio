/*******************************************************************************
 * Copyright (c) 2010, 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.perspective;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/** An action that opens or resets a perspective.
 *  @author Kay Kasemir
 *  @since 2014/03/27, moved from org.csstudio.apputil.ui.workbench;
 */
public class OpenPerspectiveAction extends Action
{
	private static final long serialVersionUID = -8811523514428926759L;
	/** ID of the Perspective to open */
    final private String ID;
    
    /** Construct the action for opening a perspective.
     *  @param icon Icon to use for the action.
     *  @param name Name to use for the action.
     *  @param ID The ID of the Perspective to open.
     */
    public OpenPerspectiveAction(final ImageDescriptor icon,
                                 final String name, final String ID)
    {
        super(name);
        setImageDescriptor(icon);
        this.ID = ID;
    }
    
    @Override
    public void run()
    {
        final IWorkbench wb = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
        PerspectiveHelper.showPerspectiveOrPromptForReset(ID, window);
    }
}
