/*******************************************************************************
* Copyright (c) 2010-2016 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.actions;

import java.util.List;
import java.util.ListIterator;

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
@SuppressWarnings("nls")
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
        ListIterator < String > list = phoneNumbers.listIterator();
        while (list.hasNext()) {
            list.set(PhoneUtils.format(list.next()));
        }
        mailSender.setTo(phoneNumbers);
        mailSender.setSubject("<NONE>");
        mailSender.setBody(smsCmdHandler.getBody());
    }

    /** {@inheritDoc} */
    @Override
    public void execute(List<PVSnapshot> pvs) throws Exception {
        this.pvs = pvs;
        mailSender.setBody(buildBody());
        mailSender.send();
    }

    public void dump() {
        System.out.println("SmsActionImpl [\n\tto= " + mailSender.getTo()
                + "\n\tsubject= " + mailSender.getSubject() + "\n]");
    }

}
