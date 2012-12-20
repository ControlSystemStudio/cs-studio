/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;

/** Action to send information about selected alarms to clipboard
 *  @author Kay Kasemir
 */
public class CopyToClipboardAction extends Action
{
	final private List<AlarmTreeLeaf> alarms;

	public CopyToClipboardAction(final List<AlarmTreeLeaf> alarms)
    {
		super(Messages.CopyToClipboard,
				Activator.getImageDescriptor("icons/clipboard.gif")); //$NON-NLS-1$
		this.alarms = alarms;
    }

	@Override
    public void run()
    {
		final String alarm_info = AlarmTextHelper.createAlarmInfoText(alarms);
        // Copy as text to clipboard
        final Clipboard clipboard = new Clipboard(
            PlatformUI.getWorkbench().getDisplay());
        clipboard.setContents(new String[] { alarm_info },
            new Transfer[] { TextTransfer.getInstance() });
    }
}
