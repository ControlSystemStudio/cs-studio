/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.actions;

import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.notifier.AAData;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.PVSnapshot;
import org.csstudio.alarm.beast.notifier.model.IActionHandler;
import org.csstudio.alarm.beast.notifier.util.EMailCommandHandler;
import org.csstudio.email.JavaxMailSender;

/**
 * Action for sending EMails.
 * If the parsed command is complete, the email does not need to be filled.
 * Otherwise, subject and body are built from {@link AlarmTreeItem} description.
 * @author Fred Arnaud (Sopra Group)
 *
 */
@SuppressWarnings("nls")
public class EmailActionImpl extends AbstractMailActionImpl {

	/** {@inheritDoc} */
	@Override
	public void init(ItemInfo item, AAData auto_action, IActionHandler handler)
			throws Exception {
		this.item = item;
		this.manuallyExecuted = auto_action.isManual();
		mailSender = new JavaxMailSender();
		EMailCommandHandler emailCmdHandler = (EMailCommandHandler) handler;
		mailSender.setTo(emailCmdHandler.getTo());
		mailSender.setCc(emailCmdHandler.getCc());
		mailSender.setCci(emailCmdHandler.getCci());
		mailSender.setSubject(emailCmdHandler.getSubject());
		mailSender.setBody(emailCmdHandler.getBody());
	}
	
	/** {@inheritDoc} */
	@Override
	public void execute(List<PVSnapshot> pvs) throws Exception {
		this.pvs = pvs;
		mailSender.setSubject(buildSubject());
		mailSender.setBody(buildBody());
		if (mailSender.checkContent())
			mailSender.send();
	}

	public void dump() {
		System.out.println("EmailActionImpl [\n\tto= " + mailSender.getTo()
				+ "\n\tcc= " + mailSender.getCc()
				+ "\n\tcci= " + mailSender.getCci()
				+ "\n\tsubject= " + mailSender.getSubject()
				+ "\n\tbody= " + mailSender.getBody()
				+ "\n]");
	}

}
