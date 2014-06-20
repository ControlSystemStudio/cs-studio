/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.actions;

import java.util.List;

import org.csstudio.alarm.beast.notifier.AAData;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.PVSnapshot;
import org.csstudio.alarm.beast.notifier.model.IActionHandler;
import org.csstudio.alarm.beast.notifier.util.PhoneUtils;
import org.csstudio.alarm.beast.notifier.util.SmsCommandHandler;
import org.csstudio.email.JavaxMailSender;


/**
 * Action for sending SMS via mail.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class SmsActionImpl extends AbstractMailActionImpl {

	/** {@inheritDoc} */
	@Override
	public void init(ItemInfo item, AAData auto_action, IActionHandler handler)
			throws Exception {
		this.item = item;
		this.manuallyExecuted = auto_action.isManual();
		mailSender = new JavaxMailSender();
		SmsCommandHandler smsCmdHandler = (SmsCommandHandler) handler;
		List<String> phoneNumbers = smsCmdHandler.getTo();
		StringBuilder sb = new StringBuilder();
		sb.append("[sms:");
		for (int index = 0; index < phoneNumbers.size(); index++) {
			String number = phoneNumbers.get(index);
			sb.append(PhoneUtils.format(number));
			if (index < phoneNumbers.size() - 1)
				sb.append(",");
		}
		sb.append("]");
		phoneNumbers.clear();
		phoneNumbers.add(sb.toString());
		mailSender.setTo(phoneNumbers);
		mailSender.setBody(smsCmdHandler.getBody());
	}

	/** {@inheritDoc} */
	@Override
	public void execute(List<PVSnapshot> pvs) throws Exception {
		this.pvs = pvs;
		mailSender.setBody(buildBody());
		if (mailSender.checkContent())
			mailSender.send();
	}
	
	public void dump() {
		System.out.println("SmsActionImpl [\n\tto= " + mailSender.getTo()
				+ "\n\tsubject= " + mailSender.getSubject() + "\n]");
	}

}
