/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import org.csstudio.alarm.beast.msghist.Messages;
import org.csstudio.alarm.beast.msghist.model.Message;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;

/** CellLabelProvider that fills cells with sequence number of Message.
 *  @author Kay Kasemir
 */
public class SeqProvider extends CellLabelProvider
{
    /** Show "Sequence: ..." as tool-tip */
    @Override
	public String getToolTipText(final Object element)
    {
        final Message message = (Message) element;
        return NLS.bind(Messages.SeqProvider_TTFmt, message.getSequence());
	}

    @Override
    public void update(ViewerCell cell)
    {
        final Message message = (Message) cell.getElement();
        cell.setText(Integer.toString(message.getSequence()));
    }
}
