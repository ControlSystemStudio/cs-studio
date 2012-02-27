/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/** Terrible Hack to avoid extra context menu entries
 *
 *  <p>Editors associated with "IResource" will receive
 *  certain context menu entries that don't make sense to
 *  the end user:
 *  <ul>
 *  <li>team support adds "Team", "Compare With", "Replace With"
 *  <li>org.eclipse.debug.ui adds "Run As", "Debug As"
 *  <li>PyDev adds more
 *  </ul>
 *
 *  <p>These context menu items appear eve though our file
 *  doesn't really integrate with these (,yet).
 *
 *  <p>To avoid the context menu entry, we wrap the actual
 *  editor input into someting that does <u>not</u> adapt
 *  to a plain {@link IResource}
 *
 *  @author Kay Kasemir
 */
public class LessAdaptableEditorInput implements IEditorInput
{
    final private IEditorInput orig;

    /** Initialize
     *  @param orig Original editor input
     */
    public LessAdaptableEditorInput(final IEditorInput orig)
    {
        this.orig = orig;
    }

    /** Do NOT adapt to IResource */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter)
    {
        if (adapter == IResource.class)
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
