/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.singlesource;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;

/** Persistable editor input based on path
 *
 *  <p>Uses an {@link IPath}, which is supported by both RCP and RAP,
 *  as the persisted identifier of an editor input.
 *  @author Kay Kasemir
 */
public class PathEditorInput implements IPathEditorInput, IPersistableElement
{
    final private IPath path;

    /** Initialize
     *  @param path {@link IPath} that identifies this input
     */
    public PathEditorInput(final IPath path)
    {
        this.path = path;
    }

    /** @return Path that identifies the editor input.
     *          Maybe workspace file, file system file or URL
     */
    @Override
    public IPath getPath()
    {
        return path;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return path.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj)
    {
        if (! (obj instanceof IPathEditorInput))
            return false;
        final IPath other = ((IPathEditorInput) obj).getPath();
        // Try shortcut if it's the same PathEditorInput and thus path,
        // else compare portable representation
        return other == path  ||
               other.toPortableString().equals(path.toPortableString());
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists()
    {
        return SingleSourcePlugin.getResourceHelper().exists(path);
    }

    /** {@inheritDoc} */
    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return path.lastSegment();
    }

    /** {@inheritDoc} */
    @Override
    public IPersistableElement getPersistable()
    {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public String getToolTipText()
    {
        return path.toOSString();
    }

    /** {@inheritDoc} */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object getAdapter(final Class adapter)
    {
        if (adapter == IPathEditorInput.class) 
          return Platform.getAdapterManager().getAdapter(this, adapter);
      
        return SingleSourcePlugin.getResourceHelper().adapt(path, adapter);
    }

    /** {@inheritDoc} */
    @Override
    public void saveState(final IMemento memento)
    {
        memento.putString(PathEditorInputFactory.TAG_PATH, path.toPortableString());
    }

    /** {@inheritDoc} */
    @Override
    public String getFactoryId()
    {
        return PathEditorInputFactory.ID;
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return path.toString();
    }
}
