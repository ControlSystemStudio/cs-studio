/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.ui;

import org.csstudio.alarm.beast.annunciator.model.AnnunciationMessage;
import org.csstudio.apputil.ringbuffer.RingBuffer;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** (Lazy) Table content provider for input of type
 *  <code>RingBuffer&lt;AnnunciationMessage&gt;</code>
 * @author Kay Kasemir
 */
public class MessageRingBufferContentProvider implements ILazyContentProvider
{
    private TableViewer viewer;

    /** List of recent Messages.
     *  Synchronize on access.
     */
    private RingBuffer<AnnunciationMessage> messages;

    @Override
    @SuppressWarnings("unchecked")
    public void inputChanged(final Viewer viewer, final Object old, final Object input)
    {
        this.viewer = (TableViewer) viewer;
        messages = (RingBuffer<AnnunciationMessage>) input;
        if (viewer == null)
            return;
        if (messages == null)
            this.viewer.setItemCount(0);
        else
        {
            final int N;
            synchronized (messages)
            {
                N = messages.size();
            }
            this.viewer.setItemCount(N);
        }
    }

    @Override
    public void updateElement(final int index)
    {
        // Show elements in reverse order; index 0 is last message
        final AnnunciationMessage message;
        synchronized (messages)
        {
            final int N = messages.size();
            // Ignore update in case message count changed
            if (index >= N)
            	return;
            message = messages.get(N-1-index);
        }
        viewer.replace(message, index);
    }

    @Override
    public void dispose()
    {
        // NOP
    }
}
