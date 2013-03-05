/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/** Terrible Hack to avoid extra context menu entries
 *
 *  <p>Eclipse Editors are often associated with a file,
 *  specifically <code>IFile</code> which is an {@link IResource}.
 *  Depending on what else is included in the overall
 *  product, the context menu of such editors will then receive
 *  certain menu entries that don't make sense to the end user:
 *  <ul>
 *  <li>Team support adds "Team", "Compare With", "Replace With"
 *      even though your editor may not really participare
 *      in team and local history.
 *  <li>org.eclipse.debug.ui adds "Run As", "Debug As"
 *      even though your editor may not represent anything
 *      "runnable"
 *  <li>PyDev adds more python-refactoring code even
 *      though your editor has nothing to do with python
 *  </ul>
 *
 *  <p>When inspecting for example org.eclipse.debug.ui/plugin.xml
 *  of Eclipse 3.7.2, it turned out to contribute the "Run As"
 *  context menu for any editor input that adapts to IResource.
 *
 *  <p>To avoid such context menu entries, we wrap the actual
 *  editor input into something that does <u>not</u> adapt
 *  to a plain {@link IResource}, but otherwise forwards
 *  to the original {@link IEditorInput}.
 *  When the editor wraps its input (received in <code>init()</code>
 *  and maybe set in <code>saveAs()</code>
 *  the nonapplicable context menu entries can be avoided
 *  - at least with Eclipse 3.7.2 for team, debug.ui and PyDev;
 *  it is a hack after all.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class NoResourceEditorInput implements IEditorInput
{
    final private IEditorInput orig;

    /** Initialize
     *  @param orig Original editor input
     */
    public NoResourceEditorInput(final IEditorInput orig)
    {
        this.orig = orig;
    }
    
    /** @return Original editor input, i.e. the one that's wrapped */
    public IEditorInput getOriginEditorInput()
    {
        return orig;
    }

    /** Do NOT adapt to IResource */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter)
    {
        // Compare name as string to compile with RAP,
        // where the RCP IResource class is not available
        if ("org.eclipse.core.resources.IResource".equals(adapter.getName()))
            return null;
        return orig.getAdapter(adapter);
    }

    // Rest just forwards to original implementation...
    @Override
    public boolean exists()
    {
        return orig.exists();
    }

    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return orig.getImageDescriptor();
    }

    @Override
    public String getName()
    {
        return orig.getName();
    }

    @Override
    public IPersistableElement getPersistable()
    {
        return orig.getPersistable();
    }

    @Override
    public String getToolTipText()
    {
        return orig.getToolTipText();
    }
}
