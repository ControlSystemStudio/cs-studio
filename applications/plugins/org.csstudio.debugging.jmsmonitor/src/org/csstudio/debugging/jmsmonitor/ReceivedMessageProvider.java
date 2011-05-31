/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.debugging.jmsmonitor;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** Provider that links model (received messages) to table viewer.
 *  @author Kay Kasemir
 */
public class ReceivedMessageProvider implements IStructuredContentProvider
{
    private ReceivedMessage[] messages = null;

    /** Remember the new input.
     *  Should be the result of call to TableViewer.setInput(ReceivedMessage[])
     *  in GUI.
     */
    @Override
    public void inputChanged(Viewer viewer, Object old_input, Object new_input)
    {
        messages = (ReceivedMessage[]) new_input;
    }

    /** {@inheritDoc} */
    @Override
    public Object[] getElements(Object inputElement)
    {
        return messages;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        // Nothing to dispose
    }
}
