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
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.email.EmailUtils;

/**
 * Handler for EMail command:
 * Parse {@link AADataStructure} details to extract information.
 * Exemple: 
 *     mailto:rf_support@iter.org;rf_operator@iter.org?cc=rf.ro@iter.org&subject=RF Source 1 in error&body=Major Alarm raised
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class EMailCommandHandler extends AbstractCommandHandler {
	
	private List<String> to, cc, cci;
	private String subject, body;
	
	public EMailCommandHandler(String details) {
		super(details, "mailto");
	}
	
	protected void handleParameter(String data, ParamType type) throws Exception {
		if (type == null || data == null) return;
		switch (type) {
		case To:
			to = extractEmailAdresses(data.toLowerCase());
			break;
		case Cc:
			cc = extractEmailAdresses(data.toLowerCase());
			break;
		case Bcc:
		case Cci:
			cci = extractEmailAdresses(data.toLowerCase());
			break;
		case Subject:
			subject = data.trim();
			validateNSF(subject);
			break;
		case Body:
			body = data.trim();
			validateNSF(body);
			break;
		}
	}
	
	private List<String> extractEmailAdresses(String data) throws Exception {
		final Pattern emailPattern = Pattern.compile(EmailUtils.EMAIL_REG_EXP);
		StringTokenizer st = new StringTokenizer(data, DELIMITERS);
		List<String> emailList = new ArrayList<String>();
		while (st.hasMoreElements()) {
			String token = st.nextToken().trim();
			Matcher emailMatcher = emailPattern.matcher(token);
			if (emailMatcher.matches())
				emailList.add(token);
			else throw new Exception("Invalid email address: " + token);
		}
		return emailList;
	}

	/** @return <code>true</code> if the EMail command define a recipient, a subject and a body */
	public boolean isComplete() {
		return ((getTo() != null && !getTo().isEmpty())
				&& (getSubject() != null && !"".equals(getSubject().trim())) 
				&& (getBody() != null && !"".equals(getBody().trim())));
	}
	
	public List<String> getTo() {
		return to;
	}
	public List<String> getCc() {
		return cc;
	}
	public List<String> getCci() {
		return cci;
	}
	public String getSubject() {
		if (subject == null) return "";
		return subject;
	}
	public String getBody() {
		if (body == null) return "";
		return body;
	}

	@Override
	public String toString() {
		return "EMailCommandHandler [to=" + to + ", cc=" + cc + ", cci=" + cci
				+ ", subject=" + subject + ", body=" + body + "]";
	}

}
