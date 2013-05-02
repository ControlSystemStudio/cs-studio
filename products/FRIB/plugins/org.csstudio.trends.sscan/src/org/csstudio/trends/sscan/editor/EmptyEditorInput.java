/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.editor;

import org.csstudio.trends.sscan.Messages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/** Empty IEditorInput.
 *  <p>
 *  When the editor is started without a file,
 *  for example from the main menu,
 *  this is used as its initial input.
 *  <p>
 *  When the user decides to save the configuration
 *  into an actual file, the input is changed to a FileEditorInput.
 *  <p>
 *  @author Kay Kasemir
 */
public class EmptyEditorInput implements IEditorInput
{
    /** Cause application title to reflect the 'not saved' state. */
    @Override
    public String getName()
    {
        return Messages.NotSaved;
    }

    /** Cause tool top to reflect the 'not saved' state. */
    @Override
    public String getToolTipText()
    {
        return Messages.NotSavedTT;
    }

    /** @return Returns <code>false</code> since no file exists. */
    @Override
    public boolean exists()
    {
        return false;
    }

    /** Returns no image. */
    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }

    /** Can't persist. */
    @Override
    public IPersistableElement getPersistable()
    {
        return null;
    }

    /** Don't adapt. */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter)
    {
        return null;
    }
}
