/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/** Eclipse Editor for the Scan Tree
 *  
 *  <p>Displays the scan tree and uses
 *  it as selection provider.
 *  {@link ScanCommandAdapterFactory} then adapts
 *  as necessary to support Properties view/editor.
 *  
 *  @author Kay Kasemir
 */
public class Editor extends EditorPart
{
    private GUI gui;

    public Editor()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
        setSite(site);
        setInput(input);
    }

    @Override
    public void createPartControl(final Composite parent)
    {
        gui = new GUI(parent);
        
        // TODO Show real scan, not dummy
        gui.setCommands(DemoScan.createCommands());
        
        getSite().setSelectionProvider(gui.getSelectionProvider());
    }

    @Override
    public void setFocus()
    {
        gui.setFocus();
    }

    @Override
    public boolean isSaveAsAllowed()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void doSave(final IProgressMonitor monitor)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void doSaveAs()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isDirty()
    {
        // TODO Auto-generated method stub
        return false;
    }
}
