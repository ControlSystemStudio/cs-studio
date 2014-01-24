/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Umit test for {@link PhoneUtils}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 * 
 */
public class PhoneUtilsUnitTest {

	@Test
	public void testFormat() {
		List<String> phoneNumbers = Arrays.asList("+33 4 42 17 64 21",
				"+33-6.03.74.36-61", "+33 4 42 17 61 08");
		StringBuilder sb = new StringBuilder();
		sb.append("[sms:");
		for (int index = 0; index < phoneNumbers.size(); index++) {
			String number = phoneNumbers.get(index);
			sb.append(PhoneUtils.format(number));
			if (index < phoneNumbers.size() - 1)
				sb.append(",");
		}
		sb.append("]");
		Assert.assertEquals("[sms:+33442176421,+33603743661,+33442176108]",
				sb.toString());
	}
}
