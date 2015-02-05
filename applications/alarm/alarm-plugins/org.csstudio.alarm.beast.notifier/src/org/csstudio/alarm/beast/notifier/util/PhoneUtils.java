/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtils {
	
	final public static Pattern PhonePattern = Pattern.compile("(\\+?[0-9\\(\\)\\/\\-\\.\\ ]+)");
	
	public static List<String> parse(String data) throws Exception {
		List<String> phoneNumbers = new ArrayList<String>();
		Matcher m = PhonePattern.matcher(data);
		while (m.find()) phoneNumbers.add(m.group(1));
		return phoneNumbers;
	}

	public static String format(String number) {
		StringBuilder cleanedNumber = new StringBuilder();
		for (int index = 0; index < number.length(); index++) {
			char c = number.charAt(index);
			if (c == '+' || c == '0' || c == '1' || c == '2' || c == '3'
					|| c == '4' || c == '5' || c == '6' || c == '7' || c == '8'
					|| c == '9')
				cleanedNumber.append(c);
		}
		return cleanedNumber.toString();
	}
}
