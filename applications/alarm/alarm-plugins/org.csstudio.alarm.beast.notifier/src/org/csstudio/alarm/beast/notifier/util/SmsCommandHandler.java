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
import java.util.StringTokenizer;
import java.util.regex.Matcher;

import org.csstudio.alarm.beast.client.AADataStructure;

/**
 * Handler for SMS command:
 * Parse {@link AADataStructure} details to extract information.
 * Exemple: 
 *     sms:+33 6 03 74 36 61?body={0} Alarm raised - Water below {1} m3
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class SmsCommandHandler extends AbstractCommandHandler {

	private List<String> to;
	private String body;
	
	public SmsCommandHandler(String details) {
		super(details, "sms");
	}
	
	protected void handleParameter(String data, ParamType type) throws Exception {
		if (type == null || data == null) return;
		switch (type) {
		case To:
			to = extractSMSNumbers(data.toLowerCase());
			break;
		case Body:
			body = data.trim();
			validateNSF(body);
			break;
		default:
			break;
		}
	}
	
	private List<String> extractSMSNumbers(String data) throws Exception {
		StringTokenizer st = new StringTokenizer(data, DELIMITERS);
		List<String> smsList = new ArrayList<String>();
		while (st.hasMoreElements()) {
			String token = st.nextToken().trim();
			Matcher smsMatcher = PhoneUtils.PhonePattern.matcher(token);
			if (smsMatcher.matches())
				smsList.add(token);
			else throw new Exception("Invalid phone number: " + token);
		}
		return smsList;
	}
	
	public List<String> getTo() {
		return to;
	}
	public String getBody() {
		if (body == null) return "";
		return body;
	}

	@Override
	public String toString() {
		return "SmsCommandHandler [to=" + to + ", body=" + body + "]";
	}
}
