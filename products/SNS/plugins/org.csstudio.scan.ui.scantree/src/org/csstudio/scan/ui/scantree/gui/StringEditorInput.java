/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.gui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/** Editor input that allows {@link String} to be displayed
 *  in read-only text editor
 *
 *  <p>Based in info from
 *  http://wiki.eclipse.org/FAQ_How_do_I_open_an_editor_on_something_that_is_not_a_file%3F
 *
 *  @author Kay Kasemir
 */
public class StringEditorInput implements IStorage, IStorageEditorInput
{
	final private String title;
	final private String text;

	public StringEditorInput(final String title, final String text)
	{
		this.title = title;
		this.text = text;
	}

	// IStorage...
	@Override
	public InputStream getContents() throws CoreException
	{
		return new ByteArrayInputStream(text.getBytes());
	}

	@Override
	public IPath getFullPath()
	{
		return null;
	}

	@Override
	public boolean isReadOnly()
	{
		return true;
	}

	// IStorageEditorInput
	@Override
    public boolean exists()
    {
	    return true;
    }

	@Override
    public ImageDescriptor getImageDescriptor()
    {
	    return null;
    }

	@Override
    public String getName()
    {
	    return title;
    }

	@Override
    public IPersistableElement getPersistable()
    {
	    return null;
    }

	@Override
    public String getToolTipText()
    {
	    return title;
    }

	@SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Class adapter)
    {
	    return null;
    }

	@Override
    public IStorage getStorage() throws CoreException
    {
	    return this;
    }
}
