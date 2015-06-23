/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import org.csstudio.alarm.beast.msghist.model.Message;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/** ViewerComparator for sorting Messages by ID.
 *  @author Kay Kasemir
 */
public class MessageIDComparator extends ViewerComparator
{
    final private boolean up;

    public MessageIDComparator(final boolean up)
    {
        this.up = up;
    }

    /** {@inhericDoc} */
    @Override
    public int compare(Viewer viewer, Object e1, Object e2)
    {
        final Message msg1 = (Message) e1;
        final Message msg2 = (Message) e2;
        if (up)
            return msg2.getId() - msg1.getId();
        return msg1.getId() - msg2.getId();
    }
}
