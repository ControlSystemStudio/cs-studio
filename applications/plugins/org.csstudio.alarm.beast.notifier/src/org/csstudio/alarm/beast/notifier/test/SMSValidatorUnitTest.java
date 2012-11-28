/*******************************************************************************
* Copyright (c) 2010-2012 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.test;

import java.util.List;

import org.csstudio.alarm.beast.notifier.util.PhoneUtils;
import org.csstudio.alarm.beast.notifier.util.SmsCommandValidator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link SmsCommandValidator}
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class SMSValidatorUnitTest {

	@Test
	public void testValidator() {
		String st1 = "sms: +33 4 42 17 64 21, +33-6.03.74.36-61; +33 4 42 17 61 08";
		SmsCommandValidator cmd = new SmsCommandValidator();
		cmd.init(st1);
		try {
			Assert.assertTrue(cmd.validate());
			System.out.println(cmd.getHandler().toString());
		} catch (Exception e) {
		}
		
		List<String> phoneNumbers = cmd.getHandler().getTo();
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
		System.out.println("Cleaned: " + phoneNumbers);

		String st2 = "sms: +33 6 (03) 74 36 61?body={0} Alarm raised - Water below {1} m3";
		SmsCommandValidator cmd2 = new SmsCommandValidator();
		cmd2.init(st2);
		try {
			Assert.assertTrue(cmd2.validate());
			System.out.println(cmd2.getHandler().toString());
		} catch (Exception e) {
		}
		
		String st3 = "sms: +33 4 42 17 64 21, +33-6.03.74n36-61; +33 4 42 17 61 08";
		SmsCommandValidator cmd3 = new SmsCommandValidator();
		cmd3.init(st3);
		try {
			cmd3.validate();
		} catch (Exception e) {
			Assert.assertTrue(true);
			System.out.println(e.getMessage());
		}
	}
}
