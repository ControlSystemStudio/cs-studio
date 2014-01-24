/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.notifier.model.IActionHandler;

/** Common behavior for mail commands handlers */
public abstract class AbstractCommandHandler implements IActionHandler {

	protected final static String DELIMITERS = ",;";
	final protected static Pattern NLSPattern = Pattern.compile("\\{\\ *[01]\\ *\\}");
	
	protected enum ParamType {
		To("to", 0), Cc("cc", 1), Cci("cci", 2), Bcc("bcc", 3), Subject("subject", 4), Body("body", 5);

		private final int type;
		private final String name;

		ParamType(String name, int type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}
		public int getType() {
			return type;
		}

		public static ParamType valueOf(int type) {
			switch (type) {
			case 0: return To;
			case 1: return Cc;
			case 2: return Cci;
			case 3: return Bcc;
			case 4: return Subject;
			case 5: return Body;
			}
			return null;
		}
	}
	
	private final String scheme;
	private final String details;
	
	public AbstractCommandHandler(String details, String scheme) {
		this.details = details;
		this.scheme = scheme;
	}
	
	public void parse() throws Exception {
		parseDataType(details, null);
	}

	protected abstract void handleParameter(String data, ParamType type) throws Exception;
	
	// Recursive method to retrieve pattern parameters
	private void parseDataType(String data, ParamType type) throws Exception {
		if (data == null || "".equals(data))
			return;

		Matcher mainMatcher = Pattern.compile("^" + scheme + ":(.*)$").matcher(data);
		if (type == null && mainMatcher.matches())
			parseDataType(mainMatcher.group(1), ParamType.To);

		boolean found = false;
		for (ParamType param : ParamType.values()) {
			Matcher matcher = Pattern.compile("^(.*)(?:\\?|&)" + param.getName() + "=(.*)$").matcher(data);
			if (matcher.matches()) {
				found = true;
				parseDataType(matcher.group(1), type);
				parseDataType(matcher.group(2), param);
			}
		}
		// no pattern found => final data
		if (!found) handleParameter(data, type);
	}
	
	protected void validateNSF(String data) throws Exception {
		String dataCopy = new String(data);
		int beginIndex = 0, endIndex = 0;
		while (true) {
			beginIndex = dataCopy.indexOf('{');
			if (beginIndex == -1) break;
			endIndex = dataCopy.indexOf('}');
			if (endIndex == -1)
				throw new Exception("Invalid field: "
						+ dataCopy.substring(beginIndex));
			String nls = dataCopy.substring(beginIndex, endIndex + 1);
			Matcher nlsMatcher = NLSPattern.matcher(nls);
			if (!nlsMatcher.matches())
				throw new Exception("Invalid field: " + nls);
			dataCopy = dataCopy.substring(endIndex + 1);
		}
	}
}
