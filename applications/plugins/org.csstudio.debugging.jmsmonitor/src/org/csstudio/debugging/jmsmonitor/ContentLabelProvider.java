/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.debugging.jmsmonitor;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/** Cell label provide that puts message content into cell.
 *  @author Kay Kasemir
 */
public class ContentLabelProvider extends CellLabelProvider
{
    @Override
    public void update(ViewerCell cell)
    {
        // ReceivedMessageProvider should always provide "ReceivedMessage" elements
        final ReceivedMessage msg = (ReceivedMessage) cell.getElement();
        cell.setText(msg.getContent());
    }
}
