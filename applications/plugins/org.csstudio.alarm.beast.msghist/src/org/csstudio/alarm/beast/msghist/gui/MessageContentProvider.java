/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import org.csstudio.alarm.beast.msghist.model.Model;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** Content provider for TableViewer that uses Model as input and 
 *  hands out messages.
 *  
 *  @author Kay Kasemir
 */
public class MessageContentProvider implements IStructuredContentProvider
{
    private Model model;

    /** Remember the new input.
     *  Should be the result of call to TableViewer.setInput(Model) in GUI.
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        model = (Model) newInput;
    }

    public Object[] getElements(Object inputElement)
    {
        return model.getMessages();
    }

    /** {@inheritDoc} */
    public void dispose()
    {
        // Nothing to dispose
    }
}
