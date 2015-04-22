/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import org.csstudio.scan.ui.scantree.ScanEditor;
import org.csstudio.scan.ui.scantree.gui.Perspective;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/** Handler connected to workbench menu or toolbar for opening a new editor.
 *  @author Kay Kasemir
 */
public class NewScanTree extends AbstractHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        ScanEditor.createInstance();
        
        try
        {
            Perspective.showPerspective();
        }
        catch (Exception ex)
        {
            // never mind
        }
        return null;
    }
}
