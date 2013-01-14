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
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.email.ui.AbstractSendEMailAction;
import org.eclipse.swt.widgets.Shell;

/** Action to send information about selected alarms via EMail
 *  @author Kay Kasemir
 */
public class SendEMailAction extends AbstractSendEMailAction
{
	final private List<AlarmTreeLeaf> alarms;
	
	/** Initialize
	 *  @param shell
	 *  @param alarms
	 */
	public SendEMailAction(final Shell shell, final List<AlarmTreeLeaf> alarms)
    {
		super(shell, Messages.DefaultEMailSender, Messages.DefaultEMailTitle);
		this.alarms = alarms;
    }

	/** Pre-populate body with alarm info
	 *  {@inheritDoc}
	 */
	@Override
    public String getBody()
    {
		return Messages.DefaultEMailBodyStart + AlarmTextHelper.createAlarmInfoText(alarms);
    }

	/** No image
	 *  {@inheritDoc}
	 */
	@Override
    protected String getImage()
    {
		return null;
    }
}
