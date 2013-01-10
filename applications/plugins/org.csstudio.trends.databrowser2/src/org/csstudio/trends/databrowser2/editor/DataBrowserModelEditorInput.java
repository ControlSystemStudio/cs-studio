/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.editor;

import org.csstudio.trends.databrowser2.model.Model;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/** Editor input for Data Browser with reference to {@link Model}
 *
 *  <p>When the data browser editor is opened,
 *  it will either receive an empty input or a file-based input.
 *
 *  <p>It then creates a {@link Model} for that input and runs the model with
 *  a plot and controller.
 *
 *  <p>When moving such a data browser editor into a new Workbench Window,
 *  Eclipse will basically close the old editor, then open
 *  a new editor in the target window with the same input.
 *
 *  <p>If the input is just the original file-based input,
 *  the new data browser editor will open that same file,
 *  show the same channels etc., but start from scratch as
 *  far as model data is concerned.
 *
 *  <p>By using this input that also holds a reference to the model,
 *  the sample buffers that have already accumulated live data in the
 *  old data browser instance will be preserved.
 *  The new' data browser editor in the target window
 *  receives the existing model.
 *
 *  <p>So while under the hood it's still a new data browser editor instance,
 *  the behavior seen by the end user is somewhat smooth.
 *  It works best, however, if the data browser model was saved to a file.
 *  When saved to a file, a file-based input with model is transferred.
 *  When not saved to a file, Eclipse will prompt for saving to a file
 *  because after all the old data browser editor is closed,
 *  but at the same time it already remembers the old 'empty'
 *  data browser input for the new window.
 *  To the user, this means: Prompt for save, then see data browser
 *  in new window that has the old data model (good) but also still
 *  shows "not saved to file" as title (confusing).
 *
 *  @author Kay Kasemir
 */
public class DataBrowserModelEditorInput implements IEditorInput, IPersistableElement
{
	final private IEditorInput input;
	final private Model model;

	public DataBrowserModelEditorInput(final IEditorInput input, final Model model)
	{
		this.input = input;
		this.model = model;
	}

	public Model getModel()
	{
		return model;
	}

	@Override
    public boolean exists()
    {
	    return input.exists();
    }

	@Override
    public ImageDescriptor getImageDescriptor()
    {
	    return input.getImageDescriptor();
    }

	@Override
    public String getName()
    {
	    return input.getName();
    }

	@Override
    public IPersistableElement getPersistable()
    {
	    return input.getPersistable();
    }

	@Override
    public String getToolTipText()
    {
	    return input.getToolTipText();
    }

	@SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Class adapter)
    {
	    return input.getAdapter(adapter);
    }

	@Override
    public void saveState(final IMemento memento)
    {
		if (input instanceof IPersistableElement)
			((IPersistableElement)input).saveState(memento);
    }

	@Override
    public String getFactoryId()
    {
		if (input instanceof IPersistableElement)
			return ((IPersistableElement)input).getFactoryId();
		return null;
    }
}
