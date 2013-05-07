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
public class StringEditorInput implements IStorageEditorInput
{
	final private IStorage storage;

	/** Storage implementation for String */
	private static class StringStorage implements IStorage
	{
		final private String name;
		final private String text;

		public StringStorage(final String name, final String text)
        {
			this.name = name;
	        this.text = text;
        }

		@SuppressWarnings("rawtypes")
        @Override
        public Object getAdapter(Class adapter)
        {
	        return null;
        }

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
        public String getName()
        {
	        return name;
        }

		@Override
        public boolean isReadOnly()
        {
			return true;
        }
	};

	/** Initialize
	 *  @param title Title of the editor, name of the content
	 *  @param text String content
	 */
	public StringEditorInput(final String title, final String text)
	{
		storage = new StringStorage(title, text);
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
	    return storage.getName();
    }

	@Override
    public IPersistableElement getPersistable()
    {
	    return null;
    }

	@Override
    public String getToolTipText()
    {
	    return getName();
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
	    return storage;
    }
}
